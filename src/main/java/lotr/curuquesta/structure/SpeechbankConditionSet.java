package lotr.curuquesta.structure;

import java.util.Set;

import lotr.curuquesta.SpeechbankContext;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.SpeechbankConditionAndPredicate;

public class SpeechbankConditionSet<C extends SpeechbankContextProvider> implements SpeechbankContextSatisfier<C> {
	private final Set<SpeechbankConditionAndPredicate<?, C>> conditionsAndPredicates;

	public SpeechbankConditionSet(Set<SpeechbankConditionAndPredicate<?, C>> conditionsAndPredicates) {
		this.conditionsAndPredicates = conditionsAndPredicates;
	}

	@Override
	public boolean satisfiesContext(SpeechbankContext<C> context) {
		return this.conditionsAndPredicates.stream().allMatch(p -> p.satisfiesContext(context));
	}
}
