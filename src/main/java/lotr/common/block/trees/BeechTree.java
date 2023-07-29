package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class BeechTree extends PartyableTree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		if (rand.nextInt(10) == 0) {
			return bees ? LOTRBiomeFeatures.beechFancyBees() : LOTRBiomeFeatures.beechFancy();
		}
		return bees ? LOTRBiomeFeatures.beechBees() : LOTRBiomeFeatures.beech();
	}

	@Override
	public ConfiguredFeature getPartyTreeFeature(Random rand) {
		return LOTRBiomeFeatures.beechParty();
	}
}
