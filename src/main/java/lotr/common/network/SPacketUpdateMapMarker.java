package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.world.map.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketUpdateMapMarker {
	private final int markerId;
	private final String name;
	private final MapMarkerIcon icon;

	private SPacketUpdateMapMarker(int markerId, String name, MapMarkerIcon icon) {
		this.markerId = markerId;
		this.name = name;
		this.icon = icon;
	}

	public SPacketUpdateMapMarker(MapMarker marker) {
		this(marker.getId(), marker.getName(), marker.getIcon());
	}

	public static SPacketUpdateMapMarker decode(PacketBuffer buf) {
		int markerId = buf.readVarInt();
		String name = buf.readUtf(32);
		MapMarkerIcon icon = MapMarkerIcon.forNetworkIdOrDefault(buf.readVarInt());
		return new SPacketUpdateMapMarker(markerId, name, icon);
	}

	public static void encode(SPacketUpdateMapMarker packet, PacketBuffer buf) {
		buf.writeVarInt(packet.markerId);
		buf.writeUtf(packet.name);
		buf.writeVarInt(packet.icon.networkId);
	}

	public static void handle(SPacketUpdateMapMarker packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		MapMarkerDataModule markerData = LOTRLevelData.clientInstance().getData(player).getMapMarkerData();
		int markerId = packet.markerId;
		MapMarker marker = markerData.getMarkerById(markerId);
		if (marker != null) {
			markerData.updateMarker(marker, packet.name, packet.icon);
		} else {
			LOTRLog.warn("Received map marker update from server, but no marker for ID %d exists", markerId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
