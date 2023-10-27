package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.world.map.CustomWaypoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketDeleteCustomWaypoint {
	private final int waypointId;

	public SPacketDeleteCustomWaypoint(CustomWaypoint wp) {
		this(wp.getCustomId());
	}

	private SPacketDeleteCustomWaypoint(int wpId) {
		waypointId = wpId;
	}

	public static SPacketDeleteCustomWaypoint decode(PacketBuffer buf) {
		int waypointId = buf.readVarInt();
		return new SPacketDeleteCustomWaypoint(waypointId);
	}

	public static void encode(SPacketDeleteCustomWaypoint packet, PacketBuffer buf) {
		buf.writeVarInt(packet.waypointId);
	}

	public static void handle(SPacketDeleteCustomWaypoint packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		int waypointId = packet.waypointId;
		CustomWaypoint waypoint = ftData.getCustomWaypointById(waypointId);
		if (waypoint != null) {
			ftData.removeCustomWaypoint(player.level, waypoint);
		} else {
			LOTRLog.warn("Received custom waypoint deletion from server, but no custom waypoint for ID %d exists", waypointId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
