package lotr.curuquesta.condition;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.SpeechbankContextProvider;

public class SpeechbankConditionAndValue<T, C extends SpeechbankContextProvider> {
	private final SpeechbankCondition<T, C> condition;
	private final T value;

	private SpeechbankConditionAndValue(SpeechbankCondition<T, C> condition, T value) {
		this.condition = condition;
		this.value = value;
		if (!condition.isValidValue(value)) {
			throw new IllegalArgumentException("Speechbank condition " + condition.getConditionName() + " cannot accept a value of " + value + "!");
		}
	}

	public SpeechbankCondition<T, C> getCondition() {
		return this.condition;
	}

	public String getConditionName() {
		return this.condition.getConditionName();
	}

	public T getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return String.format("%s=%s", this.getCondition().getConditionName(), String.valueOf(this.getValue()));
	}

	public void writeValue(ByteBuf buf) {
		this.condition.writeValue(this.value, buf);
	}

	public static <T, C extends SpeechbankContextProvider> SpeechbankConditionAndValue<T, C> of(SpeechbankCondition<T, C> condition, T value) {
		return new SpeechbankConditionAndValue<>(condition, value);
	}

	public static <T, C extends SpeechbankContextProvider> SpeechbankConditionAndValue<T, C> readValue(SpeechbankCondition<T, C> condition, ByteBuf buf) {
		T value = condition.readValue(buf);
		return SpeechbankConditionAndValue.of(condition, value);
	}
}
