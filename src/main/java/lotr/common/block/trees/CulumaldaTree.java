package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class CulumaldaTree extends Tree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		return bees ? LOTRBiomeFeatures.culumaldaBees() : LOTRBiomeFeatures.culumalda();
	}
}
