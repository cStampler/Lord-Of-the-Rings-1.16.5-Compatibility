package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.entity.npc.ai.NPCTalkAnimations;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketNPCTalkAnimations extends ByteArrayPacket {
	private SPacketNPCTalkAnimations(byte[] data) {
		super(data);
	}

	public SPacketNPCTalkAnimations(NPCTalkAnimations talkAnimations) {
		super(hummel -> talkAnimations.write((PacketBuffer) hummel));
	}

	public static SPacketNPCTalkAnimations decode(PacketBuffer buf) {
		return (SPacketNPCTalkAnimations) decodeByteData(buf, hummel -> new SPacketNPCTalkAnimations((byte[]) hummel));
	}

	public static void encode(SPacketNPCTalkAnimations packet, PacketBuffer buf) {
		encodeByteData(packet, buf);
	}

	public static void handle(SPacketNPCTalkAnimations packet, Supplier context) {
		World world = LOTRMod.PROXY.getClientWorld();
		NPCTalkAnimations.read(packet.getBufferedByteData(), world);
		((Context) context.get()).setPacketHandled(true);
	}
}
