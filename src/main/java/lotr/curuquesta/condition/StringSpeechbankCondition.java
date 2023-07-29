package lotr.curuquesta.condition;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.PredicateParser;
import lotr.curuquesta.util.StringSerializer;

public class StringSpeechbankCondition<C extends SpeechbankContextProvider> extends SpeechbankCondition<String, C> {
	public StringSpeechbankCondition(String conditionName, Function<C, String> valueFromContext, PredicateParser<String> predicateParser) {
		super(conditionName, valueFromContext, predicateParser);
	}

	@Override
	public boolean isValidValue(String value) {
		return value != null && !value.isEmpty();
	}

	@Override
	protected String readValue(ByteBuf buf) {
		return StringSerializer.read(buf);
	}

	@Override
	protected void writeValue(String value, ByteBuf buf) {
		StringSerializer.write(value, buf);
	}
}
