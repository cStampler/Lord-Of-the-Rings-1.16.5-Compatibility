package lotr.common.fac;

public class AlignmentPredicates {
	public static final AlignmentPredicate POSITIVE = greaterThan(0.0F);
	public static final AlignmentPredicate POSITIVE_OR_ZERO = greaterThanOrEqual(0.0F);
	public static final AlignmentPredicate NEGATIVE = lessThan(0.0F);
	public static final AlignmentPredicate NEGATIVE_OR_ZERO = lessThanOrEqual(0.0F);
	public static final AlignmentPredicate ZERO = equalTo(0.0F);

	public static AlignmentPredicate equalTo(float level) {
		return align -> (align == level);
	}

	public static AlignmentPredicate greaterThan(float level) {
		return align -> (align > level);
	}

	public static AlignmentPredicate greaterThanOrEqual(float level) {
		return align -> (align >= level);
	}

	public static AlignmentPredicate lessThan(float level) {
		return align -> (align < level);
	}

	public static AlignmentPredicate lessThanOrEqual(float level) {
		return align -> (align <= level);
	}
}
