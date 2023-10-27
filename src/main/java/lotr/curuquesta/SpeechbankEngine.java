package lotr.curuquesta;

import java.util.HashMap;
import java.util.Map;

import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.replaceablevar.ReplaceableSpeechVariable;

public class SpeechbankEngine<C extends SpeechbankContextProvider> {
	private final Map<String, SpeechbankCondition<?, C>> conditions = new HashMap<>();
	private final Map<String, ReplaceableSpeechVariable<C>> replaceableVariablesByShortAlias = new HashMap<>();

	private SpeechbankEngine() {
	}

	public SpeechbankCondition<?, C> getCondition(String name) {
		return this.conditions.get(name);
	}

	public ReplaceableSpeechVariable<C> getReplaceableVariableByShortAlias(String shortAlias) {
		return this.replaceableVariablesByShortAlias.get(shortAlias);
	}

	public SpeechbankContext populateContext(C contextProvider) {
		SpeechbankContext context = SpeechbankContext.newContext();
		this.conditions.values().forEach(condition -> {
			context.withCondition(condition, condition.getValueFromContext(contextProvider));
		});
		this.replaceableVariablesByShortAlias.values().forEach(variable -> {
			context.withReplaceableVariable(variable, variable.getValueFromContext(contextProvider));
		});
		return context;
	}

	public SpeechbankEngine<C> registerCondition(SpeechbankCondition<?, C> condition) {
		String name = condition.getConditionName();
		if (this.conditions.containsKey(name)) {
			throw new IllegalStateException(String.format("Speechbank condition %s is already registered", name));
		}
		this.conditions.put(name, condition);
		return this;
	}

	public SpeechbankEngine<C> registerReplaceableVariable(ReplaceableSpeechVariable<C> variable) {
		if (this.replaceableVariablesByShortAlias.values().stream().anyMatch(v -> v.aliasMatches(variable))) {
			throw new IllegalStateException(String.format("Speech variable %s conflicts with an already-registered alias", variable));
		}
		this.replaceableVariablesByShortAlias.put(variable.getShortAlias(), variable);
		return this;
	}

	public static <C extends SpeechbankContextProvider> SpeechbankEngine<C> createInstance() {
		return new SpeechbankEngine<>();
	}
}
