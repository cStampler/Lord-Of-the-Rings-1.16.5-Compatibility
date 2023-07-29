package lotr.curuquesta.condition;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.*;

public class FloatSpeechbankCondition<C extends SpeechbankContextProvider> extends SpeechbankCondition<Float, C> {
	public FloatSpeechbankCondition(String conditionName, Function<C, Float> valueFromContext) {
		this(conditionName, valueFromContext, ComplexPredicateParsers.logicalExpressionOfComparableSubpredicates(Float::parseFloat));
	}

	public FloatSpeechbankCondition(String conditionName, Function<C, Float> valueFromContext, PredicateParser<Float> predicateParser) {
		super(conditionName, valueFromContext, predicateParser);
	}

	@Override
	public boolean isValidValue(Float value) {
		return value != null;
	}

	@Override
	protected Float readValue(ByteBuf buf) {
		return buf.readFloat();
	}

	@Override
	protected void writeValue(Float value, ByteBuf buf) {
		buf.writeFloat(value);
	}
}
