package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.world.map.AdoptedCustomWaypoint;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketAdoptCustomWaypoint {
	private final AdoptedCustomWaypoint waypoint;

	public SPacketAdoptCustomWaypoint(AdoptedCustomWaypoint wp) {
		waypoint = wp;
	}

	public static SPacketAdoptCustomWaypoint decode(PacketBuffer buf) {
		AdoptedCustomWaypoint waypoint = AdoptedCustomWaypoint.read(MapSettingsManager.clientInstance().getCurrentLoadedMap(), buf);
		return new SPacketAdoptCustomWaypoint(waypoint);
	}

	public static void encode(SPacketAdoptCustomWaypoint packet, PacketBuffer buf) {
		packet.waypoint.write(buf);
	}

	public static void handle(SPacketAdoptCustomWaypoint packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		ftData.addAdoptedCustomWaypointFromServer(packet.waypoint);
		((Context) context.get()).setPacketHandled(true);
	}
}
