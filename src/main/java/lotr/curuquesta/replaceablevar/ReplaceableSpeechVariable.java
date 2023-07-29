package lotr.curuquesta.replaceablevar;

import java.util.function.Function;

import lotr.curuquesta.SpeechbankContextProvider;

public final class ReplaceableSpeechVariable<C extends SpeechbankContextProvider> {
	private static final String PREFIX = "#";
	private final String shortAliasLowercase;
	private final String longAliasLowercase;
	private final Function<C, String> valueFromContext;

	public ReplaceableSpeechVariable(String shortAlias, String longAlias, Function<C, String> valueFromContext) {
		if (shortAlias.length() >= longAlias.length()) {
			throw new IllegalArgumentException("The long alias must be longer than the short alias!");
		}
		this.shortAliasLowercase = shortAlias.toLowerCase();
		this.longAliasLowercase = longAlias.toLowerCase();
		this.valueFromContext = valueFromContext;
	}

	public boolean aliasMatches(ReplaceableSpeechVariable<?> other) {
		return this.shortAliasLowercase.equalsIgnoreCase(other.shortAliasLowercase) || this.shortAliasLowercase.equalsIgnoreCase(other.longAliasLowercase) || this.longAliasLowercase.equalsIgnoreCase(other.shortAliasLowercase) || this.longAliasLowercase.equalsIgnoreCase(other.longAliasLowercase);
	}

	public String fillMatchesInSpeechLine(String speechLine, String value) {
		return this.fillMatchesInSpeechLineFromIndex(speechLine, value, 0);
	}

	private String fillMatchesInSpeechLineFromIndex(String speechLine, String value, int fromIndex) {
		int nextPrefixIndex = speechLine.indexOf(PREFIX, fromIndex);
		if (nextPrefixIndex >= 0) {
			String textAfter = speechLine.substring(nextPrefixIndex + PREFIX.length());
			if (textAfter.toLowerCase().startsWith(this.longAliasLowercase)) {
				speechLine = speechLine.substring(0, nextPrefixIndex) + value + textAfter.substring(this.longAliasLowercase.length());
			} else if (textAfter.toLowerCase().startsWith(this.shortAliasLowercase)) {
				speechLine = speechLine.substring(0, nextPrefixIndex) + value + textAfter.substring(this.shortAliasLowercase.length());
			}
			speechLine = this.fillMatchesInSpeechLineFromIndex(speechLine, value, nextPrefixIndex + 1);
		}
		return speechLine;
	}

	public String getLongAlias() {
		return this.longAliasLowercase;
	}

	public String getShortAlias() {
		return this.shortAliasLowercase;
	}

	public String getValueFromContext(C contextProvider) {
		return this.valueFromContext.apply(contextProvider);
	}
}
