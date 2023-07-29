package lotr.common.fac;

public enum RankGender {
	MASCULINE("M"), FEMININE("F"), FLOPPA_CAT("FLOPPA_CAT");

	private final String saveName;
	public final int networkID;

	RankGender(String name) {
		saveName = name;
		networkID = ordinal();
	}

	public String getSaveName() {
		return saveName;
	}

	public boolean isFemale() {
		return this == FEMININE;
	}

	public boolean isMale() {
		return this == MASCULINE;
	}

	public static RankGender forNetworkID(int id) {
		RankGender[] var1 = values();
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			RankGender gender = var1[var3];
			if (gender.networkID == id) {
				return gender;
			}
		}

		return null;
	}

	public static RankGender forSaveName(String name) {
		RankGender[] var1 = values();
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			RankGender gender = var1[var3];
			if (gender.saveName.equals(name)) {
				return gender;
			}
		}

		return null;
	}
}
