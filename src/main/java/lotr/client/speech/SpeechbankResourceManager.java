package lotr.client.speech;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;

import lotr.client.speech.SpeechbankResourceManager.ParentSpeechbankLoader;
import lotr.client.text.*;
import lotr.common.speech.*;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.structure.Speechbank;
import net.minecraft.client.Minecraft;
import net.minecraft.util.*;

public class SpeechbankResourceManager extends TranslatableTextReloadListener<Speechbank<NPCSpeechbankContext>, ParentSpeechbankLoader<NPCSpeechbankContext>> {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static final List<String> FALLBACK_MESSAGES = ImmutableList.of("I don't have anything to say to you yet.", "Come back later for some fine conversation!", "They haven't given me speech banks yet.", "They haven't given me speech banks yet. But I do have these fine new speaking animations.", "Conditional speechbank engine - now available in a mod near you!", "$ERROR_UNKNOWN_TYPE$: SPEECHBANK NOT FOUND! No, no, just joking. But they really haven't given me any yet!", "Nopa is Based.", "I speak when I am spoken to. But I don't have much to say yet.", "What? If people were always clicking on you, you'd have trouble coming up with things to say too.", "SPEECH ERROR: java.lang.NullPointerException at NPCEn... no, that won't work on you, you're too good for that.", "Curuquesta: better than all the resta.");
	private final SpeechbankLoader<NPCSpeechbankContext> loader = new SpeechbankLoader<NPCSpeechbankContext>(LOTRSpeechbankEngine.INSTANCE);

	public SpeechbankResourceManager(Minecraft mc) {
		super(mc, ".json");
	}

	@Override
	protected ResourceLocation convertToFullResourcePath(ResourceLocation basePath) {
		return new ResourceLocation(basePath.getNamespace(), String.format("speech/%s", basePath.getPath()));
	}

	@Override
	protected ParentSpeechbankLoader<NPCSpeechbankContext> createNewParentLoader(ResourceLocation topLevelPath, String lang) {
		return new ParentSpeechbankLoader<>(topLevelPath, lang, this::convertToFullResourcePath, (parentPath, parentLang, parentSpeechbankLoader) -> (Speechbank) this.getOrLoadTextResource(parentPath, parentLang, false, parentSpeechbankLoader));
	}

	public Speechbank<NPCSpeechbankContext> getSpeechbank(ResourceLocation speechPath) {
		return this.getOrLoadTextResource(speechPath);
	}

	@Override
	protected Speechbank loadErroredFallbackResource(ResourceLocation langPath, String errorMsg) {
		return Speechbank.getFallbackSpeechbank(langPath.toString(), FALLBACK_MESSAGES);
	}

	@Override
	protected Speechbank<NPCSpeechbankContext> loadResource(ResourceLocation langPath, BufferedReader reader, ParentSpeechbankLoader<NPCSpeechbankContext> parentLoader) {
		JsonObject jsonObj = JSONUtils.fromJson(GSON, (Reader) reader, JsonObject.class);
		return loader.load(langPath, jsonObj, parentLoader);
	}

	public static class ParentSpeechbankLoader<C extends SpeechbankContextProvider> extends ParentTextResourceLoader<Speechbank<C>> {
		private final Function<ResourceLocation, ResourceLocation> fullPathConverter;
		private final ParentSpeechbankGetter<C> parentSpeechbankGetter;

		public ParentSpeechbankLoader(ResourceLocation topLevelSpeechbank, String langCode, Function<ResourceLocation, ResourceLocation> fullPathConverter, ParentSpeechbankGetter<C> parentSpeechbankGetter) {
			super(topLevelSpeechbank, langCode);
			this.fullPathConverter = fullPathConverter;
			this.parentSpeechbankGetter = parentSpeechbankGetter;
		}

		@Override
		protected IllegalArgumentException createCircularReferenceException(ResourceLocation topLevelPath, ResourceLocation parentPath) {
			return new IllegalArgumentException(String.format("Circular reference in speechbank %s parent tree! Speechbank %s already included", topLevelPath, parentPath));
		}

		@Override
		public Optional<Speechbank<C>> getOrLoadParentResource(ResourceLocation parent) {
			checkInheritanceRecord(this.fullPathConverter.apply(parent));
			return Optional.ofNullable(this.parentSpeechbankGetter.getSpeechbank(parent, langCode, this));
		}

		@FunctionalInterface
		public interface ParentSpeechbankGetter<C extends SpeechbankContextProvider> {
			Speechbank<C> getSpeechbank(ResourceLocation var1, String var2, ParentSpeechbankLoader<C> var3);
		}

	}

}
