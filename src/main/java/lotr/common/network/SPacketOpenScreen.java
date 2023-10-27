package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketOpenScreen {
	private final SPacketOpenScreen.Type type;

	public SPacketOpenScreen(SPacketOpenScreen.Type type) {
		this.type = type;
	}

	public static SPacketOpenScreen decode(PacketBuffer buf) {
		int typeId = buf.readVarInt();
		SPacketOpenScreen.Type type = SPacketOpenScreen.Type.forId(typeId);
		if (type == null) {
			LOTRLog.warn("Received nonexistent open-screen-packet type ID %d from server", typeId);
		}

		return new SPacketOpenScreen(type);
	}

	public static void encode(SPacketOpenScreen packet, PacketBuffer buf) {
		buf.writeVarInt(packet.type.ordinal());
	}

	public static void handle(SPacketOpenScreen packet, Supplier context) {
		LOTRMod.PROXY.displayPacketOpenScreen(packet.type);
		((Context) context.get()).setPacketHandled(true);
	}

	public enum Type {
		CREATE_CUSTOM_WAYPOINT;

		public static final SPacketOpenScreen.Type forId(int id) {
			return id >= 0 && id < values().length ? values()[id] : null;
		}
	}
}
