package lotr.curuquesta.structure;

import java.util.List;

import lotr.curuquesta.*;

public class AlternativeConditionSets<C extends SpeechbankContextProvider> implements SpeechbankContextSatisfier<C> {
	private final List<SpeechbankConditionSet<C>> alternatives;

	public AlternativeConditionSets(List<SpeechbankConditionSet<C>> alternatives) {
		this.alternatives = alternatives;
	}

	@Override
	public boolean satisfiesContext(SpeechbankContext<C> context) {
		return this.alternatives.stream().anyMatch(set -> set.satisfiesContext(context));
	}
}
