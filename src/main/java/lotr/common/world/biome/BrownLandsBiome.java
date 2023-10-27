package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class BrownLandsBiome extends LOTRBiomeBase {
	public BrownLandsBiome(boolean major) {
		super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.2F).temperature(0.8F).downfall(0.2F), major);
		biomeColors.setGrass(11373417);
		biomeColors.setSky(8878434);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 8, 4);
	}

	@Override
	protected void addPumpkins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.1F, LOTRBiomeFeatures.oakDead(), 10, LOTRBiomeFeatures.spruceDead(), 3);
		LOTRBiomeFeatures.addGrass(this, builder, 2, GrassBlends.BASIC);
		LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 8);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.7D, 0.08D).threshold(0.05D).state(Blocks.COARSE_DIRT).topOnly()));
	}
}
