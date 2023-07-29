package lotr.curuquesta.condition.predicate;

import java.util.function.Predicate;

@FunctionalInterface
public interface PredicateParser<T> {
	Predicate<T> parsePredicateFromString(String var1);
}
