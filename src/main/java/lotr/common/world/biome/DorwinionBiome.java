package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class DorwinionBiome extends LOTRBiomeBase {
	public DorwinionBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.3F).temperature(0.8F).downfall(0.9F), major);
	}

	protected DorwinionBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(10538541);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder);
		this.addBears(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 1, 2, 50, 3);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.DORWINION_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DORWINION_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.RED_DORWINION_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.BRONZE_LANTERN.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.1F, TreeCluster.of(8, 15), dorwinionTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 9, GrassBlends.EXTRA_WHEATGRASS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_WITH_EXTRA_WHEATGRASS);
		LOTRBiomeFeatures.addRhunPlainsFlowers(builder, 6);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
	}

	protected final Object[] dorwinionTrees() {
		return new Object[] { LOTRBiomeFeatures.oak(), 2000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 200, LOTRBiomeFeatures.oakFancyBees(), 100, LOTRBiomeFeatures.birch(), 1000, LOTRBiomeFeatures.birchFancy(), 500, LOTRBiomeFeatures.birchBees(), 10, LOTRBiomeFeatures.birchFancyBees(), 5, LOTRBiomeFeatures.beech(), 200, LOTRBiomeFeatures.beechFancy(), 200, LOTRBiomeFeatures.beechBees(), 2, LOTRBiomeFeatures.beechFancyBees(), 2, LOTRBiomeFeatures.cypress(), 5000, LOTRBiomeFeatures.oakShrub(), 8000, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1 };
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.DORWINION_PATH;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.addSubSoilLayer(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1);
		config.addSubSoilLayer(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 5, 7);
	}

	public static class Hills extends DorwinionBiome {
		public Hills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.8F).scale(0.8F).temperature(0.8F).downfall(0.9F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 0, 0.25F, dorwinionTrees());
			LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 5, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addRhunPlainsFlowers(builder, 3);
			LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
		}

		@Override
		public double getHorizontalNoiseScale() {
			return 50.0D;
		}

		@Override
		public RoadBlockProvider getRoadBlockProvider() {
			return RoadBlockProvider.CHALK_PATH;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			config.setFillerDepth(0.0D);
			config.addSubSoilLayer(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1);
			config.addSubSoilLayer(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 5);
		}
	}
}
