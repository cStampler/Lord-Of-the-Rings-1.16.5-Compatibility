package lotr.common.world.map;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.*;

import javax.annotation.Nullable;

import lotr.common.LOTRLog;
import lotr.common.block.*;
import lotr.common.data.LOTRLevelData;
import lotr.common.init.*;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import lotr.common.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.Property;
import net.minecraft.state.properties.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;

public class CustomWaypointStructureHandler {
	public static final CustomWaypointStructureHandler INSTANCE = new CustomWaypointStructureHandler();
	private Map playersClickedOnBlocksToCreate = new HashMap();
	private Map playersSentProtectionMessageTimes = new HashMap();

	private CustomWaypointStructureHandler() {
	}

	public void adoptWaypointStructure(PlayerEntity player, CustomWaypoint waypoint) {
		World world = player.level;
		BlockPos waypointPos = waypoint.getPosition();
		spawnParticles(world, waypointPos);
	}

	public boolean checkCompletedWaypointHasMarkerAndHandleIfNot(World world, AbstractCustomWaypoint waypoint, PlayerEntity player) {
		BlockPos waypointPos = waypoint.getPosition();
		CustomWaypointMarkerTileEntity marker = getAdjacentWaypointMarker(world, waypointPos, waypoint);
		if (marker == null) {
			waypoint.removeFromPlayerData(player);
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.missing", waypoint.getDisplayName()).withStyle(TextFormatting.RED));
			return false;
		}
		return true;
	}

	public void clearPlayerClickedOnBlockToCreate(PlayerEntity player) {
		playersClickedOnBlocksToCreate.remove(player.getUUID());
	}

	public void completeStructureWithCreatedWaypoint(PlayerEntity player, CustomWaypoint waypoint) {
		World world = player.level;
		BlockPos waypointPos = waypoint.getPosition();
		Optional frameOpt = getValidMapOnFocalPoint(world, waypointPos, text -> {
		});
		if (frameOpt.isPresent()) {
			ItemFrameEntity frame = (ItemFrameEntity) frameOpt.get();
			BlockPos offsetPos = frame.getPos();
			Direction frameDir = frame.getDirection();
			world.setBlockAndUpdate(offsetPos, ((Block) LOTRBlocks.CUSTOM_WAYPOINT_MARKER.get()).defaultBlockState().setValue(CustomWaypointMarkerBlock.FACING, frameDir));
			TileEntity te = world.getBlockEntity(offsetPos);
			if (te instanceof CustomWaypointMarkerTileEntity) {
				CustomWaypointMarkerTileEntity marker = (CustomWaypointMarkerTileEntity) te;
				marker.absorbItemFrame(frame);
				marker.setWaypointReference(waypoint);
				spawnParticles(world, waypointPos);
			} else {
				LOTRLog.error("Player %s created a custom waypoint at (%s) - but somehow the tile entity was not created!", UsernameHelper.getRawUsername(player), waypointPos);
			}
		} else {
			LOTRLog.warn("Player %s created a custom waypoint at (%s) where a valid item frame should exist, but didn't!", UsernameHelper.getRawUsername(player), waypointPos);
		}

	}

	private boolean destroyBlockWithDrops(World world, BlockPos pos, PlayerEntity player) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity te = world.getBlockEntity(pos);
		boolean canHarvest = true;
		boolean removed = state.removedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
		if (removed) {
			state.getBlock().destroy(world, pos, state);
			block.playerDestroy(world, player, pos, state, te, ItemStack.EMPTY);
		}

