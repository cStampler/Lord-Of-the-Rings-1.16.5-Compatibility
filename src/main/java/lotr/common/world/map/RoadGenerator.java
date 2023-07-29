package lotr.common.world.map;

import java.util.Random;
import java.util.function.UnaryOperator;

import com.google.common.math.IntMath;

import lotr.common.world.biome.LOTRBiomeWrapper;
import lotr.common.world.gen.feature.LOTRFeatures;
import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;

public class RoadGenerator {
	public static final int ROAD_DEPTH = 4;

	private boolean determineIsSlab(IChunk chunk, Mutable roadPos) {
		ChunkPos pos = chunk.getPos();
		int chunkXStart = pos.getMinBlockX();
		int chunkXEnd = pos.getMaxBlockX();
		int chunkZStart = pos.getMinBlockZ();
		int chunkZEnd = pos.getMaxBlockZ();
		int x = roadPos.getX();
		int z = roadPos.getZ();
		int y = roadPos.getY();
		int totalChecked = 0;
		int nearbySolid = 0;
		Mutable checkPos = new Mutable();

		for (int checkX = x - 1; checkX <= x + 1; ++checkX) {
			for (int checkZ = z - 1; checkZ <= z + 1; ++checkZ) {
				if ((checkX != x || checkZ != z) && checkX >= chunkXStart && checkX <= chunkXEnd && checkZ >= chunkZStart && checkZ <= chunkZEnd) {
					checkPos.set(checkX, y, checkZ);
					BlockState checkState = chunk.getBlockState(checkPos);
					if (checkState.isSolidRender(chunk, checkPos) || checkState.getBlock() instanceof SlabBlock) {
						++nearbySolid;
					}

					++totalChecked;
				}
			}
		}

		if (totalChecked == 0) {
			return false;
		}
		float avgSolid = (float) nearbySolid / (float) totalChecked;
		return avgSolid < 1.0D;
	}

	public boolean generateRoad(IWorld world, IChunk chunk, Random rand, LOTRBiomeWrapper biomeWrapper, BlockPos topPos, int seaLevel) {
		MapSettings mapSettings = MapSettingsManager.sidedInstance(world).getCurrentLoadedMap();
		RoadBlockProvider roadProvider = biomeWrapper.getRoadBlockProvider();
		BridgeBlockProvider bridgeProvider = biomeWrapper.getBridgeBlockProvider();
		int x = topPos.getX();
		int z = topPos.getZ();
		if (!mapSettings.getRoadPointCache().isRoadAt(x, z)) {
			return false;
		}
		int roadTop = 0;
		int bridgeBase = 0;
		boolean bridge = false;
		boolean bridgeSlab = false;
		BlockState topState = chunk.getBlockState(topPos);
		Mutable edgePos;
		int ySupport;
		if (topState.isSolidRender(chunk, topPos)) {
			bridge = false;
			roadTop = topPos.getY();
		} else if (topState.getMaterial().isLiquid()) {
			roadTop = topPos.getY() + 1;
			bridgeBase = roadTop;
			int maxBridgeTop = topPos.getY() + 6;
			float bridgeHeight = 0.0F;

			for (edgePos = new Mutable().set(topPos); edgePos.getY() > 0; bridgeHeight += 0.5F) {
				edgePos.move(Direction.DOWN);
				BlockState belowState = chunk.getBlockState(edgePos);
				if (!belowState.getMaterial().isLiquid()) {
					break;
				}
			}

			ySupport = (int) Math.floor(bridgeHeight);
			roadTop += ySupport;
			roadTop = Math.min(roadTop, maxBridgeTop);
			if (roadTop >= maxBridgeTop) {
				bridgeSlab = true;
			} else {
				float bridgeHeightR = bridgeHeight - ySupport;
				if (bridgeHeightR < 0.5F) {
					bridgeSlab = true;
				}
			}

			bridge = true;
		}

		boolean pillar;
		Mutable roadPos;
		BlockState block;
		if (bridge) {
			boolean fence = isFenceAt(mapSettings, x, z);
			if (fence) {
				pillar = isPillarAt(mapSettings, x, z);
				if (pillar) {
					int pillarTop = roadTop + 4;

					for (roadPos = new Mutable(x, pillarTop, z); roadPos.getY() > 0; roadPos.move(Direction.DOWN)) {
						block = chunk.getBlockState(roadPos);
						if (block.isSolidRender(chunk, roadPos)) {
							break;
						}

						if (roadPos.getY() >= pillarTop) {
							setBlock(chunk, roadPos, bridgeProvider.getFenceBlock(rand, roadPos));
						} else if (roadPos.getY() >= pillarTop - 1) {
							setBlock(chunk, roadPos, bridgeProvider.getMainBlock(rand, roadPos));
						} else {
							setBlockAndUpdateAdjacent(chunk, roadPos, bridgeProvider.getBeamBlock(rand, roadPos));
						}
					}
				} else {
					edgePos = new Mutable(x, roadTop, z);
					setBlock(chunk, edgePos, bridgeProvider.getBeamBlock(rand, edgePos));
					edgePos.move(Direction.UP);
					setBlockAndUpdateAdjacent(chunk, edgePos, bridgeProvider.getFenceBlock(rand, edgePos));
					if (roadTop > bridgeBase) {
						edgePos.setY(roadTop - 1);
						setBlockAndUpdateAdjacent(chunk, edgePos, bridgeProvider.getFenceBlock(rand, edgePos));
					}

					ySupport = bridgeBase + 2;
					if (roadTop - 1 > ySupport) {
						edgePos.setY(ySupport);
						setBlockAndUpdateAdjacent(chunk, edgePos, bridgeProvider.getFenceBlock(rand, edgePos));
					}
				}
			} else {
				Mutable bridgePos = new Mutable(x, roadTop, z);
				if (bridgeSlab) {
					setBlock(chunk, bridgePos, bridgeProvider.getMainSlabBlock(rand, bridgePos));
					if (roadTop > bridgeBase) {
						bridgePos.move(Direction.DOWN);
						setBlock(chunk, bridgePos, bridgeProvider.getMainSlabBlockInverted(rand, bridgePos));
					}
				} else {
					setBlock(chunk, bridgePos, bridgeProvider.getMainBlock(rand, bridgePos));
				}
			}
		} else {
			float repair = roadProvider.getRepair();
			pillar = isRoadEdge(mapSettings, x, z);
			boolean isHedge = roadProvider.hasHedge() && pillar;
			roadPos = new Mutable(x, roadTop, z);
			if (isHedge) {
				roadPos.move(Direction.UP);
				if (rand.nextFloat() < roadProvider.getHedgeDensity()) {
					block = roadProvider.getHedgeBlock(rand, roadPos);
					setBlock(chunk, roadPos, block);
					setGrassToDirtBelowIfPlacedBlockSolid(chunk, roadPos, block);
				}
			} else {
				boolean isDistinctEdge = roadProvider.hasDistinctEdge() && pillar;
				boolean elevatedEdge = isDistinctEdge && rand.nextFloat() < repair;

				for (boolean isTop = true; roadPos.getY() > roadTop - 4 && roadPos.getY() > 0; isTop = false) {
					float repairHere = repair;
					if (isDistinctEdge && isTop) {
						repairHere = repair * repair;
					}

					if (rand.nextFloat() < repairHere) {
						boolean isSlab = false;
						if (isTop && roadPos.getY() >= seaLevel + 1) {
							isSlab = determineIsSlab(chunk, roadPos);
						}

						if (isTop && elevatedEdge) {
							UnaryOperator moveUpByHalfBlock = inputIsSlab -> {
								if ((boolean) inputIsSlab) {
									inputIsSlab = false;
								} else {
									inputIsSlab = true;
									roadPos.move(Direction.UP);
								}

								return inputIsSlab;
							};
							isSlab = (Boolean) moveUpByHalfBlock.apply(isSlab);
							if (rand.nextInt(18) == 0) {
								isSlab = (Boolean) moveUpByHalfBlock.apply(isSlab);
							}
						}

						RoadBlockProvider roadToUse = isDistinctEdge ? roadProvider.getEdgeProvider() : roadProvider;
						BlockState roadBlockState = isSlab ? roadToUse.getTopSlabBlock(rand, roadPos) : isTop ? roadToUse.getTopBlock(rand, roadPos) : roadToUse.getFillerBlock(rand, roadPos);
						if (roadToUse.requiresPostProcessing()) {
							setBlockAndUpdateAdjacent(chunk, roadPos, roadBlockState);
						} else {
							setBlock(chunk, roadPos, roadBlockState);
						}

						if (roadPos.getY() > roadTop) {
							setGrassToDirtBelowIfPlacedBlockSolid(chunk, roadPos, roadBlockState);
						}
					}

					roadPos.move(Direction.DOWN);
				}
			}
		}

		return true;
	}

