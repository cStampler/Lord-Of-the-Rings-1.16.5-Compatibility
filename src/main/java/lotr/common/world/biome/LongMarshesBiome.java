package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class LongMarshesBiome extends LOTRBiomeBase {
	public LongMarshesBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.22F).scale(0.0F).temperature(0.5F).downfall(0.9F), 8167049, major);
		biomeColors.setSky(13230818).setFog(12112325).setFoggy(true).setWater(8167049);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addQuagmire(builder, 2);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addReeds(builder);
		LOTRBiomeFeatures.addMoreSwampReeds(builder);
		LOTRBiomeFeatures.addSwampRushes(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakFancy(), 100, LOTRBiomeFeatures.oakSwamp(), 1000, LOTRBiomeFeatures.oakShrub(), 3000, LOTRBiomeFeatures.oakDead(), 500, LOTRBiomeFeatures.spruce(), 500, LOTRBiomeFeatures.spruceDead(), 500, LOTRBiomeFeatures.fir(), 200, LOTRBiomeFeatures.pine(), 200 };
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 1, 0.5F, 63, weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 3, 0.5F, 64, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 12, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addSwampFlowers(builder, 2);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 3);
		LOTRBiomeFeatures.addWaterLilies(builder, 4);
		LOTRBiomeFeatures.addSwampSeagrass(builder);
		LOTRBiomeFeatures.addFallenLogs(builder, 1);
	}

	@Override
	public float getBiomeScaleSignificanceForChunkGen() {
		return 0.96F;
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
	}
}
