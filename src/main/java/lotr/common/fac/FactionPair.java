package lotr.common.fac;

public class FactionPair {
	private final Faction fac1;
	private final Faction fac2;

	private FactionPair(Faction f1, Faction f2) {
		fac1 = f1;
		fac2 = f2;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof FactionPair) {
			FactionPair otherPair = (FactionPair) obj;
			if (fac1 == otherPair.fac1 && fac2 == otherPair.fac2 || fac1 == otherPair.fac2 && fac2 == otherPair.fac1) {
				return true;
			}
		}

		return false;
	}

	public Faction getFirst() {
		return fac1;
	}

	public Faction getSecond() {
		return fac2;
	}

	@Override
	public int hashCode() {
		int f1 = fac1.getAssignedId();
		int f2 = fac2.getAssignedId();
		int lower = Math.min(f1, f2);
		int upper = Math.max(f1, f2);
		return upper << 16 | lower;
	}

	public static FactionPair of(Faction f1, Faction f2) {
		return new FactionPair(f1, f2);
	}
}
