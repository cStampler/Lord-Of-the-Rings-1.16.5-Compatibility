package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.world.map.AdoptedCustomWaypoint;
import lotr.common.world.map.AdoptedCustomWaypointKey;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketDeleteAdoptedCustomWaypoint {
	private final AdoptedCustomWaypointKey adoptedWaypointKey;

	public SPacketDeleteAdoptedCustomWaypoint(AdoptedCustomWaypoint wp) {
		this(wp.getAdoptedKey());
	}

	private SPacketDeleteAdoptedCustomWaypoint(AdoptedCustomWaypointKey key) {
		adoptedWaypointKey = key;
	}

	public static SPacketDeleteAdoptedCustomWaypoint decode(PacketBuffer buf) {
		AdoptedCustomWaypointKey adoptedWaypointKey = AdoptedCustomWaypointKey.read(buf);
		return new SPacketDeleteAdoptedCustomWaypoint(adoptedWaypointKey);
	}

	public static void encode(SPacketDeleteAdoptedCustomWaypoint packet, PacketBuffer buf) {
		packet.adoptedWaypointKey.write(buf);
	}

	public static void handle(SPacketDeleteAdoptedCustomWaypoint packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		AdoptedCustomWaypointKey key = packet.adoptedWaypointKey;
		AdoptedCustomWaypoint waypoint = ftData.getAdoptedCustomWaypointByKey(key);
		if (waypoint != null) {
			ftData.removeAdoptedCustomWaypoint(player.level, waypoint);
		} else {
			LOTRLog.warn("Received adopted custom waypoint deletion from server, but no adopted custom waypoint for (creator %s, ID %d) exists", key.getCreatedPlayer(), key.getWaypointId());
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
