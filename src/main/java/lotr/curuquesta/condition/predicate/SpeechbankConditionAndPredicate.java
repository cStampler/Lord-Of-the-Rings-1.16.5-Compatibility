package lotr.curuquesta.condition.predicate;

import java.util.function.Predicate;

import lotr.curuquesta.SpeechbankContext;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.structure.SpeechbankContextSatisfier;

public class SpeechbankConditionAndPredicate<T, C extends SpeechbankContextProvider> implements SpeechbankContextSatisfier<C> {
	private final SpeechbankCondition<T, C> condition;
	private final Predicate<T> predicate;

	private SpeechbankConditionAndPredicate(SpeechbankCondition<T, C> condition, Predicate<T> predicate) {
		this.condition = condition;
		this.predicate = predicate;
	}

	@Override
	public boolean satisfiesContext(SpeechbankContext<C> context) {
		return this.predicate.test(context.getConditionValue(this.condition));
	}

	public static <T, C extends SpeechbankContextProvider> SpeechbankConditionAndPredicate<T, C> of(SpeechbankCondition<T, C> condition, Predicate<T> predicate) {
		return new SpeechbankConditionAndPredicate<>(condition, predicate);
	}
}
