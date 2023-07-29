package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.time.LOTRDate;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketDate {
	private final CompoundNBT dateData;
	private final boolean displayNewDate;

	public SPacketDate(CompoundNBT nbt, boolean newDate) {
		dateData = nbt;
		displayNewDate = newDate;
	}

	public static SPacketDate decode(PacketBuffer buf) {
		CompoundNBT dateData = buf.readNbt();
		boolean displayNewDate = buf.readBoolean();
		return new SPacketDate(dateData, displayNewDate);
	}

	public static void encode(SPacketDate packet, PacketBuffer buf) {
		buf.writeNbt(packet.dateData);
		buf.writeBoolean(packet.displayNewDate);
	}

	public static void handle(SPacketDate packet, Supplier context) {
		LOTRDate.loadDates(packet.dateData);
		if (packet.displayNewDate) {
			LOTRMod.PROXY.displayNewDate();
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
