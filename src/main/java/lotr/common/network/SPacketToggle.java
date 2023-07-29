package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import net.minecraft.network.PacketBuffer;

public class SPacketToggle extends SidedTogglePacket {
	public SPacketToggle(SidedTogglePacket.ToggleType type, boolean fieldValue) {
		super(type, fieldValue);
	}

	@Override
	protected LOTRPlayerData getSidedPlayerData(Supplier context) {
		return LOTRLevelData.clientInstance().getData(LOTRMod.PROXY.getClientPlayer());
	}

	public static SPacketToggle decode(PacketBuffer buf) {
		return (SPacketToggle) SidedTogglePacket.decode(buf, (hummel, hummel2) -> new SPacketToggle((ToggleType) hummel, (boolean) hummel2));
	}

	public static void encode(SPacketToggle packet, PacketBuffer buf) {
		SidedTogglePacket.encode(packet, buf);
	}
}
