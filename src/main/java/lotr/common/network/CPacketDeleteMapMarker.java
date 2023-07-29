package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.*;
import lotr.common.util.UsernameHelper;
import lotr.common.world.map.MapMarker;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketDeleteMapMarker {
	private final int markerId;

	private CPacketDeleteMapMarker(int markerId) {
		this.markerId = markerId;
	}

	public CPacketDeleteMapMarker(MapMarker marker) {
		this(marker.getId());
	}

	public static CPacketDeleteMapMarker decode(PacketBuffer buf) {
		int markerId = buf.readVarInt();
		return new CPacketDeleteMapMarker(markerId);
	}

	public static void encode(CPacketDeleteMapMarker packet, PacketBuffer buf) {
		buf.writeVarInt(packet.markerId);
	}

	public static void handle(CPacketDeleteMapMarker packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		MapMarkerDataModule markerData = LOTRLevelData.serverInstance().getData(player).getMapMarkerData();
		int markerId = packet.markerId;
		MapMarker marker = markerData.getMarkerById(markerId);
		if (marker != null) {
			markerData.removeMarker(marker);
		} else {
			LOTRLog.warn("Player %s tried to delete map marker, but no marker for ID %d exists", UsernameHelper.getRawUsername(player), markerId);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
