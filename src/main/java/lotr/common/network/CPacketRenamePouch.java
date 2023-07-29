package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.inv.PouchContainer;
import lotr.common.util.UsernameHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketRenamePouch {
	private final String name;

	public CPacketRenamePouch(String name) {
		this.name = name;
	}

	public static CPacketRenamePouch decode(PacketBuffer buf) {
		String name = buf.readUtf(64);
		return new CPacketRenamePouch(name);
	}

	public static void encode(CPacketRenamePouch packet, PacketBuffer buf) {
		buf.writeUtf(packet.name, 64);
	}

	public static void handle(CPacketRenamePouch packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		Container container = player.containerMenu;
		if (container instanceof PouchContainer) {
			((PouchContainer) container).renamePouch(packet.name);
		} else {
			LOTRLog.warn("Player %s tried to rename a pouch, but their current open container was %s", UsernameHelper.getRawUsername(player), container);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
