package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketShowWaypoints {
	private final boolean showMapWaypoints;
	private final boolean showCustomWaypoints;

	public SPacketShowWaypoints(boolean mapWp, boolean customWp) {
		showMapWaypoints = mapWp;
		showCustomWaypoints = customWp;
	}

	public static SPacketShowWaypoints decode(PacketBuffer buf) {
		boolean showMapWaypoints = buf.readBoolean();
		boolean showCustomWaypoints = buf.readBoolean();
		return new SPacketShowWaypoints(showMapWaypoints, showCustomWaypoints);
	}

	public static void encode(SPacketShowWaypoints packet, PacketBuffer buf) {
		buf.writeBoolean(packet.showMapWaypoints);
		buf.writeBoolean(packet.showCustomWaypoints);
	}

	public static void handle(SPacketShowWaypoints packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FastTravelDataModule ftData = LOTRLevelData.clientInstance().getData(player).getFastTravelData();
		ftData.setShowMapWaypoints(packet.showMapWaypoints);
		ftData.setShowCustomWaypoints(packet.showCustomWaypoints);
		((Context) context.get()).setPacketHandled(true);
	}
}
