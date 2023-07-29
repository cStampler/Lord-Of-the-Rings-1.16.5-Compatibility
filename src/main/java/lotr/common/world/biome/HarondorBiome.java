package lotr.common.world.biome;

import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.*;

public class HarondorBiome extends LOTRBiomeBase {
	public HarondorBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.3F).temperature(1.0F).downfall(0.6F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addCaracals(builder, 1);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 16, 4);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addLapisOre(builder);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addReeds(builder);
		LOTRBiomeFeatures.addSugarCane(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oakDesert(), 10000, LOTRBiomeFeatures.oakDesertBees(), 10, LOTRBiomeFeatures.cedar(), 2500 };
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.1F, weightedTrees);
		LOTRBiomeFeatures.addTreesIncrease(this, builder, 0, 0.0625F, 3, weightedTrees);
		LOTRBiomeFeatures.addTreesIncrease(this, builder, 0, 0.0625F, 7, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.WITH_ARID);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_WITH_ARID);
		LOTRBiomeFeatures.addDeadBushes(builder, 1);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 4);
		LOTRBiomeFeatures.addCactiFreq(builder, 1);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.GONDOR.withRepair(0.6F);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.09D).threshold(0.15D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.09D, 0.002D).weights(1, 1, 2).threshold(0.35D).state(Blocks.SAND)));
	}
}
