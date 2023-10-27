package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class DeadMarshesBiome extends LOTRBiomeBase {
	public DeadMarshesBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.24F).scale(0.0F).temperature(0.4F).downfall(1.0F), 4014639, major);
		biomeColors.setGrass(8024152).setFoliage(7041093).setSky(8684390).setClouds(7368024).setFog(6315334).setWater(4014639);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addQuagmire(builder, 1);
	}

	@Override
	protected void addPumpkins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addReedsWithDriedChance(builder, 1.0F);
		LOTRBiomeFeatures.addSwampRushes(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oakDead(), 1000 };
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.25F, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 4, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addSwampFlowers(builder, 0);
		LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 1);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 3);
		LOTRBiomeFeatures.addSwampSeagrass(builder);
	}

	@Override
	public float getBiomeScaleSignificanceForChunkGen() {
		return 1.0F;
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}

	@Override
	public float getStrengthOfAddedDepthNoise() {
		return 0.15F;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setMarsh(true);
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.07D).threshold(0.15D).state(Blocks.COARSE_DIRT).topOnly()));
	}
}
