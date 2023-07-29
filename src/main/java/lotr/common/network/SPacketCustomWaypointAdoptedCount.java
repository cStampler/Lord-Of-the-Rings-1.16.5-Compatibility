package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.world.map.CustomWaypoint;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketCustomWaypointAdoptedCount {
	private final int waypointId;
	private final int adoptedCount;

	public SPacketCustomWaypointAdoptedCount(CustomWaypoint waypoint, int adoptedCount) {
		this(waypoint.getCustomId(), adoptedCount);
	}

	private SPacketCustomWaypointAdoptedCount(int waypointId, int adoptedCount) {
		this.waypointId = waypointId;
		this.adoptedCount = adoptedCount;
	}

	public static SPacketCustomWaypointAdoptedCount decode(PacketBuffer buf) {
		int waypointId = buf.readVarInt();
		int adoptedCount = buf.readVarInt();
		return new SPacketCustomWaypointAdoptedCount(waypointId, adoptedCount);
	}

	public static void encode(SPacketCustomWaypointAdoptedCount packet, PacketBuffer buf) {
		buf.writeVarInt(packet.waypointId);
		buf.writeVarInt(packet.adoptedCount);
	}

	public static void handle(SPacketCustomWaypointAdoptedCount packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		int waypointId = packet.waypointId;
		CustomWaypoint waypoint = ftData.getCustomWaypointById(waypointId);
		if (waypoint != null) {
			waypoint.receiveAdoptedCountFromServer(packet.adoptedCount);
		} else {
			LOTRLog.warn("Received custom waypoint adopted count from server, but no custom waypoint for ID %d exists", waypointId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
