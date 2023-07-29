package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.*;
import lotr.common.util.UsernameHelper;
import lotr.common.world.map.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketUpdateMapMarker {
	private final int markerId;
	private final String name;
	private final MapMarkerIcon icon;

	private CPacketUpdateMapMarker(int markerId, String name, MapMarkerIcon icon) {
		this.markerId = markerId;
		this.name = name;
		this.icon = icon;
	}

	public CPacketUpdateMapMarker(MapMarker marker) {
		this(marker.getId(), marker.getName(), marker.getIcon());
	}

	public static CPacketUpdateMapMarker decode(PacketBuffer buf) {
		int markerId = buf.readVarInt();
		String name = buf.readUtf(32);
		MapMarkerIcon icon = MapMarkerIcon.forNetworkIdOrDefault(buf.readVarInt());
		return new CPacketUpdateMapMarker(markerId, name, icon);
	}

	public static void encode(CPacketUpdateMapMarker packet, PacketBuffer buf) {
		buf.writeVarInt(packet.markerId);
		buf.writeUtf(packet.name);
		buf.writeVarInt(packet.icon.networkId);
	}

	public static void handle(CPacketUpdateMapMarker packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		MapMarkerDataModule markerData = LOTRLevelData.serverInstance().getData(player).getMapMarkerData();
		int markerId = packet.markerId;
		MapMarker marker = markerData.getMarkerById(markerId);
		if (marker != null) {
			markerData.updateMarker(marker, packet.name, packet.icon);
		} else {
			LOTRLog.warn("Player %s tried to update map marker, but no marker for ID %d exists", UsernameHelper.getRawUsername(player), markerId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
