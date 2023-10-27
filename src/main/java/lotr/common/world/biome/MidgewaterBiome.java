package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class MidgewaterBiome extends LOTRBiomeBase {
	public MidgewaterBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.22F).scale(0.0F).temperature(0.6F).downfall(1.0F), 5855807, major);
		biomeColors.setGrass(7962434).setFoliage(8154931).setSky(13560554).setFog(14211254).setFoggy(true).setWater(5855807);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addQuagmire(builder, 1);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addReeds(builder);
		LOTRBiomeFeatures.addSwampRushes(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakFancy(), 100, LOTRBiomeFeatures.oakSwamp(), 1000, LOTRBiomeFeatures.oakShrub(), 2500, LOTRBiomeFeatures.oakDead(), 500 };
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 2, 0.5F, 63, weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 3, 0.5F, 64, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 6, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addSwampFlowers(builder, 1);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 3);
		LOTRBiomeFeatures.addWaterLilies(builder, 5);
		LOTRBiomeFeatures.addSwampSeagrass(builder);
		LOTRBiomeFeatures.addFallenLogs(builder, 3);
		LOTRBiomeFeatures.addAthelasChance(builder);
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
