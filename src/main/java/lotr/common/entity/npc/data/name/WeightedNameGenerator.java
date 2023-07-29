package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.WeightedList;

public class WeightedNameGenerator implements NPCNameGenerator {
	private final WeightedList generators;

	private WeightedNameGenerator(WeightedList generators) {
		this.generators = generators;
	}

	// $FF: synthetic method
	WeightedNameGenerator(WeightedList x0, Object x1) {
		this(x0);
	}

	@Override
	public String generateName(Random rand, boolean male) {
		return ((NPCNameGenerator) generators.getOne(rand)).generateName(rand, male);
	}

	public static WeightedNameGenerator.WeightedNameGeneratorBuilder builder() {
		return new WeightedNameGenerator.WeightedNameGeneratorBuilder();
	}

	public static class WeightedNameGeneratorBuilder {
		private final WeightedList generators = new WeightedList();

		public WeightedNameGenerator.WeightedNameGeneratorBuilder add(NPCNameGenerator generator, int weight) {
			generators.add(generator, weight);
			return this;
		}

		public WeightedNameGenerator build() {
			return new WeightedNameGenerator(generators);
		}
	}
}
