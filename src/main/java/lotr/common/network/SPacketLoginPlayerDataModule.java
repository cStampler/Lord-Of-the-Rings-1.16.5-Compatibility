package lotr.common.network;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketLoginPlayerDataModule {
	private final String moduleCode;
	private final byte[] moduleData;

	private SPacketLoginPlayerDataModule(String module, byte[] data) {
		moduleCode = module;
		moduleData = data;
	}

	public SPacketLoginPlayerDataModule(String module, PacketBuffer data) {
		moduleCode = module;
		moduleData = new byte[data.readableBytes()];
		data.getBytes(0, moduleData);
	}

	public static SPacketLoginPlayerDataModule decode(PacketBuffer buf) {
		String moduleCode = buf.readUtf();
		byte[] moduleData = buf.readByteArray();
		return new SPacketLoginPlayerDataModule(moduleCode, moduleData);
	}

	public static void encode(SPacketLoginPlayerDataModule packet, PacketBuffer buf) {
		buf.writeUtf(packet.moduleCode);
		buf.writeByteArray(packet.moduleData);
	}

	public static void handle(SPacketLoginPlayerDataModule packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		PacketBuffer dataBuffer = new PacketBuffer(Unpooled.copiedBuffer(packet.moduleData));
		pd.receiveLoginData(packet.moduleCode, dataBuffer);
		((Context) context.get()).setPacketHandled(true);
	}
}
