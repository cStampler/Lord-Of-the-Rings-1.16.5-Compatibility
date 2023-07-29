package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class LarchTree extends Tree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		return LOTRBiomeFeatures.larch();
	}
}
