package lotr.common.fac;

import lotr.common.resources.GenderedTranslationDecomposer;

public class FactionRankNameDecomposer {
	private final GenderedTranslationDecomposer shortName;
	private final GenderedTranslationDecomposer fullName;

	private FactionRankNameDecomposer(String translatedName) {
		String[] shortAndFullNames = decomposeIntoShortAndFull(translatedName);
		shortName = GenderedTranslationDecomposer.actOn(shortAndFullNames[0]);
		fullName = GenderedTranslationDecomposer.actOn(shortAndFullNames[1]);
	}

	public String getFullName(RankGender gender) {
		return fullName.getName(gender.isMale());
	}

	public String getShortName(RankGender gender) {
		return shortName.getName(gender.isMale());
	}

	public static FactionRankNameDecomposer actOn(String translatedName) {
		return new FactionRankNameDecomposer(translatedName);
	}

	private static String[] decomposeIntoShortAndFull(String name) {
		return GenderedTranslationDecomposer.splitInHalfAndTrim(name, '|');
	}
}
