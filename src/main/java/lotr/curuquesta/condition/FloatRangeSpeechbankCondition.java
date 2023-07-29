package lotr.curuquesta.condition;

import java.util.function.Function;

import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.PredicateParser;

public class FloatRangeSpeechbankCondition<C extends SpeechbankContextProvider> extends FloatSpeechbankCondition<C> {
	private final float minValue;
	private final float maxValue;

	public FloatRangeSpeechbankCondition(String conditionName, Function<C, Float> valueFromContext, float minValue, float maxValue) {
		super(conditionName, valueFromContext);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public FloatRangeSpeechbankCondition(String conditionName, Function<C, Float> valueFromContext, PredicateParser<Float> predicateParser, float minValue, float maxValue) {
		super(conditionName, valueFromContext, predicateParser);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public boolean isValidValue(Float value) {
		return value != null && value.floatValue() >= this.minValue && value.floatValue() <= this.maxValue;
	}
}
