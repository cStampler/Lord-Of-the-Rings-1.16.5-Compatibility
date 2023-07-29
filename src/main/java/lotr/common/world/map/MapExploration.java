package lotr.common.world.map;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.*;

import lotr.common.LOTRLog;
import lotr.common.data.DataUtil;
import lotr.common.network.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.MathHelper;

public class MapExploration {
	private int savedMapWidth;
	private int savedMapHeight;
	private int savedMapOriginX;
	private int savedMapOriginZ;
	private BitSet explorationIndex;

	private boolean doesSavedMapMatchCurrent(MapSettings currentMap) {
		return currentMap.getWidth() == savedMapWidth && currentMap.getHeight() == savedMapHeight && currentMap.getOriginX() == savedMapOriginX && currentMap.getOriginZ() == savedMapOriginZ;
	}

	private int findDistanceFromExplored(int gridWidth, int gridHeight, int gridX, int gridZ, int maxSearchRange) {
		for (int range = 1; range <= maxSearchRange; ++range) {
			for (int dx = -range; dx <= range; ++dx) {
				for (int dz = -range; dz <= range; ++dz) {
					if (Math.abs(dx) == range || Math.abs(dz) == range) {
						int nearGridX = gridX + dx;
						int nearGridZ = gridZ + dz;
						if (nearGridX >= 0 && nearGridX < gridWidth && nearGridZ >= 0 && nearGridZ < gridHeight) {
							int nearGridIndex = nearGridZ * gridWidth + nearGridX;
							if (explorationIndex.get(nearGridIndex * 1 + 0)) {
								return range;
							}
						}
					}
				}
			}
		}

		return maxSearchRange + 1;
	}

	private Optional getBitForGridTile(int mapX, int mapZ, int bitOffset) {
		return explorationIndex == null ? Optional.empty() : getBitIndexForTileAtMapCoords(mapX, mapZ).map(bitIndex -> explorationIndex.get((int) bitIndex + bitOffset));
	}

	private Optional getBitIndexForTileAtGridCoords(int gridX, int gridZ) {
		int gridWidth = computeGridSizeForMapDimension(savedMapWidth);
		int gridHeight = computeGridSizeForMapDimension(savedMapHeight);
		if (gridX >= 0 && gridX < gridWidth && gridZ >= 0 && gridZ < gridHeight) {
			int gridIndex = gridZ * gridWidth + gridX;
			return Optional.of(gridIndex * 1);
		}
		return Optional.empty();
	}

	private Optional getBitIndexForTileAtMapCoords(int mapX, int mapZ) {
		int gridX = computeGridCoordinateForMapCoordinate(mapX);
		int gridZ = computeGridCoordinateForMapCoordinate(mapZ);
		return getBitIndexForTileAtGridCoords(gridX, gridZ);
	}

	public boolean initialiseIfEmptyOrChanged(ServerPlayerEntity player, MapSettings currentMap) {
		boolean needInit = false;
		if (explorationIndex == null) {
			needInit = true;
		} else if (!doesSavedMapMatchCurrent(currentMap)) {
			needInit = true;
			LOTRLog.info("Re-initialising %s's saved map exploration grid, because the loaded map has since changed scale or origin!", player.getName());
		}

		if (needInit) {
			explorationIndex = new BitSet();
			saveMapValues(currentMap);
			unlockNearbyAreas(player, currentMap, 192, false);
			LOTRPacketHandler.sendTo(new SPacketMapExplorationFull(this), player);
		}

		return needInit;
	}

	public boolean isExplored(int mapX, int mapZ) {
		return (Boolean) getBitForGridTile(mapX, mapZ, 0).orElse(false);
	}

	private boolean isTileAtGridCoordsExplored(int gridX, int gridZ) {
		return (Boolean) getBitIndexForTileAtGridCoords(gridX, gridZ).map(bitIndex -> explorationIndex.get((int) bitIndex + 0)).orElse(false);
	}

	private boolean isWithinGridBoundsAndNotExplored(int mapX, int mapZ) {
		return (Boolean) getBitForGridTile(mapX, mapZ, 0).map(value -> !((boolean) value)).orElse(false);
	}

