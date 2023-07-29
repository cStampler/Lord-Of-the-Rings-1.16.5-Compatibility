package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.*;
import lotr.common.item.RedBookItem;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import lotr.common.util.*;
import lotr.common.world.map.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketForsakeAdoptedCustomWaypoint {
	private final AdoptedCustomWaypointKey waypointKey;

	public CPacketForsakeAdoptedCustomWaypoint(AdoptedCustomWaypoint waypoint) {
		this(waypoint.getAdoptedKey());
	}

	private CPacketForsakeAdoptedCustomWaypoint(AdoptedCustomWaypointKey key) {
		waypointKey = key;
	}

	public static CPacketForsakeAdoptedCustomWaypoint decode(PacketBuffer buf) {
		AdoptedCustomWaypointKey waypointKey = AdoptedCustomWaypointKey.read(buf);
		return new CPacketForsakeAdoptedCustomWaypoint(waypointKey);
	}

	private static void doForsakeAdoptedCustomWaypoint(CPacketForsakeAdoptedCustomWaypoint packet, ServerPlayerEntity player, FastTravelDataModule ftData) {
		AdoptedCustomWaypointKey waypointKey = packet.waypointKey;
		AdoptedCustomWaypoint waypoint = ftData.getAdoptedCustomWaypointByKey(waypointKey);
		if (waypoint == null) {
			LOTRLog.warn("Player %s tried to forsake a nonexistent adopted custom waypoint (creator %s, ID %d)", UsernameHelper.getRawUsername(player), waypointKey.getCreatedPlayer(), waypointKey.getWaypointId());
		} else {
			BlockPos waypointPos = waypoint.getPosition();
			Vector3d playerPos = player.position();
			CustomWaypointMarkerTileEntity marker = CustomWaypointStructureHandler.INSTANCE.getAdjacentWaypointMarker(player.level, waypointPos, (AbstractCustomWaypoint) null);
			if (marker == null) {
				LOTRLog.warn("Player %s tried to forsake an adopted custom waypoint where no waypoint structure exists (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
			} else if (!marker.matchesWaypointReference(waypoint)) {
				LOTRLog.warn("Player %s tried to forsake an adopted custom waypoint at a marker which doesn't match the target waypoint (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
			} else if (waypointPos.distSqr(playerPos, false) >= 64.0D) {
				LOTRLog.warn("Player %s tried to forsake an adopted custom waypoint on a block too far away (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
			} else {
				ftData.removeAdoptedCustomWaypoint(player.level, waypoint);
				LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.forsake", waypoint.getDisplayName()));
				RedBookItem.playCompleteWaypointActionSound(player.level, waypointPos);
			}
		}
	}

	public static void encode(CPacketForsakeAdoptedCustomWaypoint packet, PacketBuffer buf) {
		packet.waypointKey.write(buf);
	}

	public static void handle(CPacketForsakeAdoptedCustomWaypoint packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		FastTravelDataModule ftData = LOTRLevelData.serverInstance().getData(player).getFastTravelData();
		doForsakeAdoptedCustomWaypoint(packet, player, ftData);
		((Context) context.get()).setPacketHandled(true);
	}
}
