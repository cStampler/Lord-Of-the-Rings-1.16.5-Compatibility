package lotr.curuquesta.condition.predicate;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimplePredicateParsers {
	public static Predicate<Boolean> booleanEquality(String s) {
		boolean expected = Boolean.parseBoolean(s);
		return bVal -> bVal != null && bVal == expected;
	}

	public static <T> Predicate<T> dummyAlwaysMatch(String s) {
		return obj -> true;
	}

	public static <T> PredicateParser<T> genericEqualityParsingFromString(Function<String, T> objFromString) {
		return s -> {
			Object parsedObject = objFromString.apply(s);
			return obj -> Objects.equals(obj, parsedObject);
		};
	}

	public static <T> Predicate<T> genericToStringEquality(String s) {
		return obj -> String.valueOf(obj).equals(s);
	}
}
