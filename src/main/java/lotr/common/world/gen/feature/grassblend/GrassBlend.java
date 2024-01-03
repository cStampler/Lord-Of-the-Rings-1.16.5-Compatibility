package lotr.common.world.gen.feature.grassblend;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import lotr.common.world.gen.feature.WeightedFeature;
import lotr.common.world.gen.feature.WeightedRandomFeatureConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public abstract class GrassBlend {
	private final List<Entry> entries;
	private WeightedRandomFeatureConfig<IFeatureConfig> bakedFeatureConfig;

	public GrassBlend(List<Entry> entries) {
		this.entries = entries;
	}

	public WeightedRandomFeatureConfig<IFeatureConfig> getFeatureConfig() {
		if (bakedFeatureConfig == null) {
			bakedFeatureConfig = toFeatureConfig(entries);
		}

		return bakedFeatureConfig;
	}

	@SuppressWarnings("unchecked")
	protected static <T extends GrassBlend> T of(Function<List<Entry>, T> constructor, Object... weightedConfigs) {
		List<Entry> entries = new ArrayList<>();

		for (int i = 0; i < weightedConfigs.length; i += 2) {
			Supplier<BlockClusterFeatureConfig> config = (Supplier<BlockClusterFeatureConfig>) weightedConfigs[i];
			int weight = (Integer) weightedConfigs[i + 1];
			entries.add(new GrassBlend.Entry(config, weight));
		}

		return constructor.apply(entries);
	}

	@SuppressWarnings("unchecked")
	private static WeightedRandomFeatureConfig<IFeatureConfig> toFeatureConfig(List<Entry> entries) {
	    List<WeightedFeature<IFeatureConfig>> weightedGrassTypes = new ArrayList<>();
	    entries.forEach(entry -> weightedGrassTypes.add(WeightedFeature.make((Supplier<ConfiguredFeature<?, ?>>) Feature.RANDOM_PATCH.configured(entry.config.get()), entry.weight)));
	    return new WeightedRandomFeatureConfig<IFeatureConfig>(weightedGrassTypes);
	  }

	public static class Entry {
		private final Supplier<BlockClusterFeatureConfig> config;
		private final int weight;

		public Entry(Supplier<BlockClusterFeatureConfig> config, int weight) {
			this.config = config;
			this.weight = weight;
		}
	}
}
