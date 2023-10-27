package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.item.RedBookItem;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import lotr.common.util.LOTRUtil;
import lotr.common.util.UsernameHelper;
import lotr.common.world.map.AbstractCustomWaypoint;
import lotr.common.world.map.CustomWaypoint;
import lotr.common.world.map.CustomWaypointStructureHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketDestroyCustomWaypoint {
	private final int waypointId;

	public CPacketDestroyCustomWaypoint(CustomWaypoint waypoint) {
		this(waypoint.getCustomId());
	}

	private CPacketDestroyCustomWaypoint(int waypointId) {
		this.waypointId = waypointId;
	}

	public static CPacketDestroyCustomWaypoint decode(PacketBuffer buf) {
		int waypointId = buf.readVarInt();
		return new CPacketDestroyCustomWaypoint(waypointId);
	}

	private static void doDestroyCustomWaypoint(CPacketDestroyCustomWaypoint packet, ServerPlayerEntity player, FastTravelDataModule ftData) {
		int waypointId = packet.waypointId;
		CustomWaypoint waypoint = ftData.getCustomWaypointById(waypointId);
		if (waypoint == null) {
			LOTRLog.warn("Player %s tried to destroy a custom waypoint with nonexistent ID %d", UsernameHelper.getRawUsername(player), waypointId);
		} else {
			BlockPos waypointPos = waypoint.getPosition();
			Vector3d playerPos = player.position();
			CustomWaypointMarkerTileEntity marker = CustomWaypointStructureHandler.INSTANCE.getAdjacentWaypointMarker(player.level, waypointPos, (AbstractCustomWaypoint) null);
			if (marker == null) {
				LOTRLog.warn("Player %s tried to destroy a custom waypoint where no waypoint structure exists (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
			} else if (!marker.matchesWaypointReference(waypoint)) {
				LOTRLog.warn("Player %s tried to destroy a custom waypoint at a marker which doesn't match the target waypoint (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
			} else if (waypointPos.distSqr(playerPos, false) >= 64.0D) {
				LOTRLog.warn("Player %s tried to destroy a custom waypoint on a block too far away (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
			} else if (CustomWaypointStructureHandler.INSTANCE.destroyCustomWaypointMarkerAndRemoveFromPlayerData(player.level, waypoint, player, true)) {
				LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.destroy", waypoint.getDisplayName()));
				RedBookItem.playCompleteWaypointActionSound(player.level, waypointPos);
			}
		}
	}

	public static void encode(CPacketDestroyCustomWaypoint packet, PacketBuffer buf) {
		buf.writeVarInt(packet.waypointId);
	}

	public static void handle(CPacketDestroyCustomWaypoint packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		FastTravelDataModule ftData = LOTRLevelData.serverInstance().getData(player).getFastTravelData();
		doDestroyCustomWaypoint(packet, player, ftData);
		((Context) context.get()).setPacketHandled(true);
	}
}
