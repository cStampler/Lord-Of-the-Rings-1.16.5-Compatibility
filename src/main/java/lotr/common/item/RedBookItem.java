package lotr.common.item;

import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.*;
import lotr.common.block.CustomWaypointMarkerBlock;
import lotr.common.data.*;
import lotr.common.init.LOTRBlocks;
import lotr.common.network.*;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import lotr.common.util.*;
import lotr.common.world.map.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RedBookItem extends Item {
	public RedBookItem(Properties properties) {
		super(properties);
	}

	public boolean createCustomWaypointStructure(World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClientSide) {
			ServerWorld sWorld = (ServerWorld) world;
			boolean canBeCWPStructure = CustomWaypointStructureHandler.INSTANCE.isFocalPoint(world, pos);
			if (canBeCWPStructure) {
				if (!world.getGameRules().getBoolean(LOTRGameRules.CUSTOM_WAYPOINT_CREATION)) {
					LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.create.disabled"));
					return false;
				}

				if (CustomWaypointStructureHandler.INSTANCE.hasAdjacentWaypointMarker(world, pos)) {
					LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.alreadyExists"));
					playFailedSound(world, pos);
					return true;
				}

				FastTravelDataModule ftData = LOTRLevelData.sidedInstance(world).getData(player).getFastTravelData();
				if (!ftData.canCreateOrAdoptMoreCustomWaypoints()) {
					LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.limit"));
					playFailedSound(world, pos);
					return true;
				}

				boolean isCompleteStructure = CustomWaypointStructureHandler.INSTANCE.isFocalPointOfCompletableStructure(sWorld, pos, msg -> {
					LOTRUtil.sendMessage(player, (ITextComponent) msg);
				});
				if (isCompleteStructure) {
					CustomWaypointStructureHandler.INSTANCE.setPlayerClickedOnBlockToCreate(player, pos);
					LOTRPacketHandler.sendTo(new SPacketOpenScreen(SPacketOpenScreen.Type.CREATE_CUSTOM_WAYPOINT), (ServerPlayerEntity) player);
					playOpenScreenSound(world, pos);
				} else {
					playFailedSound(world, pos);
				}

				return true;
			}
		}

		return false;
	}

	private Pair getClickedOnOrAdjacentMarkerAndFocalPos(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == LOTRBlocks.CUSTOM_WAYPOINT_MARKER.get()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof CustomWaypointMarkerTileEntity) {
				CustomWaypointMarkerTileEntity marker = (CustomWaypointMarkerTileEntity) te;
				BlockPos focalPos = pos.relative(state.getValue(CustomWaypointMarkerBlock.FACING).getOpposite());
				return Pair.of(marker, focalPos);
			}
		} else {
			CustomWaypointMarkerTileEntity adjacentMarker = CustomWaypointStructureHandler.INSTANCE.getAdjacentWaypointMarker(world, pos, (AbstractCustomWaypoint) null);
			if (adjacentMarker != null) {
				return Pair.of(adjacentMarker, pos);
			}
		}

		return null;
	}

	private void playFailedSound(World world, BlockPos pos) {
		world.playSound((PlayerEntity) null, pos, world.getBlockState(pos).getSoundType().getHitSound(), SoundCategory.PLAYERS, 0.5F, 1.0F);
	}

	private void playOpenScreenSound(World world, BlockPos pos) {
		world.playSound((PlayerEntity) null, pos, SoundEvents.BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	private boolean updateCustomWaypointStructure(World world, BlockPos waypointStructurePos, PlayerEntity player, CustomWaypointMarkerTileEntity marker) {
		int waypointId = marker.getWaypointId();
		CustomWaypoint waypoint = LOTRLevelData.sidedInstance(world).getData(player).getFastTravelData().getCustomWaypointById(waypointId);
		if (waypoint == null) {
			LOTRLog.error("Player %s tried to update completed custom waypoint structure at (%s), but no matching waypoint exists in the player data!", UsernameHelper.getRawUsername(player), waypointStructurePos.toString());
			return false;
		}
		BlockPos waypointSavedPos = waypoint.getPosition();
		if (waypointStructurePos.equals(waypointSavedPos)) {
			LOTRPacketHandler.sendTo(new SPacketOpenUpdateCustomWaypointScreen(waypoint), (ServerPlayerEntity) player);
			playOpenScreenSound(world, waypointStructurePos);
			return true;
		}
		LOTRLog.error("Player %s tried to update completed custom waypoint structure at (%s), but the waypoint's saved position (%s) didn't match!", UsernameHelper.getRawUsername(player), waypointStructurePos.toString(), waypointSavedPos.toString());
		return false;
	}

	private boolean useExistingCustomWaypointStructure(World world, BlockPos pos, PlayerEntity player) {
		if (!world.isClientSide) {
			Pair markerAndPos = getClickedOnOrAdjacentMarkerAndFocalPos(world, pos);
			if (markerAndPos != null) {
				CustomWaypointMarkerTileEntity marker = (CustomWaypointMarkerTileEntity) markerAndPos.getLeft();
				BlockPos waypointStructurePos = (BlockPos) markerAndPos.getRight();
				UUID waypointPlayer = marker.getWaypointPlayer();
				if (player.getUUID().equals(waypointPlayer)) {
					return updateCustomWaypointStructure(world, waypointStructurePos, player, marker);
				}

				return useOtherPlayerCustomWaypointStructure(world, waypointStructurePos, player, marker, waypointPlayer);
			}
		}

		return false;
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity player = context.getPlayer();
		if (useExistingCustomWaypointStructure(world, pos, player)) {
			return ActionResultType.SUCCESS;
		}
		return createCustomWaypointStructure(world, pos, player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
	}

	private boolean useOtherPlayerCustomWaypointStructure(World world, BlockPos waypointStructurePos, PlayerEntity player, CustomWaypointMarkerTileEntity marker, UUID waypointPlayer) {
		if (!marker.isWaypointPublic()) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.update.otherPlayer"));
			playFailedSound(world, waypointStructurePos);
			return true;
		}
		int waypointId = marker.getWaypointId();
		AdoptedCustomWaypointKey adoptKey = AdoptedCustomWaypointKey.of(waypointPlayer, waypointId);
		FastTravelDataModule ftData = LOTRLevelData.sidedInstance(world).getData(player).getFastTravelData();
		AdoptedCustomWaypoint waypoint = ftData.getAdoptedCustomWaypointByKey(adoptKey);
		if (waypoint != null) {
			String createdPlayerName = UsernameHelper.getLastKnownUsernameOrFallback(waypointPlayer);
			LOTRPacketHandler.sendTo(new SPacketOpenViewAdoptedCustomWaypointScreen(waypoint, createdPlayerName), (ServerPlayerEntity) player);
			playOpenScreenSound(world, waypointStructurePos);
		} else if (!ftData.canCreateOrAdoptMoreCustomWaypoints()) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.limit"));
			playFailedSound(world, waypointStructurePos);
		} else {
			CustomWaypoint originalWaypoint = LOTRLevelData.sidedInstance(world).getData(world, waypointPlayer).getFastTravelData().getCustomWaypointById(waypointId);
			String createdPlayerName = UsernameHelper.getLastKnownUsernameOrFallback(waypointPlayer);
			LOTRPacketHandler.sendTo(new SPacketOpenAdoptCustomWaypointScreen(originalWaypoint, createdPlayerName), (ServerPlayerEntity) player);
			playOpenScreenSound(world, waypointStructurePos);
		}
		return true;
	}

	public static void playCompleteWaypointActionSound(World world, BlockPos pos) {
		world.playSound((PlayerEntity) null, pos, SoundEvents.BOOK_PUT, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}
}
