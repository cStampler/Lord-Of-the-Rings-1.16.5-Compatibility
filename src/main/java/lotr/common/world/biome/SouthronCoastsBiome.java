package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.*;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class SouthronCoastsBiome extends LOTRBiomeBase {
	public SouthronCoastsBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.1F).temperature(1.2F).downfall(0.7F), major);
	}

	protected SouthronCoastsBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(11914805);
		biomeColors.setFog(16248281);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 3);
		this.addCaracals(builder, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addLapisOre(builder);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addLessCommonReeds(builder);
		LOTRBiomeFeatures.addPapyrus(builder);
		LOTRBiomeFeatures.addSugarCane(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.HARAD_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.CEDAR_FENCE.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.BRONZE_LANTERN.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.2F, TreeCluster.of(8, 24), southronCoastsTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addDeadBushes(builder, 1);
		LOTRBiomeFeatures.addHaradFlowers(builder, 3);
		LOTRBiomeFeatures.addHaradDoubleFlowers(builder, 1);
		LOTRBiomeFeatures.addCactiFreq(builder, 1);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.HARAD_PATH;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.25D, 0.07D, 0.002D).threshold(0.13D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.25D, 0.07D, 0.002D).threshold(0.3D).state(Blocks.SAND), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.25D, 0.07D, 0.002D).threshold(0.53D).state(Blocks.RED_SAND)));
	}

	protected final Object[] southronCoastsTrees() {
		return new Object[] { LOTRBiomeFeatures.cedar(), 8000, LOTRBiomeFeatures.oakDesert(), 5000, LOTRBiomeFeatures.oakDesertBees(), 50, LOTRBiomeFeatures.cypress(), 4000 };
	}

	public static class Forest extends SouthronCoastsBiome {
		public Forest(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.4F).temperature(1.0F).downfall(1.0F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 6, 0.5F, LOTRUtil.combineVarargs(southronCoastsTrees(), LOTRBiomeFeatures.cedar(), 6000, LOTRBiomeFeatures.cedarLarge(), 1500));
			LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addDeadBushes(builder, 1);
			LOTRBiomeFeatures.addHaradFlowers(builder, 4);
			LOTRBiomeFeatures.addHaradDoubleFlowers(builder, 2);
			LOTRBiomeFeatures.addCactiFreq(builder, 1);
		}
	}
}
