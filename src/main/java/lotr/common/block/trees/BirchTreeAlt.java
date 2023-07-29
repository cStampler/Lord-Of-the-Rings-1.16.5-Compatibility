package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import lotr.common.world.gen.feature.WeightedRandomFeatureConfig;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class BirchTreeAlt extends Tree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		return bees ? ((WeightedRandomFeatureConfig) LOTRBiomeFeatures.birchBees().config).getRandomFeature(rand) : ((WeightedRandomFeatureConfig) LOTRBiomeFeatures.birch().config).getRandomFeature(rand);
	}
}
