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

public class SPacketOpenViewAdoptedCustomWaypointScreen {
	private final AdoptedCustomWaypointKey waypointKey;
	private final String createdPlayerName;

	public SPacketOpenViewAdoptedCustomWaypointScreen(AdoptedCustomWaypoint wp, String playerName) {
		this(wp.getAdoptedKey(), playerName);
	}

	public SPacketOpenViewAdoptedCustomWaypointScreen(AdoptedCustomWaypointKey key, String playerName) {
		waypointKey = key;
		createdPlayerName = playerName;
	}

	public static SPacketOpenViewAdoptedCustomWaypointScreen decode(PacketBuffer buf) {
		AdoptedCustomWaypointKey waypoint = AdoptedCustomWaypointKey.read(buf);
		String createdPlayerName = buf.readUtf();
		return new SPacketOpenViewAdoptedCustomWaypointScreen(waypoint, createdPlayerName);
	}

	public static void encode(SPacketOpenViewAdoptedCustomWaypointScreen packet, PacketBuffer buf) {
		packet.waypointKey.write(buf);
		buf.writeUtf(packet.createdPlayerName);
	}

	public static void handle(SPacketOpenViewAdoptedCustomWaypointScreen packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		AdoptedCustomWaypointKey waypointKey = packet.waypointKey;
		AdoptedCustomWaypoint waypoint = ftData.getAdoptedCustomWaypointByKey(waypointKey);
		if (waypoint != null) {
			LOTRMod.PROXY.displayViewAdoptedCustomWaypointScreen(waypoint, packet.createdPlayerName);
		} else {
			LOTRLog.warn("Server asked to open an adopted custom waypoint view screen, but no adopted custom waypoint for (creator %s, ID %d) exists", waypointKey.getCreatedPlayer(), waypointKey.getWaypointId());
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
