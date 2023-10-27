package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.MapMarkerDataModule;
import lotr.common.world.map.MapMarker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketDeleteMapMarker {
	private final int markerId;

	private SPacketDeleteMapMarker(int markerId) {
		this.markerId = markerId;
	}

	public SPacketDeleteMapMarker(MapMarker marker) {
		this(marker.getId());
	}

	public static SPacketDeleteMapMarker decode(PacketBuffer buf) {
		int markerId = buf.readVarInt();
		return new SPacketDeleteMapMarker(markerId);
	}

	public static void encode(SPacketDeleteMapMarker packet, PacketBuffer buf) {
		buf.writeVarInt(packet.markerId);
	}

	public static void handle(SPacketDeleteMapMarker packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		MapMarkerDataModule markerData = LOTRLevelData.clientInstance().getData(player).getMapMarkerData();
		int markerId = packet.markerId;
		MapMarker marker = markerData.getMarkerById(markerId);
		if (marker != null) {
			markerData.removeMarker(marker);
		} else {
			LOTRLog.warn("Received map marker deletion from server, but no marker for ID %d exists", markerId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
