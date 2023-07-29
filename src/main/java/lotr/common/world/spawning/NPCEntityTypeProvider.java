package lotr.common.world.spawning;

import java.util.*;
import java.util.stream.Stream;

import net.minecraft.entity.EntityType;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandom.Item;

public interface NPCEntityTypeProvider {
	Stream getAllPossibleTypes();

	EntityType getTypeToSpawn(Random var1);

	public static class Mixed implements NPCEntityTypeProvider {
		private final List mixedEntries;

		public Mixed(List mixedEntries) {
			this.mixedEntries = mixedEntries;
		}

		@Override
		public Stream getAllPossibleTypes() {
			return mixedEntries.stream().map(e -> ((MixedEntry) e).entityType);
		}

		@Override
		public EntityType getTypeToSpawn(Random rand) {
			return ((NPCEntityTypeProvider.MixedEntry) WeightedRandom.getRandomItem(rand, mixedEntries)).entityType;
		}
	}

	public static class MixedEntry extends Item {
		public final EntityType entityType;

		public MixedEntry(EntityType entityType, int weight) {
			super(weight);
			this.entityType = entityType;
		}
	}

	public static class Single implements NPCEntityTypeProvider {
		private final EntityType type;

		public Single(EntityType type) {
			this.type = type;
		}

		@Override
		public Stream getAllPossibleTypes() {
			return Stream.of(type);
		}

		@Override
		public EntityType getTypeToSpawn(Random rand) {
			return type;
		}
	}
}
