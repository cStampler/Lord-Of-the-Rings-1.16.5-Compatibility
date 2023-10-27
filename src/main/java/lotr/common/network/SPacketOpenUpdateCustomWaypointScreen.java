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

public class SPacketOpenUpdateCustomWaypointScreen {
	private final int waypointId;

	public SPacketOpenUpdateCustomWaypointScreen(CustomWaypoint waypoint) {
		this(waypoint.getCustomId());
	}

	private SPacketOpenUpdateCustomWaypointScreen(int waypointId) {
		this.waypointId = waypointId;
	}

	public static SPacketOpenUpdateCustomWaypointScreen decode(PacketBuffer buf) {
		int waypointId = buf.readVarInt();
		return new SPacketOpenUpdateCustomWaypointScreen(waypointId);
	}

	public static void encode(SPacketOpenUpdateCustomWaypointScreen packet, PacketBuffer buf) {
		buf.writeVarInt(packet.waypointId);
	}

	public static void handle(SPacketOpenUpdateCustomWaypointScreen packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		int waypointId = packet.waypointId;
		CustomWaypoint waypoint = ftData.getCustomWaypointById(waypointId);
		if (waypoint != null) {
			LOTRMod.PROXY.displayUpdateCustomWaypointScreen(waypoint);
		} else {
			LOTRLog.warn("Server asked to open a custom waypoint editing screen, but no custom waypoint for ID %d exists", waypointId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
