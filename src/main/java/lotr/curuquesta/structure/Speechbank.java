package lotr.curuquesta.structure;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.*;

import lotr.curuquesta.*;

public class Speechbank<C extends SpeechbankContextProvider> {
	private final String speechbankName;
	private final List<SpeechbankEntry<C>> entries;

	public Speechbank(String speechbankName, List<SpeechbankEntry<C>> entries) {
		this.speechbankName = speechbankName;
		this.entries = entries;
	}

	private String fillAllVariablesInLine(String line, SpeechbankContext<C> context) {
		String[] callback = { line };
		context.forEachReplaceableVariable((variable, value) -> {
			callback[0] = variable.fillMatchesInSpeechLine(callback[0], value);
		});
		return callback[0];
	}

	private List<String> filterMatchingLines(SpeechbankContext<C> context) {
		return this.entries.stream().filter(e -> e.doesContextSatisfyConditions(context)).flatMap(SpeechbankEntry::streamLines).collect(Collectors.toList());
	}

	public List<SpeechbankEntry<C>> getEntriesView() {
		return new ArrayList<>(this.entries);
	}

	public String getRandomSpeech(SpeechbankContext<C> context, Random rand) {
		List<String> matchingLines = this.filterMatchingLines(context);
		if (matchingLines.isEmpty()) {
			return String.format("Speechbank %s found no lines that satisfy the current context!", this.speechbankName);
		}
		String line = matchingLines.get(rand.nextInt(matchingLines.size()));
		return this.fillAllVariablesInLine(line, context);
	}

	public static <C extends SpeechbankContextProvider> Speechbank<C> getFallbackSpeechbank(String name, List<String> fallbackMessages) {
		return new Speechbank<>(name, ImmutableList.of(new SpeechbankEntry<C>(ImmutableSet.of(), fallbackMessages)));
	}
}
