package lotr.common.speech.condition;

import java.util.function.*;

import io.netty.buffer.ByteBuf;
import lotr.common.entity.npc.data.*;
import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.condition.predicate.ComplexPredicateParsers;
import net.minecraft.network.PacketBuffer;

public class PersonalitySpeechbankCondition extends SpeechbankCondition {
	public PersonalitySpeechbankCondition(String conditionName, Function valueFromContext) {
		super(conditionName, valueFromContext, ComplexPredicateParsers.logicalExpressionOfSubpredicates(PersonalitySpeechbankCondition::parsePersonalityPredicate));
	}

	@Override
	public boolean isValidValue(Object value) {
		return value != null;
	}

	@Override
	protected PersonalityTraits readValue(ByteBuf buf) {
		return PersonalityTraits.read(new PacketBuffer(buf));
	}

	@Override
	protected void writeValue(Object value, ByteBuf buf) {
		((PersonalityTraits) value).write(new PacketBuffer(buf));
	}

	private static Predicate parsePersonalityPredicate(String elem) {
		return personalityTraits -> {
			PersonalityTrait mainTrait = PersonalityTrait.fromMainName(elem);
			if (mainTrait != null) {
				return ((PersonalityTraits) personalityTraits).hasTrait(mainTrait);
			}
			PersonalityTrait oppositeTrait = PersonalityTrait.fromOppositeName(elem);
			if (oppositeTrait != null) {
				return ((PersonalityTraits) personalityTraits).hasOppositeTrait(oppositeTrait);
			}
			throw new IllegalArgumentException("Personality trait name '" + elem + "' does not refer to any known trait or opposite!");
		};
	}
}
