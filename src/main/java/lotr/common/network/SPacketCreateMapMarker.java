package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.MapMarkerDataModule;
import lotr.common.world.map.MapMarker;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketCreateMapMarker {
	private final MapMarker marker;

	public SPacketCreateMapMarker(MapMarker marker) {
		this.marker = marker;
	}

	public static SPacketCreateMapMarker decode(PacketBuffer buf) {
		MapMarker marker = MapMarker.read(MapSettingsManager.clientInstance().getCurrentLoadedMap(), buf);
		return new SPacketCreateMapMarker(marker);
	}

	public static void encode(SPacketCreateMapMarker packet, PacketBuffer buf) {
		packet.marker.write(buf);
	}

	public static void handle(SPacketCreateMapMarker packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		MapMarkerDataModule markerData = LOTRLevelData.clientInstance().getData(player).getMapMarkerData();
		markerData.addCreatedMarkerFromServer(packet.marker);
		((Context) context.get()).setPacketHandled(true);
	}
}
