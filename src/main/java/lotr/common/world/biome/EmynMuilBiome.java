package lotr.common.world.biome;

import com.google.common.collect.ImmutableList;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class EmynMuilBiome extends LOTRBiomeBase {
	public EmynMuilBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.DESERT).depth(0.2F).scale(0.8F).temperature(0.5F).downfall(0.9F), major);
		biomeColors.setGrass(9539937);
		biomeColors.setSky(10000788);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 1, 4, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 5, 8, 1, 4, 6);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.GRANITE.defaultBlockState(), 1, 4, 1, 4, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.GRANITE.defaultBlockState(), 5, 8, 1, 4, 6);
		LOTRBiomeFeatures.addTerrainSharpener(builder, ImmutableList.of(Blocks.STONE.defaultBlockState(), Blocks.GRANITE.defaultBlockState()), 1, 3, 10);
		LOTRBiomeFeatures.addGrassPatches(builder, ImmutableList.of(Blocks.STONE.defaultBlockState(), Blocks.GRANITE.defaultBlockState()), 1, 5, 4, 5, 5);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 1, 0.5F, LOTRBiomeFeatures.oakDesert(), 200, LOTRBiomeFeatures.oakDead(), 800);
		LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addMountainsFlowers(builder, 1);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setTop(Blocks.STONE.defaultBlockState());
		config.setFiller(Blocks.STONE.defaultBlockState());
	}
}
