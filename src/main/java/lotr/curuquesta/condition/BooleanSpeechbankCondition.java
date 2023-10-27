package lotr.curuquesta.condition;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.PredicateParser;
import lotr.curuquesta.condition.predicate.SimplePredicateParsers;

public class BooleanSpeechbankCondition<C extends SpeechbankContextProvider> extends SpeechbankCondition<Boolean, C> {
	public BooleanSpeechbankCondition(String conditionName, Function<C, Boolean> valueFromContext) {
		this(conditionName, valueFromContext, SimplePredicateParsers::booleanEquality);
	}

	public BooleanSpeechbankCondition(String conditionName, Function<C, Boolean> valueFromContext, PredicateParser<Boolean> predicateParser) {
		super(conditionName, valueFromContext, predicateParser);
	}

	@Override
	public boolean isValidValue(Boolean value) {
		return value != null;
	}

	@Override
	protected Boolean readValue(ByteBuf buf) {
		return buf.readBoolean();
	}

	@Override
	protected void writeValue(Boolean value, ByteBuf buf) {
		buf.writeBoolean(value);
	}
}
