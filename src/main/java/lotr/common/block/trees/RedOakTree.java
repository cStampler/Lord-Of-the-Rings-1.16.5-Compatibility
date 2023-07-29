package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class RedOakTree extends PartyableTree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		return bees ? LOTRBiomeFeatures.redOakBees() : LOTRBiomeFeatures.redOak();
	}

	@Override
	protected ConfiguredFeature getPartyTreeFeature(Random rand) {
		return LOTRBiomeFeatures.redOakParty();
	}
}
