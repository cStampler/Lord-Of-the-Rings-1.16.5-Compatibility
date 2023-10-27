package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class HaradDesertBiome extends LOTRBiomeBase {
	public HaradDesertBiome(boolean major) {
		this(new Builder().precipitation(RainType.NONE).biomeCategory(Category.DESERT).depth(0.2F).scale(0.1F).temperature(1.5F).downfall(0.1F), major);
	}

	protected HaradDesertBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setCloudCoverage(0.2F);
		biomeColors.setFog(16180681);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 240, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.SANDSTONE.defaultBlockState(), 1, 3, 240, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addLapisOre(builder);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addLessCommonReeds(builder);
		LOTRBiomeFeatures.addMoreCommonPapyrus(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oakDead(), 8000, LOTRBiomeFeatures.oakDesert(), 2000 };
		LOTRBiomeFeatures.addTreesIncrease(this, builder, 0, 5.0E-4F, 1, weightedTrees);
		LOTRBiomeFeatures.addTreesIncrease(this, builder, 0, 5.0E-4F, 3, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 1, GrassBlends.WITH_ARID);
		LOTRBiomeFeatures.addHaradFlowers(builder, 0);
		LOTRBiomeFeatures.addCactiAtSurfaceChance(builder, 50);
		LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 16);
	}

	@Override
	protected ExtendedWeatherType getBiomeExtendedWeather() {
		return ExtendedWeatherType.SANDSTORM;
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.HARAD.withRepair(0.5F);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setTop(Blocks.SAND.defaultBlockState());
		config.setFiller(Blocks.SAND.defaultBlockState());
	}

	public static class HalfDesert extends HaradDesertBiome {
		public HalfDesert(boolean major) {
			super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.DESERT).depth(0.2F).scale(0.1F).temperature(1.5F).downfall(0.3F), major);
			biomeColors.setCloudCoverage(0.6F);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = { LOTRBiomeFeatures.oakDead(), 5000, LOTRBiomeFeatures.oakDesert(), 5000 };
			LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.05F, TreeCluster.of(8, 100), weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.WITH_ARID);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_WITH_ARID);
			LOTRBiomeFeatures.addHaradFlowers(builder, 0);
			LOTRBiomeFeatures.addCactiAtSurfaceChance(builder, 200);
			LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 16);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return getNormalRiver(world);
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.08D).threshold(0.15D).state(Blocks.COARSE_DIRT).topOnly()));
		}
	}

	public static class Hills extends HaradDesertBiome {
		public Hills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.DESERT).depth(0.5F).scale(0.8F).temperature(1.2F).downfall(0.3F), major);
			biomeColors.setCloudCoverage(0.4F);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = { LOTRBiomeFeatures.oakDead(), 8000, LOTRBiomeFeatures.oakDesert(), 2000 };
			LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.01F, TreeCluster.of(6, 100), weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 1, GrassBlends.WITH_ARID);
			LOTRBiomeFeatures.addHaradFlowers(builder, 0);
			LOTRBiomeFeatures.addCactiAtSurfaceChance(builder, 200);
			LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 16);
		}

		@Override
		protected ExtendedWeatherType getBiomeExtendedWeather() {
			return ExtendedWeatherType.NONE;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.09D).threshold(0.1D).state(Blocks.SANDSTONE), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.09D).threshold(0.3D).state(Blocks.STONE)));
		}
	}
}
