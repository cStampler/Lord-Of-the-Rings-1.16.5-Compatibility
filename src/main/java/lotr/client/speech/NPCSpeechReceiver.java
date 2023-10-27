/*
 * Decompiled with CFR 0.148.
 *
 * Could not load the following classes:
 *  net.minecraft.client.entity.player.ClientPlayerEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.IFormattableTextComponent
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.StringTextComponent
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.util.text.TranslationTextComponent
 *  net.minecraft.world.World
 */
package lotr.client.speech;

import java.util.Random;

import lotr.client.LOTRClientProxy;
import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.event.SpeechGarbler;
import lotr.common.network.SPacketSpeechbank;
import lotr.curuquesta.SpeechbankContext;
import lotr.curuquesta.structure.Speechbank;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class NPCSpeechReceiver {
	private static final Random SEEDED_SPEECH_RAND = new Random();
	private static final SpeechGarbler SPEECH_GARBLER = new SpeechGarbler();

	private static void addSpeechMessage(PlayerEntity player, NPCEntity npc, ResourceLocation speechbank, SpeechbankContext context, long randomSpeechSeed, boolean forceChatLog) {
		String speechLine = NPCSpeechReceiver.getSpeechbankLine(speechbank, context, randomSpeechSeed, npc);
		if ((Boolean) LOTRConfig.CLIENT.immersiveSpeech.get()) {
			ImmersiveSpeech.receiveSpeech(npc, speechLine);
		}
		if (!((Boolean) LOTRConfig.CLIENT.immersiveSpeech.get()).booleanValue() || ((Boolean) LOTRConfig.CLIENT.immersiveSpeechChatLog.get()).booleanValue() || forceChatLog) {
			ITextComponent speechComponent = NPCSpeechReceiver.formatSpeechLineForNPC(npc, speechLine);
			player.sendMessage(speechComponent, npc.getUUID());
		}
	}

	private static String applyGarbling(String line, NPCEntity npc) {
		if (SpeechGarbler.isEnabledInConfig() && npc.isDrunk()) {
			float f = npc.getDrunkenSpeechFactor();
			line = SPEECH_GARBLER.garbleString(line, f);
		}
		return line;
	}

	private static ITextComponent formatSpeechLineForNPC(NPCEntity npc, String speechLine) {
		StringTextComponent speechComponent = new StringTextComponent(speechLine);
		IFormattableTextComponent nameComponent = new TranslationTextComponent("<%s>", npc.getName()).withStyle(TextFormatting.YELLOW);
		return new TranslationTextComponent("%s %s", nameComponent, speechComponent);
	}

	private static Speechbank getSpeechbank(ResourceLocation speechbank) {
		return LOTRClientProxy.getSpeechbankResourceManager().getSpeechbank(speechbank);
	}

	private static String getSpeechbankLine(ResourceLocation speechbank, SpeechbankContext context, long randomSpeechSeed, NPCEntity npc) {
		SEEDED_SPEECH_RAND.setSeed(randomSpeechSeed);
		String line = NPCSpeechReceiver.getSpeechbank(speechbank).getRandomSpeech(context, SEEDED_SPEECH_RAND);
		return NPCSpeechReceiver.applyGarbling(line, npc);
	}

	public static void receiveSpeech(World world, ClientPlayerEntity player, SPacketSpeechbank packet) {
		int eId = packet.entityId;
		Entity entity = world.getEntity(eId);
		if (entity instanceof NPCEntity) {
			NPCEntity npc = (NPCEntity) entity;
			LOTRLog.debug("Received speechbank %s for NPC %s with context %s", packet.speechbank, npc.getName().getString(), packet.context);
			NPCSpeechReceiver.addSpeechMessage(player, npc, packet.speechbank, packet.context, packet.randomSpeechSeed, packet.forceChatLog);
		} else if (entity == null) {
			LOTRLog.warn("Received speechbank packet on behalf of entity with ID %d, but the entity does not exist on the client side!");
		} else {
			LOTRLog.warn("Received speechbank packet on behalf of entity with ID %d, but the entity is not an NPC - it is %s!", eId, entity.getName().getString());
		}
	}
}
