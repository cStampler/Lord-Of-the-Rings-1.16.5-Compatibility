package lotr.common.world.biome;

import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class EregionBiome extends LOTRBiomeBase {
	public EregionBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.3F).temperature(0.7F).downfall(0.7F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addWolves(builder, 1);
		this.addDeer(builder, 2);
		this.addBears(builder, 1);
		this.addFoxes(builder, 1);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 30, 3);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.4F, TreeCluster.of(12, 5), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 5, LOTRBiomeFeatures.oakFancyBees(), 1, LOTRBiomeFeatures.holly(), 10000, LOTRBiomeFeatures.hollyBees(), 10, LOTRBiomeFeatures.birch(), 1000, LOTRBiomeFeatures.birchFancy(), 500, LOTRBiomeFeatures.birchBees(), 1, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.beech(), 500, LOTRBiomeFeatures.beechFancy(), 250, LOTRBiomeFeatures.beechBees(), 1, LOTRBiomeFeatures.beechFancyBees(), 1, LOTRBiomeFeatures.larch(), 2000, LOTRBiomeFeatures.aspen(), 200, LOTRBiomeFeatures.aspenLarge(), 50, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_WITH_FERNS);
		LOTRBiomeFeatures.addForestFlowers(builder, 3);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
		LOTRBiomeFeatures.addFoxBerryBushes(builder);
	}
}
