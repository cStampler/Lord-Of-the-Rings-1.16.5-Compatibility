package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class LebenninBiome extends BaseGondorBiome {
	public LebenninBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.3F).temperature(0.9F).downfall(0.9F), major);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addBoulders(builder);
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.GONDOR_ROCK.get()).defaultBlockState(), 2, 5, 24, 3);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.5F, TreeCluster.of(10, 30), LOTRBiomeFeatures.oak(), 10000, LOTRBiomeFeatures.oakFancy(), 3000, LOTRBiomeFeatures.oakBees(), 100, LOTRBiomeFeatures.oakFancyBees(), 30, LOTRBiomeFeatures.birch(), 3000, LOTRBiomeFeatures.birchFancy(), 1000, LOTRBiomeFeatures.birchBees(), 30, LOTRBiomeFeatures.birchFancyBees(), 10, LOTRBiomeFeatures.beech(), 1500, LOTRBiomeFeatures.beechFancy(), 500, LOTRBiomeFeatures.beechBees(), 15, LOTRBiomeFeatures.beechFancyBees(), 5, LOTRBiomeFeatures.maple(), 500, LOTRBiomeFeatures.mapleBees(), 5, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 12, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 4, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 3, LOTRBlocks.MALLOS.get(), 50);
		LOTRBiomeFeatures.addAthelasChance(builder);
		LOTRBiomeFeatures.addWildPipeweedChance(builder, 24);
	}
}
