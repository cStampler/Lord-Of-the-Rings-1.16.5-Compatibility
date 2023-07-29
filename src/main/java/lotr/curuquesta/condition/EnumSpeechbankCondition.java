package lotr.curuquesta.condition;

import java.util.function.Function;
import java.util.stream.*;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.condition.predicate.*;

public class EnumSpeechbankCondition<E extends Enum<E>, C extends SpeechbankContextProvider> extends SpeechbankCondition<E, C> {
	private final E[] enumValues;

	public EnumSpeechbankCondition(String conditionName, E[] enumValues, Function<C, E> valueFromContext) {
		this(conditionName, enumValues, valueFromContext, ComplexPredicateParsers.logicalOrOfValues(s -> parseEnum(enumValues, s)));
	}

	public EnumSpeechbankCondition(String conditionName, E[] enumValues, Function<C, E> valueFromContext, PredicateParser<E> predicateParser) {
		super(conditionName, valueFromContext, predicateParser);
		this.enumValues = enumValues;
	}

	@Override
	public boolean isValidValue(E value) {
		return value != null;
	}

	@Override
	protected E readValue(ByteBuf buf) {
		int ordinal = buf.readInt();
		if (ordinal < 0 || ordinal >= this.enumValues.length) {
			ordinal = 0;
		}
		return this.enumValues[ordinal];
	}

	@Override
	protected void writeValue(E value, ByteBuf buf) {
		buf.writeInt(value.ordinal());
	}

	public static <E extends Enum<E>, C extends SpeechbankContextProvider> EnumSpeechbankCondition<E, C> enumWithComparableExpressions(String conditionName, E[] enumValues, Function<C, E> valueFromContext) {
		return new EnumSpeechbankCondition<>(conditionName, enumValues, valueFromContext, ComplexPredicateParsers.logicalExpressionOfComparableSubpredicates(s -> parseEnum(enumValues, s)));
	}

	private static <E extends Enum<?>> E parseEnum(E[] enumValues, String s) {
		return Stream.of(enumValues).filter(e -> e.name().equalsIgnoreCase(s)).findFirst().orElseThrow(() -> {
			String errorMsg = String.format("No such value '%s' in enum - acceptable values are [%s]", s, Stream.of(enumValues).map(e -> e.name().toLowerCase()).collect(Collectors.joining(", ")));
			return new IllegalArgumentException(errorMsg);
		});
	}
}
