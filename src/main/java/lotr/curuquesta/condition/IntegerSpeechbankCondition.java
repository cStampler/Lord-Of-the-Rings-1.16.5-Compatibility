package lotr.curuquesta.condition;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.*;

public class IntegerSpeechbankCondition<C extends SpeechbankContextProvider> extends SpeechbankCondition<Integer, C> {
	public IntegerSpeechbankCondition(String conditionName, Function<C, Integer> valueFromContext) {
		this(conditionName, valueFromContext, ComplexPredicateParsers.logicalExpressionOfComparableSubpredicates(Integer::parseInt));
	}

	public IntegerSpeechbankCondition(String conditionName, Function<C, Integer> valueFromContext, PredicateParser<Integer> predicateParser) {
		super(conditionName, valueFromContext, predicateParser);
	}

	@Override
	public boolean isValidValue(Integer value) {
		return value != null;
	}

	@Override
	protected Integer readValue(ByteBuf buf) {
		return buf.readInt();
	}

	@Override
	protected void writeValue(Integer value, ByteBuf buf) {
		buf.writeInt(value);
	}
}
