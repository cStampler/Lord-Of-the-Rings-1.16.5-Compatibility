package lotr.common.speech;

import java.util.List;

import com.google.common.collect.ImmutableList;

import lotr.common.LOTRLog;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.network.*;
import lotr.curuquesta.SpeechbankContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.util.ResourceLocation;

public class NPCSpeechSender {
	public static void sendMessageInContext(List players, NPCEntity npc, ResourceLocation speechbank) {
		long randomSpeechSeed = npc.getRandom().nextLong();
		players.forEach(player -> {
			NPCSpeechbankContext contextProvider = new NPCSpeechbankContext(npc, (PlayerEntity) player);
			SpeechbankContext context = LOTRSpeechbankEngine.INSTANCE.populateContext(contextProvider);
			LOTRLog.debug("Sending speechbank %s for NPC %s to player %s with context %s", speechbank, npc.getName().getString(), ((Entity) player).getName().getString(), context);
			SPacketSpeechbank packet = new SPacketSpeechbank(npc.getId(), speechbank, context, randomSpeechSeed);
			LOTRPacketHandler.sendTo(packet, (ServerPlayerEntity) player);
		});
	}

	public static void sendMessageInContext(ServerPlayerEntity player, NPCEntity npc, ResourceLocation speechbank) {
		sendMessageInContext(ImmutableList.of(player), npc, speechbank);
	}
}
