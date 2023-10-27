package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class GladdenFieldsBiome extends LOTRBiomeBase {
	public GladdenFieldsBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.22F).scale(0.0F).temperature(0.6F).downfall(1.2F), major);
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
		LOTRBiomeFeatures.addMoreSwampReeds(builder);
		LOTRBiomeFeatures.addSwampRushes(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakFancy(), 100, LOTRBiomeFeatures.oakSwamp(), 1000, LOTRBiomeFeatures.oakShrub(), 4000, LOTRBiomeFeatures.birch(), 500, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.spruce(), 1000, LOTRBiomeFeatures.larch(), 500 };
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 2, 0.5F, 63, weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 3, 0.5F, 64, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 8, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addSwampFlowers(builder, 2);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 7, LOTRBlocks.YELLOW_IRIS.get(), 40);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 3);
		LOTRBiomeFeatures.addWaterLiliesWithFlowers(builder, 5);
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
