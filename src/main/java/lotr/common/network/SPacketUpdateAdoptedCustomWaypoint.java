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

public class SPacketUpdateAdoptedCustomWaypoint {
	private final AdoptedCustomWaypointKey waypointKey;
	private final String name;
	private final String lore;

	public SPacketUpdateAdoptedCustomWaypoint(AdoptedCustomWaypoint waypoint) {
		this(waypoint.getAdoptedKey(), waypoint.getRawName(), waypoint.getRawLore());
	}

	private SPacketUpdateAdoptedCustomWaypoint(AdoptedCustomWaypointKey key, String name, String lore) {
		waypointKey = key;
		this.name = name;
		this.lore = lore;
	}

	public static SPacketUpdateAdoptedCustomWaypoint decode(PacketBuffer buf) {
		AdoptedCustomWaypointKey waypointKey = AdoptedCustomWaypointKey.read(buf);
		String name = buf.readUtf();
		String lore = buf.readUtf();
		return new SPacketUpdateAdoptedCustomWaypoint(waypointKey, name, lore);
	}

	public static void encode(SPacketUpdateAdoptedCustomWaypoint packet, PacketBuffer buf) {
		packet.waypointKey.write(buf);
		buf.writeUtf(packet.name);
		buf.writeUtf(packet.lore);
	}

	public static void handle(SPacketUpdateAdoptedCustomWaypoint packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		AdoptedCustomWaypointKey key = packet.waypointKey;
		AdoptedCustomWaypoint waypoint = ftData.getAdoptedCustomWaypointByKey(key);
		if (waypoint != null) {
			waypoint.updateFromOriginal(packet.name, packet.lore);
		} else {
			LOTRLog.warn("Received adopted custom waypoint update from server, but none for (creator %s, ID %d) exists", key.getCreatedPlayer(), key.getWaypointId());
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
