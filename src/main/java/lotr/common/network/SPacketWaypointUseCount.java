package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.world.map.Waypoint;
import lotr.common.world.map.WaypointNetworkType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketWaypointUseCount {
	private final Waypoint waypoint;
	private final int useCount;

	public SPacketWaypointUseCount(Waypoint wp, int count) {
		waypoint = wp;
		useCount = count;
	}

	public static SPacketWaypointUseCount decode(PacketBuffer buf) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		Waypoint waypoint = WaypointNetworkType.readFromIdentification(buf, pd);
		int useCount = buf.readVarInt();
		return new SPacketWaypointUseCount(waypoint, useCount);
	}

	public static void encode(SPacketWaypointUseCount packet, PacketBuffer buf) {
		WaypointNetworkType.writeIdentification(buf, packet.waypoint);
		buf.writeVarInt(packet.useCount);
	}

	public static void handle(SPacketWaypointUseCount packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		FastTravelDataModule ftData = pd.getFastTravelData();
		ftData.setWPUseCount(packet.waypoint, packet.useCount);
		((Context) context.get()).setPacketHandled(true);
	}
}
