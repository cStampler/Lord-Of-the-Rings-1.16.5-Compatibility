package lotr.curuquesta;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.condition.SpeechbankConditionAndValue;
import lotr.curuquesta.replaceablevar.ReplaceableSpeechVariable;

public class SpeechbankContext<C extends SpeechbankContextProvider> {
	private Map<String, SpeechbankConditionAndValue<?, C>> conditionValueMap = new HashMap<>();
	private Map<ReplaceableSpeechVariable<C>, String> replaceVariableValues = new HashMap<>();

	private SpeechbankContext() {
	}

	public void forEachCondition(Consumer<SpeechbankConditionAndValue<?, C>> action) {
		this.conditionValueMap.values().forEach(action);
	}

	public void forEachReplaceableVariable(BiConsumer<ReplaceableSpeechVariable<C>, String> action) {
		this.replaceVariableValues.entrySet().forEach(e -> action.accept(e.getKey(), e.getValue()));
	}

	public <T> T getConditionValue(SpeechbankCondition<T, C> condition) {
		if (!this.conditionValueMap.containsKey(condition.getConditionName())) {
			throw new IllegalStateException("Asked speechbank context for the value of condition " + condition + ", but no value was set!");
		}
		return (T) ((SpeechbankConditionAndValue<?, ?>) this.conditionValueMap.get(condition.getConditionName())).getValue();
	}

	public int getNumConditions() {
		return this.conditionValueMap.size();
	}

	public int getNumReplaceableVariables() {
		return this.replaceVariableValues.size();
	}

	@Override
	public String toString() {
		return String.format("SpeechbankContext: conditions = [%s], replaceable variables = [%s]", this.conditionValueMap.values().stream().sorted(Comparator.comparing(SpeechbankConditionAndValue::getConditionName)).map(SpeechbankConditionAndValue::toString).collect(Collectors.joining(", ")), this.replaceVariableValues.entrySet().stream().sorted(Comparator.comparing(e -> ((ReplaceableSpeechVariable<?>) e.getKey()).getLongAlias())).map(e -> String.format("%s=%s", ((ReplaceableSpeechVariable<?>) e.getKey()).getLongAlias(), e.getValue())).collect(Collectors.joining(", ")));
	}

	public <T> SpeechbankContext<C> withCondition(SpeechbankCondition<T, C> condition, T value) {
		return this.withCondition(SpeechbankConditionAndValue.of(condition, value));
	}

	public <T> SpeechbankContext<C> withCondition(SpeechbankConditionAndValue<T, C> conditionAndValue) {
		if (this.conditionValueMap.containsKey(conditionAndValue.getConditionName())) {
			throw new IllegalStateException("Speechbank context already has a value set for condition " + conditionAndValue.getConditionName());
		}
		this.conditionValueMap.put(conditionAndValue.getConditionName(), conditionAndValue);
		return this;
	}

	public SpeechbankContext<C> withReplaceableVariable(ReplaceableSpeechVariable<C> variable, String value) {
		if (this.replaceVariableValues.containsKey(variable)) {
			throw new IllegalStateException("Speechbank context already has a value set for variable " + variable);
		}
		this.replaceVariableValues.put(variable, value);
		return this;
	}

	public static <C extends SpeechbankContextProvider> SpeechbankContext<C> newContext() {
		return new SpeechbankContext<>();
	}
}
