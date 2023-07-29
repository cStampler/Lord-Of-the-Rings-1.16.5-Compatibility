package lotr.client.speech;

import java.util.*;
import java.util.function.BiConsumer;

import lotr.common.config.LOTRConfig;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.LivingEntity;

public class ImmersiveSpeech {
	private static final Map<Integer, TimedSpeech> speeches = new HashMap<>();

	public static void clearAll() {
		speeches.clear();
	}

	public static void forEach(BiConsumer<Integer, TimedSpeech> action) {
		speeches.forEach(action);
	}

	private static int getDisplayTime() {
		return LOTRUtil.secondsToTicks((Integer) LOTRConfig.CLIENT.immersiveSpeechDuration.get());
	}

	public static TimedSpeech getSpeechFor(LivingEntity entity) {
		return speeches.get(entity.getId());
	}

	public static boolean hasSpeech(LivingEntity entity) {
		return speeches.containsKey(entity.getId());
	}

	public static void receiveSpeech(LivingEntity entity, String speech) {
		speeches.put(entity.getId(), new TimedSpeech(speech, ImmersiveSpeech.getDisplayTime()));
	}

	public static void removeSpeech(int entityId) {
		speeches.remove(entityId);
	}

	public static void removeSpeech(LivingEntity entity) {
		ImmersiveSpeech.removeSpeech(entity.getId());
	}

	public static void update() {
		HashSet removes = new HashSet();
		speeches.forEach((id, speech) -> {
			speech.time--;
			if (speech.time <= 0) {
				removes.add(id);
			}
		});
		removes.forEach(speeches::remove);
	}

	public static class TimedSpeech {
		private final String speech;
		private int time;
		private final int maxTime;

		private TimedSpeech(String s, int i) {
			speech = s;
			time = i;
			maxTime = i;
		}

		public float getAge() {
			return (float) time / (float) maxTime;
		}

		public String getSpeech() {
			return speech;
		}
	}

}
