package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.speech.LOTRSpeechbankEngine;
import lotr.curuquesta.SpeechbankContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketSpeechbank {
	public final int entityId;
	public final ResourceLocation speechbank;
	public final SpeechbankContext context;
	public final long randomSpeechSeed;
	public final boolean forceChatLog;

	public SPacketSpeechbank(int entityId, ResourceLocation speechbank, SpeechbankContext context, long randomSpeechSeed) {
		this(entityId, speechbank, context, randomSpeechSeed, false);
	}

	public SPacketSpeechbank(int entityId, ResourceLocation speechbank, SpeechbankContext context, long randomSpeechSeed, boolean forceChatLog) {
		this.entityId = entityId;
		this.speechbank = speechbank;
		this.randomSpeechSeed = randomSpeechSeed;
		this.context = context;
		this.forceChatLog = forceChatLog;
	}

	public static SPacketSpeechbank decode(PacketBuffer buf) {
		int entityId = buf.readVarInt();
		ResourceLocation speechbank = buf.readResourceLocation();
		SpeechbankContext context = LOTRSpeechbankEngine.SERIALIZER.read(buf);
		long randomSpeechSeed = buf.readLong();
		boolean forceChatLog = buf.readBoolean();
		return new SPacketSpeechbank(entityId, speechbank, context, randomSpeechSeed, forceChatLog);
	}

	public static void encode(SPacketSpeechbank packet, PacketBuffer buf) {
		buf.writeVarInt(packet.entityId);
		buf.writeResourceLocation(packet.speechbank);
		LOTRSpeechbankEngine.SERIALIZER.write(packet.context, buf);
		buf.writeLong(packet.randomSpeechSeed);
		buf.writeBoolean(packet.forceChatLog);
	}

	public static void handle(SPacketSpeechbank packet, Supplier context) {
		LOTRMod.PROXY.receiveSpeechbankPacket(packet);
		((Context) context.get()).setPacketHandled(true);
	}
}
