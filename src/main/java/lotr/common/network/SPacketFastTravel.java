package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import lotr.common.world.map.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketFastTravel {
	private final Waypoint waypoint;
	private final int startX;
	private final int startZ;

	public SPacketFastTravel(Waypoint wp, int x, int z) {
		waypoint = wp;
		startX = x;
		startZ = z;
	}

	public static SPacketFastTravel decode(PacketBuffer buf) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		Waypoint waypoint = WaypointNetworkType.readFromIdentification(buf, pd);
		int startX = buf.readVarInt();
		int startZ = buf.readVarInt();
		return new SPacketFastTravel(waypoint, startX, startZ);
	}

	public static void encode(SPacketFastTravel packet, PacketBuffer buf) {
		WaypointNetworkType.writeIdentification(buf, packet.waypoint);
		buf.writeVarInt(packet.startX);
		buf.writeVarInt(packet.startZ);
	}

	public static void handle(SPacketFastTravel packet, Supplier context) {
		LOTRMod.PROXY.displayFastTravelScreen(packet.waypoint, packet.startX, packet.startZ);
		((Context) context.get()).setPacketHandled(true);
	}
}
