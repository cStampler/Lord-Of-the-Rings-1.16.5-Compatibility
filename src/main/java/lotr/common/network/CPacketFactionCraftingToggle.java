package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.inv.FactionCraftingContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketFactionCraftingToggle {
	private final boolean isStandardCrafting;

	public CPacketFactionCraftingToggle(boolean standard) {
		isStandardCrafting = standard;
	}

	public static CPacketFactionCraftingToggle decode(PacketBuffer buf) {
		boolean standardCrafting = buf.readBoolean();
		return new CPacketFactionCraftingToggle(standardCrafting);
	}

	public static void encode(CPacketFactionCraftingToggle packet, PacketBuffer buf) {
		buf.writeBoolean(packet.isStandardCrafting);
	}

	public static void handle(CPacketFactionCraftingToggle packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		Container container = player.containerMenu;
		if (container instanceof FactionCraftingContainer) {
			boolean standardCrafting = packet.isStandardCrafting;
			((FactionCraftingContainer) container).setStandardCraftingActive(standardCrafting);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
