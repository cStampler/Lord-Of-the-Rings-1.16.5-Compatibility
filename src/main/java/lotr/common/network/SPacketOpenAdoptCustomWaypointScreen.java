package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.world.map.*;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketOpenAdoptCustomWaypointScreen {
	private final CustomWaypoint waypoint;
	private final String createdPlayerName;

	public SPacketOpenAdoptCustomWaypointScreen(CustomWaypoint wp, String playerName) {
		waypoint = wp;
		createdPlayerName = playerName;
	}

	public static SPacketOpenAdoptCustomWaypointScreen decode(PacketBuffer buf) {
		CustomWaypoint waypoint = CustomWaypoint.read(MapSettingsManager.clientInstance().getCurrentLoadedMap(), buf);
		String createdPlayerName = buf.readUtf();
		return new SPacketOpenAdoptCustomWaypointScreen(waypoint, createdPlayerName);
	}

	public static void encode(SPacketOpenAdoptCustomWaypointScreen packet, PacketBuffer buf) {
		packet.waypoint.write(buf);
		buf.writeUtf(packet.createdPlayerName);
	}

	public static void handle(SPacketOpenAdoptCustomWaypointScreen packet, Supplier context) {
		LOTRMod.PROXY.displayAdoptCustomWaypointScreen(packet.waypoint, packet.createdPlayerName);
		((Context) context.get()).setPacketHandled(true);
	}
}
