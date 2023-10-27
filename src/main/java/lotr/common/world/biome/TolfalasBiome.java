package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.MountainTerrainProvider;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class TolfalasBiome extends LOTRBiomeBase {
	public TolfalasBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.OCEAN).depth(0.3F).scale(1.0F).temperature(0.8F).downfall(0.7F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 10, 3);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addReeds(builder);
		LOTRBiomeFeatures.addSugarCane(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 1, 0.4F, LOTRBiomeFeatures.oak(), 100, LOTRBiomeFeatures.oakDesert(), 500, LOTRBiomeFeatures.oakDead(), 2000);
		LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 6, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addDefaultFlowers(builder, 1);
		LOTRBiomeFeatures.addAthelasChance(builder);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.09D).threshold(0.65D).state(Blocks.GRAVEL).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.09D).threshold(0.2D).state(Blocks.STONE).topOnly()));
		config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(90).useStone()));
	}
}