	public void load(CompoundNBT nbt, UUID playerUuid) {
		savedMapWidth = nbt.getInt("MapWidth");
		savedMapHeight = nbt.getInt("MapHeight");
		savedMapOriginX = nbt.getInt("MapOriginX");
		savedMapOriginZ = nbt.getInt("MapOriginZ");
		if (nbt.contains("ExplorationIndex", 12)) {
			long[] backingArray = nbt.getLongArray("ExplorationIndex");
			backingArray = checkBackingArrayNotTooLong(backingArray, savedMapWidth, savedMapHeight, playerUuid);
			explorationIndex = BitSet.valueOf(backingArray);
		} else {
			explorationIndex = null;
		}

	}

	public boolean onUpdate(ServerPlayerEntity player, MapSettings currentMap) {
		return unlockNearbyAreas(player, currentMap, 144, true);
	}

	public void read(PacketBuffer buf) {
		savedMapWidth = buf.readVarInt();
		savedMapHeight = buf.readVarInt();
		savedMapOriginX = buf.readVarInt();
		savedMapOriginZ = buf.readVarInt();
		long[] backingArray = (long[]) DataUtil.readNullableFromBuffer(buf, () -> buf.readLongArray((long[]) null));
		explorationIndex = Optional.ofNullable(backingArray).map(BitSet::valueOf).orElse((BitSet) null);
	}

	public void receiveSingleTileUpdateFromServer(int mapX, int mapZ, BitSet tileBits) {
		if (explorationIndex == null) {
			LOTRLog.warn("Received an exploration tile update from the server, but the backing array is null! This shouldn't happen");
		} else {
			Optional bitIndex = getBitIndexForTileAtMapCoords(mapX, mapZ);
			if (bitIndex.isPresent()) {
				for (int i = 0; i < 1; ++i) {
					explorationIndex.set((Integer) bitIndex.get() + i, tileBits.get(i));
				}
			} else {
				LOTRLog.warn("Received an exploration tile update from the server (at map pixel %d, %d) but the tile location was out of bounds of the backing array! This shouldn't happen", mapX, mapZ);
			}

		}
	}

	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt("MapWidth", savedMapWidth);
		nbt.putInt("MapHeight", savedMapHeight);
		nbt.putInt("MapOriginX", savedMapOriginX);
		nbt.putInt("MapOriginZ", savedMapOriginZ);
		if (explorationIndex != null) {
			nbt.putLongArray("ExplorationIndex", explorationIndex.toLongArray());
		}

