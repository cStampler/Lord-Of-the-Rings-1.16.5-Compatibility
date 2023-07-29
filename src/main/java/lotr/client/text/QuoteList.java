/*
 * Decompiled with CFR 0.148.
 *
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package lotr.client.text;

import java.util.*;

import net.minecraft.util.ResourceLocation;

public class QuoteList {
	private final ResourceLocation path;
	private final List<String> quotes;

	public QuoteList(ResourceLocation path, List<String> quotes) {
		this.path = path;
		this.quotes = quotes;
	}

	public String getRandomQuote(Random rand) {
		if (quotes.isEmpty()) {
			return String.format("Quote list %s was empty!", path);
		}
		return quotes.get(rand.nextInt(quotes.size()));
	}
}
