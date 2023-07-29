package lotr.common.block.trees;

import java.util.Random;

import lotr.common.world.biome.LOTRBiomeFeatures;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class MallornTree extends PartyableCrossBigTree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		return bees ? LOTRBiomeFeatures.mallornBees() : LOTRBiomeFeatures.mallorn();
	}

	@Override
	protected ConfiguredFeature getCrossTreeFeature(Random rand) {
		return LOTRBiomeFeatures.mallornBoughs();
	}

	@Override
	protected ConfiguredFeature getPartyTreeFeature(Random rand) {
		return LOTRBiomeFeatures.mallornParty();
	}
}
