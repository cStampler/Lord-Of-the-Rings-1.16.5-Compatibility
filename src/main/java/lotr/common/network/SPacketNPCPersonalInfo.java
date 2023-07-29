package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.entity.npc.data.NPCPersonalInfo;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketNPCPersonalInfo extends ByteArrayPacket {
	private SPacketNPCPersonalInfo(byte[] data) {
		super(data);
	}

	public SPacketNPCPersonalInfo(NPCPersonalInfo personalInfo) {
		super(hummel -> personalInfo.write((PacketBuffer) hummel));
	}

	public static SPacketNPCPersonalInfo decode(PacketBuffer buf) {
		return (SPacketNPCPersonalInfo) decodeByteData(buf, hummel -> new SPacketNPCPersonalInfo((byte[]) hummel));
	}

	public static void encode(SPacketNPCPersonalInfo packet, PacketBuffer buf) {
		encodeByteData(packet, buf);
	}

	public static void handle(SPacketNPCPersonalInfo packet, Supplier context) {
		World world = LOTRMod.PROXY.getClientWorld();
		NPCPersonalInfo.read(packet.getBufferedByteData(), world);
		((Context) context.get()).setPacketHandled(true);
	}
}