		return removed;
	}

	public boolean destroyCustomWaypointMarkerAndRemoveFromPlayerData(World world, CustomWaypoint waypoint, PlayerEntity player, boolean destroyWholeStructure) {
		BlockPos waypointPos = waypoint.getPosition();
		CustomWaypointMarkerTileEntity marker = getAdjacentWaypointMarker(world, waypointPos, waypoint);
		if (marker == null) {
			LOTRLog.warn("Tried to destroy a custom waypoint %s for player %s at (%s) but no matching marker block was found", waypoint.getRawName(), UsernameHelper.getRawUsername(player), waypointPos);
			return false;
		}
		if (!LOTRLevelData.sidedInstance(world).getData(player).getFastTravelData().removeCustomWaypoint(world, waypoint)) {
			return false;
		}
		world.setBlockAndUpdate(marker.getBlockPos(), Blocks.AIR.defaultBlockState());
		if (destroyWholeStructure) {
			Stream.concat(streamPositionsInBoundingBox(waypointPos).filter(pos -> !world.isEmptyBlock((BlockPos) pos)), streamPositionsInSolidBase(waypointPos).filter(pos -> (world.random.nextInt(4) == 0))).forEach(pos -> {
				destroyBlockWithDrops(world, (BlockPos) pos, player);
			});
		}

		return true;
	}

	private boolean doesMapIncludePosition(World world, MapData mapData, BlockPos pos) {
		if (!LOTRDimensions.isDimension(world, mapData.dimension)) {
			return false;
		}
		int scaleFactor = 1 << mapData.scale;
		int halfMapWidth = 64;
		halfMapWidth = halfMapWidth * scaleFactor;
		return Math.abs(pos.getX() - mapData.x) <= halfMapWidth && Math.abs(pos.getZ() - mapData.z) <= halfMapWidth;
	}

	@Nullable
	public BlockPos findRandomTravelPositionForCompletedWaypoint(World world, AbstractCustomWaypoint waypoint, PlayerEntity player) {
		BlockPos waypointPos = waypoint.getPosition();
		if (!checkCompletedWaypointHasMarkerAndHandleIfNot(world, waypoint, player)) {
			LOTRLog.warn("Player %s tried to travel to a custom waypoint (%s, %s) that isn't a complete structure!", UsernameHelper.getRawUsername(player), waypoint.getRawName(), waypointPos);
			return null;
		}
		List safePositions = (List) streamPositionsInBoundingBox(waypointPos).filter(pos -> {
			BlockPos belowPos = ((BlockPos) pos).below();
			return world.getBlockState(((BlockPos) pos).below()).isFaceSturdy(world, belowPos, Direction.UP) && isEmptyBlockForBounds(world, (BlockPos) pos) && isEmptyBlockForBounds(world, ((BlockPos) pos).above());
		}).collect(Collectors.toList());
		if (safePositions.isEmpty()) {
			LOTRLog.warn("Player %s tried to travel to a custom waypoint (%s, %s) but couldn't find any safe positions!", UsernameHelper.getRawUsername(player), waypoint.getRawName(), waypointPos);
			return waypointPos;
		}
		return (BlockPos) safePositions.get(world.random.nextInt(safePositions.size()));
	}

	public CustomWaypointMarkerTileEntity getAdjacentWaypointMarker(World world, BlockPos focalPos, @Nullable AbstractCustomWaypoint waypointToValidate) {
		Iterator var4 = Plane.HORIZONTAL.iterator();

		CustomWaypointMarkerTileEntity marker;
		do {
			TileEntity te;
			do {
				BlockPos offsetPos;
				BlockState state;
				do {
					if (!var4.hasNext()) {
						return null;
					}

					Direction dir = (Direction) var4.next();
					offsetPos = focalPos.relative(dir);
					state = world.getBlockState(offsetPos);
				} while (state.getBlock() != LOTRBlocks.CUSTOM_WAYPOINT_MARKER.get());

				te = world.getBlockEntity(offsetPos);
			} while (!(te instanceof CustomWaypointMarkerTileEntity));

			marker = (CustomWaypointMarkerTileEntity) te;
		} while (waypointToValidate != null && !marker.matchesWaypointReference(waypointToValidate));

		return marker;
	}

	private MutableBoundingBox getBoundingBoxForProtection(BlockPos focalPos) {
		MutableBoundingBox bb = getMainBoundingBox(focalPos);
		bb.expand(getSolidBaseBoundingBox(focalPos));
		++bb.y1;
		return bb;
	}

	private MutableBoundingBox getMainBoundingBox(BlockPos focalPos) {
		int x = focalPos.getX();
		int y = focalPos.getY();
		int z = focalPos.getZ();
		return new MutableBoundingBox(x - 2, y - 2, z - 2, x + 2, y + 2, z + 2);
	}

	private MapData getMapDataFromFrame(ItemFrameEntity frame) {
		return FilledMapItem.getSavedData(frame.getItem(), frame.level);
	}

	public BlockPos getPlayerClickedOnBlockToCreate(PlayerEntity player) {
		return (BlockPos) playersClickedOnBlocksToCreate.get(player.getUUID());
	}

	private MutableBoundingBox getSolidBaseBoundingBox(BlockPos focalPos) {
		int baseY = focalPos.getY() - 3;
		int x = focalPos.getX();
		int z = focalPos.getZ();
		return new MutableBoundingBox(x - 2, baseY, z - 2, x + 2, baseY, z + 2);
	}

	private Optional getValidMapOnFocalPoint(World world, BlockPos focalPos, Consumer messageCallback) {
		List attachedFrames = world.getEntitiesOfClass(ItemFrameEntity.class, new AxisAlignedBB(focalPos).inflate(1.0D), frame -> getItemFrameSupportPos(frame).equals(focalPos));
		if (attachedFrames.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.frame"));
			return Optional.empty();
		}
		attachedFrames = (List) attachedFrames.stream().filter(frame -> (((ItemFrameEntity) frame).getItem().getItem() == Items.FILLED_MAP)).collect(Collectors.toList());
		if (attachedFrames.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.map.required"));
			return Optional.empty();
		}
		attachedFrames = (List) attachedFrames.stream().filter(frame -> doesMapIncludePosition(world, getMapDataFromFrame((ItemFrameEntity) frame), focalPos)).collect(Collectors.toList());
		if (attachedFrames.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.map.wrongArea"));
			return Optional.empty();
		}
		attachedFrames = (List) attachedFrames.stream().filter(frame -> isMapScaleLargeEnough(getMapDataFromFrame((ItemFrameEntity) frame))).collect(Collectors.toList());
		if (attachedFrames.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.map.tooSmall"));
			return Optional.empty();
		}
		attachedFrames = (List) attachedFrames.stream().filter(frame -> isMapSufficientlyExplored(getMapDataFromFrame((ItemFrameEntity) frame))).collect(Collectors.toList());
		if (attachedFrames.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.map.notExplored"));
			return Optional.empty();
		}
		return Optional.of(attachedFrames.get(0));
	}

	public boolean hasAdjacentWaypointMarker(World world, BlockPos focalPos) {
		return getAdjacentWaypointMarker(world, focalPos, (AbstractCustomWaypoint) null) != null;
	}

	private boolean hasValidMapOnFocalPoint(World world, BlockPos focalPos, Consumer messageCallback) {
		return getValidMapOnFocalPoint(world, focalPos, messageCallback).isPresent();
	}

	public boolean isCompletedWaypointStillValidStructure(World world, BlockPos focalPos) {
		return isFocalPointOfValidStructure(world, focalPos, text -> {
		});
	}

	private boolean isCorrectDimension(World world) {
		return LOTRDimensions.isDimension(world, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY);
	}

	private boolean isEmptyBlockForBounds(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.getFluidState().getType() == Fluids.EMPTY && state.getBlockSupportShape(world, pos).isEmpty() && !(state.getBlock() instanceof FireBlock);
	}

	public boolean isFocalPoint(World world, BlockPos focalPos) {
		return isCorrectDimension(world) ? isValidCentrepiece(world, focalPos) : false;
	}

	public boolean isFocalPointOfCompletableStructure(ServerWorld world, BlockPos focalPos) {
		return this.isFocalPointOfCompletableStructure(world, focalPos, text -> {
		});
	}

	public boolean isFocalPointOfCompletableStructure(ServerWorld world, BlockPos focalPos, Consumer messageCallback) {
		if (isTooCloseToExistingCustomWaypoint(world, focalPos, true)) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.tooClose.otherCustomWaypoint"));
			return false;
		}
		if (isTooCloseToSpawnOrMapWaypoint(world, focalPos, messageCallback) || !isFocalPointOfValidStructure(world, focalPos, messageCallback)) {
			return false;
		}
		return hasValidMapOnFocalPoint(world, focalPos, messageCallback);
	}

	private boolean isFocalPointOfValidStructure(World world, BlockPos focalPos, Consumer messageCallback) {
		if (!isFocalPoint(world, focalPos)) {
			return false;
		}
		List boundsToCheckEmpty = (List) streamPositionsInBoundingBox(focalPos).collect(Collectors.toList());
		boundsToCheckEmpty.remove(focalPos);
		if (!testForBlock(world, focalPos.above(), boundsToCheckEmpty, (h1, h2) -> isValidPillar((World) h1, (BlockPos) h2)) || !testForBlock(world, focalPos.below(), boundsToCheckEmpty, (h1, h2) -> isValidPillar((World) h1, (BlockPos) h2)) || !testForBlock(world, focalPos.below(2), boundsToCheckEmpty, (h1, h2) -> isValidPillar((World) h1, (BlockPos) h2))) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.invalid.pillar"));
			return false;
		}
		if (!testForBlock(world, focalPos.above(2), boundsToCheckEmpty, (h1, h2) -> isValidTopLight((World) h1, (BlockPos) h2))) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.invalid.light"));
			return false;
		}
		BlockPos buttressCentre = focalPos.below(2);
		if (!testForBlock(world, buttressCentre.north(), boundsToCheckEmpty, testValidButtress(Direction.SOUTH)) || !testForBlock(world, buttressCentre.south(), boundsToCheckEmpty, testValidButtress(Direction.NORTH)) || !testForBlock(world, buttressCentre.west(), boundsToCheckEmpty, testValidButtress(Direction.EAST)) || !testForBlock(world, buttressCentre.east(), boundsToCheckEmpty, testValidButtress(Direction.WEST))) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.invalid.buttress"));
			return false;
		}
		BlockPos crownCentre = focalPos.above(1);
		if (!testForBlock(world, crownCentre.north(), boundsToCheckEmpty, testValidCrown(Direction.SOUTH)) || !testForBlock(world, crownCentre.south(), boundsToCheckEmpty, testValidCrown(Direction.NORTH)) || !testForBlock(world, crownCentre.west(), boundsToCheckEmpty, testValidCrown(Direction.EAST)) || !testForBlock(world, crownCentre.east(), boundsToCheckEmpty, testValidCrown(Direction.WEST))) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.invalid.crown"));
			return false;
		}
		boolean validBase = streamPositionsInSolidBase(focalPos).allMatch(pos -> isValidBase(world, (BlockPos) pos));
		if (!validBase) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.invalid.base"));
			return false;
		}
		List boundsNotEmpty = (List) boundsToCheckEmpty.stream().filter(pos -> !isEmptyBlockForBounds(world, (BlockPos) pos)).collect(Collectors.toList());
		if (!boundsNotEmpty.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.invalid.notEmpty", boundsNotEmpty.size()));
			return false;
		}
		return true;
	}

	private boolean isMapScaleLargeEnough(MapData mapData) {
		return mapData.scale >= 1;
	}

	private boolean isMapSufficientlyExplored(MapData mapData) {
		int counted = 0;
		int empty = 0;
		byte[] var4 = mapData.colors;
		int var5 = var4.length;

		for (int var6 = 0; var6 < var5; ++var6) {
			byte c = var4[var6];
			++counted;
			if (c == 0) {
				++empty;
			}
		}

		float emptyFraction = (float) empty / (float) counted;
		return emptyFraction < 0.05F;
	}

	public boolean isProtectedByWaypointStructure(World world, BlockPos pos) {
		return isTooCloseToExistingCustomWaypoint(world, pos, false);
	}

	public boolean isProtectedByWaypointStructure(World world, BlockPos pos, PlayerEntity player) {
		if (player.abilities.instabuild || !this.isProtectedByWaypointStructure(world, pos)) {
			return false;
		}
		long currentTime = System.currentTimeMillis();
		long lastMessagedTime = (Long) playersSentProtectionMessageTimes.getOrDefault(player.getUUID(), 0L);
		if (currentTime - lastMessagedTime >= 3000L) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.protected"));
			playersSentProtectionMessageTimes.put(player.getUUID(), currentTime);
		}

		if (player instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			serverPlayer.refreshContainer(serverPlayer.inventoryMenu);
		}

		return true;
	}

	private boolean isTooCloseToExistingCustomWaypoint(World world, BlockPos pos, boolean checkPosWithWaypointBounds) {
		MutableBoundingBox thisBb = checkPosWithWaypointBounds ? getBoundingBoxForProtection(pos) : new MutableBoundingBox(pos, pos);
		int maxWaypointBoundsRange = 2;
		int chunkX0 = thisBb.x0 - maxWaypointBoundsRange >> 4;
		int chunkX1 = thisBb.x1 + maxWaypointBoundsRange >> 4;
		int chunkZ0 = thisBb.z0 - maxWaypointBoundsRange >> 4;
		int chunkZ1 = thisBb.z1 + maxWaypointBoundsRange >> 4;

		for (int chunkX = chunkX0; chunkX <= chunkX1; ++chunkX) {
			for (int chunkZ = chunkZ0; chunkZ <= chunkZ1; ++chunkZ) {
				Chunk chunk = world.getChunk(chunkX, chunkZ);
				Iterator var13 = chunk.getBlockEntities().entrySet().iterator();

				while (var13.hasNext()) {
					Entry entry = (Entry) var13.next();
					BlockPos tePos = (BlockPos) entry.getKey();
					TileEntity te = (TileEntity) entry.getValue();
					if (te instanceof CustomWaypointMarkerTileEntity) {
						BlockState state = world.getBlockState(tePos);
						Direction markerFacing = state.getValue(CustomWaypointMarkerBlock.FACING);
						BlockPos markerFocalPos = tePos.relative(markerFacing.getOpposite());
						MutableBoundingBox existingWaypointBb = getBoundingBoxForProtection(markerFocalPos);
						if (existingWaypointBb.intersects(thisBb)) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	private boolean isTooCloseToSpawnOrMapWaypoint(ServerWorld world, BlockPos focalPos, Consumer messageCallback) {
		BlockPos spawnPoint = LOTRDimensions.getDimensionSpawnPoint(world);
		if (focalPos.closerThan(spawnPoint, 500.0D)) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.tooClose.spawn"));
			return true;
		}
		List mapWaypointsTooClose = (List) MapSettingsManager.sidedInstance(world).getCurrentLoadedMap().getWaypoints().stream().filter(wp -> {
			double dx = ((Waypoint) wp).getWorldX() - focalPos.getX();
			double dz = ((Waypoint) wp).getWorldZ() - focalPos.getZ();
			double dSq = dx * dx + dz * dz;
			return dSq < 40000.0D;
		}).collect(Collectors.toList());
		if (!mapWaypointsTooClose.isEmpty()) {
			messageCallback.accept(new TranslationTextComponent("chat.lotr.cwp.structure.tooClose.mapWaypoint", ((MapWaypoint) mapWaypointsTooClose.get(0)).getDisplayName()));
			return true;
		}
		return false;
	}

	private boolean isValidBase(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return isValidStoneOrSimilarMaterial(state) && state.isSolidRender(world, pos);
	}

	private boolean isValidCentrepiece(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.is(LOTRTags.Blocks.CUSTOM_WAYPOINT_CENTERPIECES);
	}

	private boolean isValidPillar(World world, BlockPos pos) {
		return isValidBase(world, pos);
	}

	private boolean isValidStoneOrSimilarMaterial(BlockState state) {
		Material material = state.getMaterial();
		if (material != Material.STONE && material != LOTRBlockMaterial.ICE_BRICK && material != LOTRBlockMaterial.SNOW_BRICK) {
			Block block = state.getBlock();
			return block == Blocks.BONE_BLOCK;
		}
		return true;
	}

	private boolean isValidTopLight(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getLightValue(world, pos) >= 8) {
			return true;
		}
		BlockPos abovePos = pos.above();
		BlockState aboveState = world.getBlockState(abovePos);
		return aboveState.getBlock() == state.getBlock() && aboveState.getLightValue(world, abovePos) >= 8;
	}

	public void setPlayerClickedOnBlockToCreate(PlayerEntity player, BlockPos pos) {
		playersClickedOnBlocksToCreate.put(player.getUUID(), pos.immutable());
	}

	private void spawnParticles(World world, BlockPos waypointPos) {
		if (world instanceof ServerWorld) {
			ServerWorld sWorld = (ServerWorld) world;
			streamPositionsInSolidBase(waypointPos).forEach(pos -> {
				BlockPos abovePos = ((BlockPos) pos).above();
				if (world.isEmptyBlock(abovePos)) {
					int count = 0;
					double speed = 0.12D;
					sWorld.sendParticles(ParticleTypes.POOF, abovePos.getX() + 0.5D, abovePos.getY() + 0.1D, abovePos.getZ() + 0.5D, count, 0.0D, 1.0D, 0.0D, speed);
				}

			});
		}

	}

	private Stream streamPositionsInBoundingBox(BlockPos focalPos) {
		return BlockPos.betweenClosedStream(getMainBoundingBox(focalPos)).map(BlockPos::immutable);
	}

	private Stream streamPositionsInSolidBase(BlockPos focalPos) {
		return BlockPos.betweenClosedStream(getSolidBaseBoundingBox(focalPos)).map(BlockPos::immutable);
	}

	private boolean testForBlock(World world, BlockPos pos, List boundsToCheckEmpty, BiPredicate tester) {
		if (tester.test(world, pos)) {
			boundsToCheckEmpty.remove(pos);
			return true;
		}
		return false;
	}

	private BiPredicate testSidewaysHangingTrapdoor(Direction facing) {
		return (world, pos) -> {
			BlockState state = ((World) world).getBlockState((BlockPos) pos);
			if (!(state.getBlock() instanceof TrapDoorBlock)) {
				return false;
			}
			return state.getValue(HorizontalBlock.FACING) == facing.getOpposite() && state.getValue(TrapDoorBlock.OPEN);
		};
	}

	private BiPredicate testSidewaysStoneSlab(Direction facing) {
		return (world, pos) -> {
			BlockState state = ((World) world).getBlockState((BlockPos) pos);
			if (isValidStoneOrSimilarMaterial(state) && state.getBlock() instanceof SlabBlock) {
				Optional optAxialSlabProperty = state.getProperties().stream().filter(property -> {
					if (property.getValueClass() != Axis.class) {
						return false;
					}
					Collection propertyValues = property.getPossibleValues();
					return propertyValues.contains(Axis.X) && propertyValues.contains(Axis.Z);
				}).findFirst();
				if (optAxialSlabProperty.isPresent()) {
					Property axialSlabProperty = (Property) optAxialSlabProperty.get();
					SlabType expectedSlabType = facing.getAxisDirection() == AxisDirection.NEGATIVE ? SlabType.BOTTOM : SlabType.TOP;
					return state.getValue(axialSlabProperty) == facing.getAxis() && state.getValue(SlabBlock.TYPE) == expectedSlabType;
				}
			}

			return false;
		};
	}

	private BiPredicate testValidButtress(Direction facing) {
		return (world, pos) -> {
			BlockState state = ((World) world).getBlockState((BlockPos) pos);
			if (isValidStoneOrSimilarMaterial(state) && state.getBlock() instanceof StairsBlock) {
				return state.getValue(StairsBlock.FACING) == facing && state.getValue(StairsBlock.HALF) == Half.BOTTOM && state.getValue(StairsBlock.SHAPE) == StairsShape.STRAIGHT;
			}
			return testSidewaysStoneSlab(facing).test(world, pos);
		};
	}

	private BiPredicate testValidCrown(Direction facing) {
		return (world, pos) -> (isEmptyBlockForBounds((World) world, (BlockPos) pos) || testSidewaysStoneSlab(facing).test(world, pos) || testSidewaysHangingTrapdoor(facing).test(world, pos));
	}

	public void updateWaypointStructure(PlayerEntity player, CustomWaypoint waypoint) {
		World world = player.level;
		BlockPos waypointPos = waypoint.getPosition();
		CustomWaypointMarkerTileEntity marker = getAdjacentWaypointMarker(world, waypointPos, waypoint);
		if (marker != null) {
			marker.updateWaypointReference(waypoint);
			spawnParticles(world, waypointPos);
		} else {
			LOTRLog.error("Player %s tried to update a custom waypoint at (%s) - but somehow the tile entity does not exist!", UsernameHelper.getRawUsername(player), waypointPos);
		}

	}

	public static BlockPos getItemFrameSupportPos(ItemFrameEntity frame) {
		return frame.getPos().relative(frame.getDirection().getOpposite());
	}
}
