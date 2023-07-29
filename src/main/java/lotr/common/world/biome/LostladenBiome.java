package lotr.common.world.biome;

import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.*;

public class LostladenBiome extends LOTRBiomeBase {
	public LostladenBiome(boolean major) {
		super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.DESERT).depth(0.2F).scale(0.1F).temperature(0.9F).downfall(0.2F), major);
		biomeColors.setSky(15592678);
		biomeColors.setCloudCoverage(0.7F);
		biomeColors.setFog(15393237);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 48, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.SANDSTONE.defaultBlockState(), 1, 3, 48, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addLapisOre(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oakDead(), 200, LOTRBiomeFeatures.oakDesert(), 1000 };
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.02F, TreeCluster.of(8, 200), weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 3, GrassBlends.WITH_ARID);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_WITH_ARID);
		LOTRBiomeFeatures.addDefaultFlowers(builder, 0);
		LOTRBiomeFeatures.addCactiAtSurfaceChance(builder, 200);
		LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 8);
	}

	@Override
	protected ExtendedWeatherType getBiomeExtendedWeather() {
		return ExtendedWeatherType.SANDSTORM;
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.HARAD.withRepair(0.3F);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.09D).threshold(0.15D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.09D).threshold(0.05D).states(Blocks.SAND, 4, Blocks.SANDSTONE, 1), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.4D, 0.09D).threshold(0.05D).states(Blocks.STONE, 4, Blocks.GRAVEL, 1)));
	}
}
