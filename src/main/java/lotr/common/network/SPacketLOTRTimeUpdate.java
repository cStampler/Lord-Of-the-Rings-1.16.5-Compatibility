package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.time.LOTRTime;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketLOTRTimeUpdate {
	private final long daytime;

	public SPacketLOTRTimeUpdate(long time) {
		daytime = time;
	}

	public static SPacketLOTRTimeUpdate decode(PacketBuffer buf) {
		long daytime = buf.readLong();
		return new SPacketLOTRTimeUpdate(daytime);
	}

	public static void encode(SPacketLOTRTimeUpdate packet, PacketBuffer buf) {
		buf.writeLong(packet.daytime);
	}

	public static void handle(SPacketLOTRTimeUpdate packet, Supplier context) {
		LOTRTime.setWorldTime(LOTRMod.PROXY.getClientWorld(), packet.daytime);
		((Context) context.get()).setPacketHandled(true);
	}
}
