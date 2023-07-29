package lotr.common.speech;

import lotr.common.LOTRLog;
import lotr.curuquesta.*;
import lotr.curuquesta.condition.*;
import lotr.curuquesta.replaceablevar.ReplaceableSpeechVariable;
import net.minecraft.network.PacketBuffer;

public class SpeechbankContextSerializer {
	private final SpeechbankEngine engine;

	public SpeechbankContextSerializer(SpeechbankEngine engine) {
		this.engine = engine;
	}

	public SpeechbankContext read(PacketBuffer buf) {
		SpeechbankContext context = SpeechbankContext.newContext();
		int numConditions = buf.readVarInt();

		int numReplaceableVariables;
		for (numReplaceableVariables = 0; numReplaceableVariables < numConditions; ++numReplaceableVariables) {
			String conditionName = buf.readUtf();
			SpeechbankCondition condition = engine.getCondition(conditionName);
			if (condition == null) {
				LOTRLog.warn("Received speechbank context from server with an unknown condition name '%s! Exiting read now to prevent continuing to read malformed data", conditionName);
				return context;
			}

			SpeechbankConditionAndValue conditionAndValue = SpeechbankConditionAndValue.readValue(condition, buf);
			context.withCondition(conditionAndValue);
		}

		numReplaceableVariables = buf.readVarInt();

		for (int i = 0; i < numReplaceableVariables; ++i) {
			String shortAlias = buf.readUtf();
			String value = buf.readUtf();
			ReplaceableSpeechVariable variable = engine.getReplaceableVariableByShortAlias(shortAlias);
			if (variable == null) {
				LOTRLog.warn("Received speechbank context from server with an unknown replaceable variable alias '%s'! Exiting read now to prevent continuing to read malformed data", shortAlias);
				return context;
			}

			context.withReplaceableVariable(variable, value);
		}

		return context;
	}

	public void write(SpeechbankContext context, PacketBuffer buf) {
		buf.writeVarInt(context.getNumConditions());
		context.forEachCondition(conditionAndValue -> {
			buf.writeUtf(((SpeechbankConditionAndValue) conditionAndValue).getCondition().getConditionName());
			((SpeechbankConditionAndValue) conditionAndValue).writeValue(buf);
		});
		buf.writeVarInt(context.getNumReplaceableVariables());
		context.forEachReplaceableVariable((variable, value) -> {
			buf.writeUtf(((ReplaceableSpeechVariable) variable).getShortAlias());
			buf.writeUtf((String) value);
		});
	}
}
