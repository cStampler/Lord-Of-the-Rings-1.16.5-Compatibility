package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class LebethronTree extends PartyableTree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		if (rand.nextInt(10) == 0) {
			return bees ? LOTRBiomeFeatures.lebethronFancyBees() : LOTRBiomeFeatures.lebethronFancy();
		}
		return bees ? LOTRBiomeFeatures.lebethronBees() : LOTRBiomeFeatures.lebethron();
	}

	@Override
	protected ConfiguredFeature getPartyTreeFeature(Random rand) {
		return LOTRBiomeFeatures.lebethronParty();
	}
}
