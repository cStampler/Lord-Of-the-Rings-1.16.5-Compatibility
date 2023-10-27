package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class FurtherGondorBiome extends BaseGondorBiome {
	public FurtherGondorBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.3F).scale(0.5F).temperature(0.7F).downfall(0.8F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addBears(builder);
		this.addWolves(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addBoulders(builder);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 36, 3);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.45F, TreeCluster.of(16, 15), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 50, LOTRBiomeFeatures.oakFancyBees(), 10, LOTRBiomeFeatures.birch(), 500, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.birchBees(), 5, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.aspen(), 1000, LOTRBiomeFeatures.larch(), 2000, LOTRBiomeFeatures.oakShrub(), 10000);
		LOTRBiomeFeatures.addGrass(this, builder, 12, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 5, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 2);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.07D).threshold(0.5D).state(Blocks.COARSE_DIRT).topOnly()));
	}
}
