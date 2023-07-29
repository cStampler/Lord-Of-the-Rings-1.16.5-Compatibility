package lotr.common.world.biome;

import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.*;

public class ColdfellsBiome extends LOTRBiomeBase {
	public ColdfellsBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.4F).scale(0.8F).temperature(0.2F).downfall(0.8F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addWolves(builder, 2);
		this.addElk(builder, 1);
		this.addBears(builder, 2);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 8, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 1, 0.5F, LOTRBiomeFeatures.fir(), 5000, LOTRBiomeFeatures.pine(), 5000, LOTRBiomeFeatures.pineDead(), 500, LOTRBiomeFeatures.spruce(), 4000, LOTRBiomeFeatures.spruceThin(), 2000, LOTRBiomeFeatures.spruceDead(), 600, LOTRBiomeFeatures.oak(), 2000, LOTRBiomeFeatures.oakFancy(), 300, LOTRBiomeFeatures.oakBees(), 2, LOTRBiomeFeatures.oakFancyBees(), 1, LOTRBiomeFeatures.larch(), 3000, LOTRBiomeFeatures.maple(), 500, LOTRBiomeFeatures.mapleFancy(), 50, LOTRBiomeFeatures.mapleBees(), 1, LOTRBiomeFeatures.mapleFancyBees(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.MOORS);
		LOTRBiomeFeatures.addMountainsFlowers(builder, 2);
		LOTRBiomeFeatures.addAthelasChance(builder);
	}
}
