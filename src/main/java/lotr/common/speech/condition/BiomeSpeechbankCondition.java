package lotr.common.speech.condition;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.condition.predicate.*;

public class BiomeSpeechbankCondition extends SpeechbankCondition {
	public static final String HOME = "#home";
	public static final String FOREIGN = "#foreign";

	public BiomeSpeechbankCondition(String conditionName, Function valueFromContext) {
		super(conditionName, valueFromContext, parsePredicate());
	}

	@Override
	public boolean isValidValue(Object value) {
		return value != null;
	}

	@Override
	protected BiomeWithTags readValue(ByteBuf buf) {
		return BiomeWithTags.read(buf);
	}

	@Override
	protected void writeValue(Object value, ByteBuf buf) {
		((BiomeWithTags) value).write(buf);
	}

	private static PredicateParser parsePredicate() {
		return ComplexPredicateParsers.logicalOrOfSubpredicates(elem -> {
			if ("#home".equalsIgnoreCase(elem)) {
				return hummel -> ((BiomeWithTags) hummel).isHomeBiome();
			}
			return "#foreign".equalsIgnoreCase(elem) ? hummel -> ((BiomeWithTags) hummel).isForeignBiome() : biomeWithTags -> ((BiomeWithTags) biomeWithTags).getBiomeName().toString().equals(elem);
		});
	}
}
