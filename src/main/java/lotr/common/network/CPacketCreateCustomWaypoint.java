package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.init.LOTRWorldTypes;
import lotr.common.item.RedBookItem;
import lotr.common.stat.LOTRStats;
import lotr.common.util.*;
import lotr.common.world.map.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketCreateCustomWaypoint {
	private final String name;
	private final String lore;
	private final boolean isPublic;

	public CPacketCreateCustomWaypoint(String name, String lore, boolean isPublic) {
		this.name = name;
		this.lore = lore;
		this.isPublic = isPublic;
	}

	public static CPacketCreateCustomWaypoint decode(PacketBuffer buf) {
		String name = buf.readUtf(40);
		String lore = buf.readUtf(160);
		boolean isPublic = buf.readBoolean();
		return new CPacketCreateCustomWaypoint(name, lore, isPublic);
	}

	private static void doCreateCustomWaypoint(CPacketCreateCustomWaypoint packet, ServerPlayerEntity player, FastTravelDataModule ftData) {
		ServerWorld world = player.getLevel();
		if (!world.getGameRules().getBoolean(LOTRGameRules.CUSTOM_WAYPOINT_CREATION)) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.create.disabled.gamerule"));
		} else if (!LOTRWorldTypes.hasMapFeatures(world)) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.create.disabled.worldtype"));
		} else if (!ftData.canCreateOrAdoptMoreCustomWaypoints()) {
			LOTRLog.warn("Player %s tried to create a custom waypoint but has reached their limit", UsernameHelper.getRawUsername(player));
		} else {
			String waypointName = packet.name.trim();
			String waypointLore = packet.lore.trim();
			if (waypointName.isEmpty()) {
				LOTRLog.warn("Player %s tried to create a custom waypoint with a blank name", UsernameHelper.getRawUsername(player));
			} else if (waypointName.length() > 40) {
				LOTRLog.warn("Player %s tried to create a custom waypoint with a name too long (%s)", UsernameHelper.getRawUsername(player), waypointName);
			} else if (waypointLore.length() > 160) {
				LOTRLog.warn("Player %s tried to create a custom waypoint with lore too long (%s)", UsernameHelper.getRawUsername(player), waypointLore);
			} else {
				boolean isPublic = packet.isPublic;
				BlockPos waypointPos = CustomWaypointStructureHandler.INSTANCE.getPlayerClickedOnBlockToCreate(player);
				if (waypointPos == null) {
					LOTRLog.warn("Player %s tried to create a custom waypoint without having clicked on a block", UsernameHelper.getRawUsername(player));
				} else {
					Vector3d playerPos = player.position();
					if (waypointPos.distSqr(playerPos, false) >= 64.0D) {
						LOTRLog.warn("Player %s tried to create a custom waypoint on a block too far away (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
					} else if (!CustomWaypointStructureHandler.INSTANCE.isFocalPointOfCompletableStructure(world, waypointPos)) {
						LOTRLog.warn("Player %s tried to create a custom waypoint without a completed structure (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
					} else if (CustomWaypointStructureHandler.INSTANCE.hasAdjacentWaypointMarker(world, waypointPos)) {
						LOTRLog.warn("Player %s tried to create a custom waypoint at an already completed structure (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
					} else {
						CustomWaypointStructureHandler.INSTANCE.clearPlayerClickedOnBlockToCreate(player);
						CustomWaypoint createdWaypoint = ftData.createNewCustomWaypoint(waypointName, waypointLore, isPublic, waypointPos);
						CustomWaypointStructureHandler.INSTANCE.completeStructureWithCreatedWaypoint(player, createdWaypoint);
						LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.create", waypointName, ftData.getNumCustomWaypoints(), ftData.getMaxCustomWaypoints()));
						RedBookItem.playCompleteWaypointActionSound(world, waypointPos);
						player.awardStat(LOTRStats.CREATE_CUSTOM_WAYPOINT);
					}
				}
			}
		}
	}

	public static void encode(CPacketCreateCustomWaypoint packet, PacketBuffer buf) {
		buf.writeUtf(packet.name);
		buf.writeUtf(packet.lore);
		buf.writeBoolean(packet.isPublic);
	}

	public static void handle(CPacketCreateCustomWaypoint packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		FastTravelDataModule ftData = LOTRLevelData.serverInstance().getData(player).getFastTravelData();
		doCreateCustomWaypoint(packet, player, ftData);
		((Context) context.get()).setPacketHandled(true);
	}
}
