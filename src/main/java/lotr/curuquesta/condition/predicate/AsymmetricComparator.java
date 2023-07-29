package lotr.curuquesta.condition.predicate;

import java.util.OptionalInt;
import java.util.function.IntPredicate;

public interface AsymmetricComparator<T> {
	OptionalInt compare(T var1, T var2);

	default boolean compareAndTestResult(T objectInContext, T objectInPredicate, IntPredicate comparisonTest) {
		OptionalInt comparison = this.compare(objectInContext, objectInPredicate);
		return comparison.isPresent() && comparisonTest.test(comparison.getAsInt());
	}

	static <T extends Comparable<T>> AsymmetricComparator<T> naturalComparator() {
		return (objectInContext, objectInPredicate) -> OptionalInt.of(objectInContext.compareTo(objectInPredicate));
	}
}
