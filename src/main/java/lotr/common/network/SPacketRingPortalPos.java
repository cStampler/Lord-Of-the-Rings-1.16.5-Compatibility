package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketRingPortalPos {
	private final BlockPos portalPos;

	public SPacketRingPortalPos(BlockPos pos) {
		portalPos = pos;
	}

	public static SPacketRingPortalPos decode(PacketBuffer buf) {
		BlockPos portalPos = buf.readBlockPos();
		return new SPacketRingPortalPos(portalPos);
	}

	public static void encode(SPacketRingPortalPos packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.portalPos);
	}

	public static void handle(SPacketRingPortalPos packet, Supplier context) {
		World clientWorld = LOTRMod.PROXY.getClientWorld();
		LOTRLevelData.clientInstance().markMiddleEarthPortalLocation(clientWorld, packet.portalPos);
		((Context) context.get()).setPacketHandled(true);
	}
}
