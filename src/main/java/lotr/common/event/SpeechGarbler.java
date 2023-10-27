package lotr.common.event;

import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.ServerChatEvent;

public class SpeechGarbler {
	private final Random rand = new Random(13541482055L);

	public String garbleString(String speech, float chance) {
		StringBuilder newSpeech = new StringBuilder();

		for (int i = 0; i < speech.length(); ++i) {
			String s = speech.substring(i, i + 1);
			if (rand.nextFloat() < chance) {
				s = "";
			} else if (rand.nextFloat() < chance * 0.4F) {
				s = s + " *hic* ";
			}

			newSpeech.append(s);
		}

		return StringUtils.normalizeSpace(newSpeech.toString());
	}

	public void handle(ServerChatEvent event) {
		ServerPlayerEntity player = event.getPlayer();
		String message = event.getMessage();
		event.getUsername();
		ITextComponent chatComponent = event.getComponent();
		if (isEnabledInConfig()) {
			EffectInstance nausea = player.getEffect(Effects.CONFUSION);
			if (nausea != null) {
				int duration = nausea.getDuration();
				float chance = duration / 4800.0F;
				chance = Math.min(chance, 1.0F);
				chance *= 0.4F;
				if (chatComponent instanceof TranslationTextComponent) {
					TranslationTextComponent ttc = (TranslationTextComponent) chatComponent;
					String key = ttc.getKey();
					Object[] formatArgs = ttc.getArgs();

					for (int a = 0; a < formatArgs.length; ++a) {
						Object arg = formatArgs[a];
						String chatText = null;
						if (arg instanceof StringTextComponent) {
							StringTextComponent stc = (StringTextComponent) arg;
							chatText = stc.getContents();
						} else if (arg instanceof String) {
							chatText = (String) arg;
						}

						if (chatText != null && chatText.equals(message)) {
							String newText = garbleString(chatText, chance);
							if (arg instanceof String) {
								formatArgs[a] = newText;
							} else if (arg instanceof StringTextComponent) {
								formatArgs[a] = new StringTextComponent(newText);
							}
						}
					}

					TranslationTextComponent newComponent = new TranslationTextComponent(key, formatArgs);
					newComponent.setStyle(chatComponent.getStyle());
					chatComponent = newComponent;
				} else {
					LOTRLog.warn("SpeechGarbler expected a TranslationTextComponent, instead got a " + chatComponent.getClass().getName());
				}
			}
		}

		event.setComponent(chatComponent);
	}

	public static boolean isEnabledInConfig() {
		return (Boolean) LOTRConfig.COMMON.drunkSpeech.get();
	}
}
