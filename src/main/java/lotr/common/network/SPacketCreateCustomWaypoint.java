package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.world.map.CustomWaypoint;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketCreateCustomWaypoint {
	private final CustomWaypoint waypoint;

	public SPacketCreateCustomWaypoint(CustomWaypoint wp) {
		waypoint = wp;
	}

	public static SPacketCreateCustomWaypoint decode(PacketBuffer buf) {
		CustomWaypoint waypoint = CustomWaypoint.read(MapSettingsManager.clientInstance().getCurrentLoadedMap(), buf);
		return new SPacketCreateCustomWaypoint(waypoint);
	}

	public static void encode(SPacketCreateCustomWaypoint packet, PacketBuffer buf) {
		packet.waypoint.write(buf);
	}

	public static void handle(SPacketCreateCustomWaypoint packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		ftData.addCreatedCustomWaypointFromServer(packet.waypoint);
		((Context) context.get()).setPacketHandled(true);
	}
}