		return nbt;
	}

	private void saveMapValues(MapSettings map) {
		savedMapWidth = map.getWidth();
		savedMapHeight = map.getHeight();
		savedMapOriginX = map.getOriginX();
		savedMapOriginZ = map.getOriginZ();
	}

	private void sendSingleTileUpdate(ServerPlayerEntity player, int mapX, int mapZ) {
		Optional optBitIndex = getBitIndexForTileAtMapCoords(mapX, mapZ);
		if (!optBitIndex.isPresent()) {
			LOTRLog.warn("Tried to send an exploration tile update (map pixel %d, %d) to player %s, but the corresponding bit index was calculated as null! This shouldn't happen", mapX, mapZ, player.getName().getString());
		} else {
			int bitIndex = (Integer) optBitIndex.get();
			BitSet tileBits = explorationIndex.get(bitIndex, bitIndex + 1);
			LOTRPacketHandler.sendTo(new SPacketMapExplorationTile(mapX, mapZ, tileBits), player);
		}

	}

	private void setExplored(int mapX, int mapZ, boolean explored) {
		updateBitForGridTile(mapX, mapZ, 0, explored);
	}

	public Stream streamTilesForRendering(double mapXMin, double mapXMax, double mapZMin, double mapZMax, IProfiler profiler) {
		if (explorationIndex == null) {
			return Stream.empty();
		}
		int gridWidth = computeGridSizeForMapDimension(savedMapWidth);
		int gridHeight = computeGridSizeForMapDimension(savedMapHeight);
		int textureEdgeWidth = 8;
		int gridXMin = computeGridCoordinateForFractionalMapCoordinate(mapXMin - textureEdgeWidth);
		int gridXMax = computeGridCoordinateForFractionalMapCoordinate(mapXMax + textureEdgeWidth);
		int gridZMin = computeGridCoordinateForFractionalMapCoordinate(mapZMin - textureEdgeWidth);
		int gridZMax = computeGridCoordinateForFractionalMapCoordinate(mapZMax + textureEdgeWidth);
		return IntStream.rangeClosed(gridXMin, gridXMax).mapToObj(gridX -> IntStream.rangeClosed(gridZMin, gridZMax).filter(gridZ -> !isTileAtGridCoordsExplored(gridX, gridZ)).mapToObj(gridZ -> {
			int mapX = computeMapCoordinateForGridCoordinate(gridX);
			int mapZ = computeMapCoordinateForGridCoordinate(gridZ);
			profiler.push("findDistanceFromExplored");
			int distanceFromExplored = findDistanceFromExplored(gridWidth, gridHeight, gridX, gridZ, 2);
			profiler.pop();
			return new MapExplorationTile(mapX - textureEdgeWidth, mapZ - textureEdgeWidth, 64, distanceFromExplored);
		})).flatMap(stream -> stream);
	}

	private boolean unlockNearbyAreas(ServerPlayerEntity player, MapSettings currentMap, int discoveryRange, boolean sendUpdatePackets) {
		int playerMapX = currentMap.worldToMapX(MathHelper.floor(player.getX()));
		int playerMapZ = currentMap.worldToMapZ(MathHelper.floor(player.getZ()));
		boolean updatedAny = false;
		int incr = 48;

		for (int dx = -discoveryRange; dx < discoveryRange; dx += incr) {
			for (int dz = -discoveryRange; dz < discoveryRange; dz += incr) {
				if (dx >= -discoveryRange + incr && dx < discoveryRange - incr || dz >= -discoveryRange + incr && dz < discoveryRange - incr) {
					int mapX = playerMapX + dx;
					int mapZ = playerMapZ + dz;
					if (isWithinGridBoundsAndNotExplored(mapX, mapZ)) {
						setExplored(mapX, mapZ, true);
						if (sendUpdatePackets) {
							sendSingleTileUpdate(player, mapX, mapZ);
						}

						updatedAny = true;
					}
				}
			}
		}

		return updatedAny;
	}

	private void updateBitForGridTile(int mapX, int mapZ, int bitOffset, boolean value) {
		if (explorationIndex == null) {
			throw new IllegalStateException("Tried to update the map exploration grid when the backing bitset was null!");
		}
		getBitIndexForTileAtMapCoords(mapX, mapZ).ifPresent(bitIndex -> {
			explorationIndex.set((int) bitIndex + bitOffset, value);
		});
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(savedMapWidth);
		buf.writeVarInt(savedMapHeight);
		buf.writeVarInt(savedMapOriginX);
		buf.writeVarInt(savedMapOriginZ);
		long[] backingArray = Optional.ofNullable(explorationIndex).map(BitSet::toLongArray).orElse((long[]) null);
		DataUtil.writeNullableToBuffer(buf, backingArray, (BiFunction) (h1, h2) -> ((PacketBuffer) h1).writeLongArray((long[]) h2));
	}

	private static long[] checkBackingArrayNotTooLong(long[] backingArray, int savedMapWidth, int savedMapHeight, UUID playerUuid) {
		int gridWidth = computeGridSizeForMapDimension(savedMapWidth);
		int gridHeight = computeGridSizeForMapDimension(savedMapHeight);
		int numTiles = gridWidth * gridHeight;
		int numBits = numTiles * 1;
		int maxWordsRequired = MathHelper.ceil(numBits / 64.0D);
		if (backingArray.length > maxWordsRequired) {
			long[] truncatedArray = new long[maxWordsRequired];
			System.arraycopy(backingArray, 0, truncatedArray, 0, maxWordsRequired);
			LOTRLog.warn("Map exploration playerdata for %s loaded a backing array which is longer than expected for the saved map dimensions (loaded array length is %d, but map size %dx%d => %d exploration tiles => %d long words required) - so truncated to an array of length %d", playerUuid, backingArray.length, savedMapWidth, savedMapHeight, numTiles, maxWordsRequired, truncatedArray.length);
			return truncatedArray;
		}
		return backingArray;
	}

	private static int computeGridCoordinateForFractionalMapCoordinate(double mapCoord) {
		return computeGridCoordinateForMapCoordinate(MathHelper.floor(mapCoord));
	}

	private static int computeGridCoordinateForMapCoordinate(int mapCoord) {
		return MathHelper.floor(mapCoord / 48.0D) + 8;
	}

	private static int computeGridSizeForMapDimension(int mapDimension) {
		return MathHelper.ceil(mapDimension / 48.0D) + 16;
	}

	private static int computeMapCoordinateForGridCoordinate(int gridCoord) {
		return (gridCoord - 8) * 48;
	}
}
