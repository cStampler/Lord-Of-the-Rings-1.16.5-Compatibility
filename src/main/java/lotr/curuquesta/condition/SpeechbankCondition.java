package lotr.curuquesta.condition;

import java.util.function.Function;
import java.util.function.Predicate;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.PredicateParser;

public abstract class SpeechbankCondition<T, C extends SpeechbankContextProvider> {
	private final String conditionName;
	private final Function<C, T> valueFromContext;
	private final PredicateParser<T> predicateParser;

	protected SpeechbankCondition(String conditionName, Function<C, T> valueFromContext, PredicateParser<T> predicateParser) {
		this.conditionName = conditionName;
		this.valueFromContext = valueFromContext;
		this.predicateParser = predicateParser;
	}

	public String getConditionName() {
		return this.conditionName;
	}

	public final T getValueFromContext(C contextProvider) {
		return this.valueFromContext.apply(contextProvider);
	}

	public abstract boolean isValidValue(T var1);

	public final Predicate<T> parsePredicateFromJsonString(String s) {
		return this.predicateParser.parsePredicateFromString(s);
	}

	protected abstract T readValue(ByteBuf var1);

	protected abstract void writeValue(T var1, ByteBuf var2);
}
