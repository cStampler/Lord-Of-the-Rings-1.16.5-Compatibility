package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketToggle extends SidedTogglePacket {
	public CPacketToggle(SidedTogglePacket.ToggleType type, boolean fieldValue) {
		super(type, fieldValue);
	}

	@Override
	protected LOTRPlayerData getSidedPlayerData(Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		return LOTRLevelData.serverInstance().getData(player);
	}

	public static CPacketToggle decode(PacketBuffer buf) {
		return (CPacketToggle) SidedTogglePacket.decode(buf, (hummel, hummel2) -> new CPacketToggle((ToggleType) hummel, (boolean) hummel2));
	}

	public static void encode(CPacketToggle packet, PacketBuffer buf) {
		SidedTogglePacket.encode(packet, buf);
	}
}
