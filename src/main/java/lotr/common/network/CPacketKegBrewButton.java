package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.inv.KegContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketKegBrewButton {
	public static CPacketKegBrewButton decode(PacketBuffer buf) {
		return new CPacketKegBrewButton();
	}

	public static void encode(CPacketKegBrewButton packet, PacketBuffer buf) {
	}

	public static void handle(CPacketKegBrewButton packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		Container container = player.containerMenu;
		if (container instanceof KegContainer) {
			((KegContainer) container).handleBrewButtonPress(player);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
