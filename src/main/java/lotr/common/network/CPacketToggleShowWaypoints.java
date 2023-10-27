package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketToggleShowWaypoints {
	private final boolean showMapWaypoints;
	private final boolean showCustomWaypoints;

	public CPacketToggleShowWaypoints(boolean mapWp, boolean customWp) {
		showMapWaypoints = mapWp;
		showCustomWaypoints = customWp;
	}

	public static CPacketToggleShowWaypoints decode(PacketBuffer buf) {
		boolean showMapWaypoints = buf.readBoolean();
		boolean showCustomWaypoints = buf.readBoolean();
		return new CPacketToggleShowWaypoints(showMapWaypoints, showCustomWaypoints);
	}

	public static void encode(CPacketToggleShowWaypoints packet, PacketBuffer buf) {
		buf.writeBoolean(packet.showMapWaypoints);
		buf.writeBoolean(packet.showCustomWaypoints);
	}

	public static void handle(CPacketToggleShowWaypoints packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		FastTravelDataModule ftData = LOTRLevelData.serverInstance().getData(player).getFastTravelData();
		ftData.setShowMapWaypoints(packet.showMapWaypoints);
		ftData.setShowCustomWaypoints(packet.showCustomWaypoints);
		((Context) context.get()).setPacketHandled(true);
	}
}
