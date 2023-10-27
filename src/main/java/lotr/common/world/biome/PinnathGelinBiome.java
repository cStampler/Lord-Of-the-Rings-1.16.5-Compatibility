package lotr.common.world.biome;

import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class PinnathGelinBiome extends BaseGondorBiome {
	public PinnathGelinBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.5F).scale(0.5F).temperature(0.8F).downfall(0.8F), major);
		biomeColors.setGrass(10930470);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addBoulders(builder);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 40, 3);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.01F, TreeCluster.of(10, 80), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 50, LOTRBiomeFeatures.oakFancyBees(), 10, LOTRBiomeFeatures.aspen(), 5000, LOTRBiomeFeatures.birch(), 2000, LOTRBiomeFeatures.birchFancy(), 500, LOTRBiomeFeatures.birchBees(), 20, LOTRBiomeFeatures.birchFancyBees(), 5, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 2);
		LOTRBiomeFeatures.addAthelasChance(builder);
		LOTRBiomeFeatures.addWildPipeweedChance(builder, 24);
	}
}