	private boolean isBridgeEdgePillar(MapSettings mapSettings, int i, int k) {
		return mapSettings.getRoadPointCache().isRoadAt(i, k) && isFenceAt(mapSettings, i, k) && isPillarAt(mapSettings, i, k);
	}

	private boolean isFenceAt(MapSettings mapSettings, int i, int k) {
		return isRoadEdge(mapSettings, i, k);
	}

	private boolean isPillarAt(MapSettings mapSettings, int i, int k) {
		int pRange = 8;
		int xmod = IntMath.mod(i, pRange);
		int zmod = IntMath.mod(k, pRange);
		if (IntMath.mod(xmod + zmod, pRange) == 0) {
			return !isBridgeEdgePillar(mapSettings, i + 1, k - 1) && !isBridgeEdgePillar(mapSettings, i + 1, k + 1);
		}
		return false;
	}

	private boolean isRoadEdge(MapSettings mapSettings, int i, int k) {
		for (int i1 = -1; i1 <= 1; ++i1) {
			for (int k1 = -1; k1 <= 1; ++k1) {
				if ((i1 != 0 || k1 != 0) && !mapSettings.getRoadPointCache().isRoadAt(i + i1, k + k1)) {
					return true;
				}
			}
		}

		return false;
	}

	private void setBlock(IChunk chunk, BlockPos pos, BlockState state) {
		chunk.setBlockState(pos, state, false);
	}

	private void setBlockAndUpdateAdjacent(IChunk chunk, BlockPos pos, BlockState state) {
		setBlock(chunk, pos, state);
		chunk.markPosForPostprocessing(pos);
	}

	private void setGrassToDirtBelowIfPlacedBlockSolid(IChunk chunk, BlockPos pos, BlockState placedState) {
		if (placedState.isFaceSturdy(chunk, pos, Direction.DOWN)) {
			LOTRFeatures.setGrassToDirtBelowDuringChunkGen(chunk, pos);
		}

	}
}
