package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class UmbarBiome extends LOTRBiomeBase {
	public UmbarBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.2F).temperature(0.9F).downfall(0.6F), major);
	}

	protected UmbarBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(11914805);
		biomeColors.setFog(16248281);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 1);
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
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.UMBAR_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.UMBAR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.NUMENOREAN_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.15F, TreeCluster.of(10, 30), umbarTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addHaradFlowers(builder, 3);
		LOTRBiomeFeatures.addHaradDoubleFlowers(builder, 1);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.UMBAR;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.25D, 0.07D, 0.002D).threshold(0.2D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.25D, 0.07D, 0.002D).threshold(0.3667D).state(Blocks.SAND)));
	}

	protected final Object[] umbarTrees() {
		return new Object[] { LOTRBiomeFeatures.oakDesert(), 10000, LOTRBiomeFeatures.oakDesertBees(), 10, LOTRBiomeFeatures.cedar(), 3000, LOTRBiomeFeatures.cypress(), 5000 };
	}

	public static class Forest extends UmbarBiome {
		public Forest(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.3F).temperature(0.8F).downfall(1.0F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 5, 0.5F, LOTRUtil.combineVarargs(umbarTrees(), LOTRBiomeFeatures.cedar(), 3000, LOTRBiomeFeatures.cedarLarge(), 500, LOTRBiomeFeatures.cypress(), 2000));
			LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addHaradFlowers(builder, 4);
			LOTRBiomeFeatures.addHaradDoubleFlowers(builder, 2);
		}
	}

	public static class Hills extends UmbarBiome {
		public Hills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(1.2F).scale(0.8F).temperature(0.8F).downfall(0.6F), major);
			biomeColors.resetGrass();
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addBoulders(builder);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 1, 32, 3);
		}
	}
}
