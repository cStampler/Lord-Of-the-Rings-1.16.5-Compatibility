package lotr.common.world.gen.feature.grassblend;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import lotr.common.world.gen.feature.WeightedFeature;
import lotr.common.world.gen.feature.WeightedRandomFeatureConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public abstract class GrassBlend {
	private final List entries;
	private WeightedRandomFeatureConfig bakedFeatureConfig;

	public GrassBlend(List entries) {
		this.entries = entries;
	}

	public WeightedRandomFeatureConfig getFeatureConfig() {
		if (bakedFeatureConfig == null) {
			bakedFeatureConfig = toFeatureConfig(entries);
		}

		return bakedFeatureConfig;
	}

	protected static GrassBlend of(Function constructor, Object... weightedConfigs) {
		List entries = new ArrayList();

		for (int i = 0; i < weightedConfigs.length; i += 2) {
			Supplier config = (Supplier) weightedConfigs[i];
			int weight = (Integer) weightedConfigs[i + 1];
			entries.add(new GrassBlend.Entry(config, weight));
		}

		return (GrassBlend) constructor.apply(entries);
	}

	private static WeightedRandomFeatureConfig toFeatureConfig(List entries) {
		List weightedGrassTypes = new ArrayList();
		entries.forEach(entry -> {
			weightedGrassTypes.add(WeightedFeature.make(Feature.RANDOM_PATCH.configured((BlockClusterFeatureConfig) ((Entry) entry).config.get()), ((Entry) entry).weight));
		});
		return new WeightedRandomFeatureConfig(weightedGrassTypes);
	}

	public static class Entry {
		private final Supplier config;
		private final int weight;

		public Entry(Supplier config, int weight) {
			this.config = config;
			this.weight = weight;
		}
	}
}
