package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketIsOpResponse {
	private final boolean isOp;

	public SPacketIsOpResponse(boolean op) {
		isOp = op;
	}

	public static SPacketIsOpResponse decode(PacketBuffer buf) {
		boolean isOp = buf.readBoolean();
		return new SPacketIsOpResponse(isOp);
	}

	public static void encode(SPacketIsOpResponse packet, PacketBuffer buf) {
		buf.writeBoolean(packet.isOp);
	}

	public static void handle(SPacketIsOpResponse packet, Supplier context) {
		LOTRMod.PROXY.mapHandleIsOp(packet.isOp);
		((Context) context.get()).setPacketHandled(true);
	}
}
