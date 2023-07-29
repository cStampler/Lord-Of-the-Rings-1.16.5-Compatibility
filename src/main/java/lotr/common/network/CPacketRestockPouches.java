package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.item.PouchItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketRestockPouches {
	public static CPacketRestockPouches decode(PacketBuffer buf) {
		return new CPacketRestockPouches();
	}

	public static void encode(CPacketRestockPouches packet, PacketBuffer buf) {
	}

	public static void handle(CPacketRestockPouches packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		PouchItem.attemptRestockPouches(player);
		((Context) context.get()).setPacketHandled(true);
	}
}
