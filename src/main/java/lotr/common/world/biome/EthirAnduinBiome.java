package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class EthirAnduinBiome extends BaseGondorBiome {
	public EthirAnduinBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.22F).scale(0.0F).temperature(0.9F).downfall(1.0F), major);
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
		LOTRBiomeFeatures.addSugarCane(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 100, LOTRBiomeFeatures.oakFancy(), 100, LOTRBiomeFeatures.oakSwamp(), 300, LOTRBiomeFeatures.birch(), 600, LOTRBiomeFeatures.cypress(), 1200, LOTRBiomeFeatures.oakShrub(), 3000 };
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 4, 0.5F, 63, weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 3, 0.5F, 64, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 8, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addSwampFlowers(builder, 4, LOTRBlocks.MALLOS.get(), 4);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 3);
		LOTRBiomeFeatures.addSwampSeagrass(builder);
		LOTRBiomeFeatures.addFallenLogs(builder, 1);
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
		super.setupSurface(config);
		config.setMarsh(true);
	}
}
