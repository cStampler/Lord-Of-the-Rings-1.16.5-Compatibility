package lotr.curuquesta.condition.predicate;

import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.stream.*;

public class ComplexPredicateParsers {
	public static <T extends Comparable<T>> PredicateParser<T> logicalExpressionOfComparableSubpredicates(Function<String, T> objFromString) {
		return ComplexPredicateParsers.logicalExpressionOfComparableSubpredicates(objFromString, AsymmetricComparator.naturalComparator());
	}

	public static <T> PredicateParser<T> logicalExpressionOfComparableSubpredicates(Function<String, T> objFromString, AsymmetricComparator<T> comparator) {
		return ComplexPredicateParsers.logicalExpressionOfSubpredicates(elem -> ComplexPredicateParsers.parseComparableSubpredicate(elem, objFromString, comparator));
	}

	public static <T> PredicateParser<T> logicalExpressionOfSubpredicates(PredicateParser<T> subpredicateFromString) {
		return ComplexPredicateParsers.logicalOrOfSubpredicates(elem -> ComplexPredicateParsers.parseLogicalAndOfSubpredicates(elem, subpredicateFromString));
	}

	public static <T> PredicateParser<T> logicalOrOfSubpredicates(PredicateParser<T> subpredicateFromString) {
		return s -> {
			List<Predicate<T>> anyOfSubpredicates = Stream.<String>of(s.split(Pattern.quote("|"))).map(String::trim).map(subpredicateFromString::parsePredicateFromString).collect(Collectors.toList());
			return obj -> anyOfSubpredicates.stream().anyMatch(predicate -> predicate.test(obj));
		};
	}

	public static <T> PredicateParser<T> logicalOrOfValues(Function<String, T> objFromString) {
		return ComplexPredicateParsers.logicalOrOfSubpredicates(elem -> obj -> Objects.equals(obj, objFromString.apply(elem)));
	}

	private static <T> Predicate<T> parseComparableSubpredicate(String elem, Function<String, T> objFromString, AsymmetricComparator<T> comparator) {
		if (elem.startsWith(">=")) {
			return obj -> comparator.compareAndTestResult(obj, objFromString.apply(elem.substring(">=".length())), result -> result >= 0);
		}
		if (elem.startsWith("<=")) {
			return obj -> comparator.compareAndTestResult(obj, objFromString.apply(elem.substring("<=".length())), result -> result <= 0);
		}
		if (elem.startsWith(">")) {
			return obj -> comparator.compareAndTestResult(obj, objFromString.apply(elem.substring(">".length())), result -> result > 0);
		}
		if (elem.startsWith("<")) {
			return obj -> comparator.compareAndTestResult(obj, objFromString.apply(elem.substring("<".length())), result -> result < 0);
		}
		String equalityElem = elem.startsWith("=") ? elem.substring("=".length()) : elem;
		return obj -> Objects.equals(obj, objFromString.apply(equalityElem));
	}

	private static <T> Predicate<T> parseLogicalAndOfSubpredicates(String elem, PredicateParser<T> subpredicateFromString) {
		List<Predicate<T>> logicalANDClauses = Stream.<String>of(elem.split(Pattern.quote("&"))).map(String::trim).map(subpredicateFromString::parsePredicateFromString).collect(Collectors.toList());
		Predicate<T> composedAND = null;
		for (Predicate<T> clause : logicalANDClauses) {
			if (composedAND == null) {
				composedAND = clause;
				continue;
			}
			composedAND = composedAND.and(clause);
		}
		return composedAND;
	}
}
