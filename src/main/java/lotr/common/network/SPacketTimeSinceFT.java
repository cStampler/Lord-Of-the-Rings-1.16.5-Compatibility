package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketTimeSinceFT {
	private final int ftSinceTick;

	public SPacketTimeSinceFT(int timer) {
		ftSinceTick = timer;
	}

	public static SPacketTimeSinceFT decode(PacketBuffer buf) {
		int ftSinceTick = buf.readInt();
		return new SPacketTimeSinceFT(ftSinceTick);
	}

	public static void encode(SPacketTimeSinceFT packet, PacketBuffer buf) {
		buf.writeInt(packet.ftSinceTick);
	}

	public static void handle(SPacketTimeSinceFT packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		pd.getFastTravelData().setTimeSinceFT(packet.ftSinceTick);
		((Context) context.get()).setPacketHandled(true);
	}
}
