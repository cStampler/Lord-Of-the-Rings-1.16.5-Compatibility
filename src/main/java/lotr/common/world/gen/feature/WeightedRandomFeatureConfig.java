package lotr.common.world.gen.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.LOTRLog;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

@SuppressWarnings("unchecked")
public class WeightedRandomFeatureConfig<FC extends IFeatureConfig> implements IFeatureConfig {
	public static final Codec<WeightedRandomFeatureConfig<IFeatureConfig>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    		WeightedFeature.CODEC.listOf().fieldOf("weighted_features").forGetter(p -> p.weightedFeatures)).apply(instance, WeightedRandomFeatureConfig::new));
	public final List<WeightedFeature<FC>> weightedFeatures;
	private int totalWeight;

	public WeightedRandomFeatureConfig(List<WeightedFeature<FC>> features) {
		weightedFeatures = features;
		updateTotalWeight();
	}

	public ConfiguredFeature<FC, ?> getRandomFeature(Random rand) {
		int totalWeight = getTotalWeight();
		int chosenWeight = rand.nextInt(totalWeight);
		WeightedFeature<FC> selected = null;

		for (WeightedFeature<FC> weightedFeature : this.weightedFeatures) {
		      float featureWeight = weightedFeature.weight;
		      if (chosenWeight < featureWeight) {
		        selected = weightedFeature;
		        break;
		      } 
		      chosenWeight = (int)(chosenWeight - featureWeight);
		    }

		if (selected == null) {
			LOTRLog.error("WeightedRandomFeature error: total weight = %d, chosen weight = %d, but selected feature == null", totalWeight, chosenWeight);
		}

		return (ConfiguredFeature<FC, ?>) selected.feature.get();
	}

	private int getTotalWeight() {
		return totalWeight;
	}

	private void updateTotalWeight() {
		totalWeight = 0;
		weightedFeatures.stream().forEach(wf -> {
			totalWeight += wf.weight;
		});
	}

	public static <FC extends IFeatureConfig> WeightedRandomFeatureConfig<FC> fromEntries(Object... entries) {
		try {
			List<WeightedFeature<FC>> tempList = new ArrayList<>();

			for (int i = 0; i < entries.length; i += 2) {
				ConfiguredFeature<FC, ?> feature = (ConfiguredFeature<FC, ?>) entries[i];
				int weight = (Integer) entries[i + 1];
				WeightedFeature<FC> wf = WeightedFeature.make(() -> feature, weight);
				tempList.add(wf);
			}

			return new WeightedRandomFeatureConfig<>((List<WeightedFeature<FC>>)ImmutableList.copyOf(tempList));
		} catch (ArrayIndexOutOfBoundsException | ClassCastException var6) {
			throw new IllegalArgumentException("Error adding biome trees! A list of (tree1, weight1), (tree2, weight2)... is required", var6);
		}
	}
}
