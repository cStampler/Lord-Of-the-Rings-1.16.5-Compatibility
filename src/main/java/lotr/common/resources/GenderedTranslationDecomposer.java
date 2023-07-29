package lotr.common.resources;

public class GenderedTranslationDecomposer {
	private final String mascName;
	private final String femName;

	private GenderedTranslationDecomposer(String translatedName) {
		String[] mascAndFemNames = decomposeIntoMascAndFem(translatedName);
		mascName = mascAndFemNames[0];
		femName = mascAndFemNames[1];
	}

	public String getName(boolean isMale) {
		return isMale ? mascName : femName;
	}

	public static GenderedTranslationDecomposer actOn(String translatedName) {
		return new GenderedTranslationDecomposer(translatedName);
	}

	private static String[] decomposeIntoMascAndFem(String name) {
		return splitInHalfAndTrim(name, '~');
	}

	public static String[] splitInHalfAndTrim(String name, char splitter) {
		int splitIndex = name.indexOf(splitter);
		if (splitIndex < 0) {
			return new String[] { name, name };
		}
		String first = name.substring(0, splitIndex);
		String second = name.substring(splitIndex + 1);
		return new String[] { first.trim(), second.trim() };
	}
}
