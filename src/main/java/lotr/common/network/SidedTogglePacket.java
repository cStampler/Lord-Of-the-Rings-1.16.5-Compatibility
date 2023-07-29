package lotr.common.network;

import java.util.function.*;

import lotr.common.LOTRLog;
import lotr.common.data.LOTRPlayerData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class SidedTogglePacket {
	private final SidedTogglePacket.ToggleType type;
	private final boolean fieldValue;

	public SidedTogglePacket(SidedTogglePacket.ToggleType type, boolean fieldValue) {
		this.type = type;
		this.fieldValue = fieldValue;
	}

	protected abstract LOTRPlayerData getSidedPlayerData(Supplier var1);

	public static SidedTogglePacket decode(PacketBuffer buf, BiFunction packetConstructor) {
		SidedTogglePacket.ToggleType type = SidedTogglePacket.ToggleType.forId(buf.readByte());
		boolean fieldValue = buf.readBoolean();
		return (SidedTogglePacket) packetConstructor.apply(type, fieldValue);
	}

	protected static void encode(SidedTogglePacket packet, PacketBuffer buf) {
		buf.writeByte(packet.type.ordinal());
		buf.writeBoolean(packet.fieldValue);
	}

	public static void handle(SidedTogglePacket packet, Supplier context) {
		LOTRPlayerData pd = packet.getSidedPlayerData(context);
		SidedTogglePacket.ToggleType type = packet.type;
		boolean fieldValue = packet.fieldValue;
		if (type == SidedTogglePacket.ToggleType.SHOW_MAP_LOCATION) {
			pd.getMiscData().setShowMapLocation(fieldValue);
		} else if (type == SidedTogglePacket.ToggleType.SHOW_MAP_MARKERS) {
			pd.getMapMarkerData().setShowMarkers(fieldValue);
		} else if (type == SidedTogglePacket.ToggleType.FRIENDLY_FIRE) {
			pd.getAlignmentData().setFriendlyFireEnabled(fieldValue);
		} else {
			LOTRLog.error("Received %s with unsupported type %s!", packet.getClass().getSimpleName(), type.name());
		}

		((Context) context.get()).setPacketHandled(true);
	}

	public enum ToggleType {
		SHOW_MAP_LOCATION, SHOW_MAP_MARKERS, FRIENDLY_FIRE;

		public static SidedTogglePacket.ToggleType forId(int id) {
			return values()[MathHelper.clamp(id, 0, values().length - 1)];
		}
	}
}
