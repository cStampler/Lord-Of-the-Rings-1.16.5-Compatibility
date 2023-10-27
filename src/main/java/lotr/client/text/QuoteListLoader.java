/*
 * Decompiled with CFR 0.148.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.text.IFormattableTextComponent
 *  net.minecraft.util.text.StringTextComponent
 *  org.apache.commons.lang3.StringUtils
 */
package lotr.client.text;

import java.io.BufferedReader;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;

public class QuoteListLoader extends TranslatableTextReloadListener<QuoteList, ParentTextResourceLoader.NoopParentLoader> {
	private static final Random RANDOM = new Random();

	public QuoteListLoader(Minecraft mc) {
		super(mc, ".txt");
	}

	@Override
	protected ResourceLocation convertToFullResourcePath(ResourceLocation basePath) {
		return new ResourceLocation(basePath.getNamespace(), String.format("quotes/%s", basePath.getPath()));
	}

	@Override
	protected ParentTextResourceLoader.NoopParentLoader createNewParentLoader(ResourceLocation topLevelPath, String lang) {
		return new ParentTextResourceLoader.NoopParentLoader();
	}

	public String getRandomQuote(ResourceLocation basePath) {
		return this.getOrLoadTextResource(basePath).getRandomQuote(RANDOM);
	}

	public IFormattableTextComponent getRandomQuoteComponent(ResourceLocation basePath) {
		return new StringTextComponent(getRandomQuote(basePath));
	}

	@Override
	protected QuoteList loadErroredFallbackResource(ResourceLocation langPath, String errorMsg) {
		return new QuoteList(langPath, ImmutableList.of(errorMsg));
	}

	@Override
	protected QuoteList loadResource(ResourceLocation langPath, BufferedReader reader, ParentTextResourceLoader.NoopParentLoader parentLoader) {
		List<String> quotes = reader.lines().map(this::stripCommentsOut).map(String::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
		return new QuoteList(langPath, quotes);
	}

	private String stripCommentsOut(String line) {
		line = QuoteListLoader.stripCommentTypeOut(line, "#");
		return QuoteListLoader.stripCommentTypeOut(line, "//");
	}

	private static String stripCommentTypeOut(String line, String commentStart) {
		if (line.contains(commentStart)) {
			line = line.substring(0, line.indexOf(commentStart));
		}
		return line;
	}
}
