package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class ShireBiome extends LOTRBiomeBase {
	public ShireBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.15F).scale(0.3F).temperature(0.8F).downfall(0.7F), major);
	}

	protected ShireBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(8111137);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.FOX, 5, 2, 4));
	}

	@Override
	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWaterSprings(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.HOBBIT_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.PINE_PLANKS.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.PINE_FENCE.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.LANTERN.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.2F, TreeCluster.of(10, 6), shireTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.SHIRE);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 3);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
		LOTRBiomeFeatures.addSunflowers(builder, 6);
		LOTRBiomeFeatures.addWildPipeweedChance(builder, 6);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.PATH.withStandardHedge();
	}

	protected final Object[] shireTrees() {
		return new Object[] { LOTRBiomeFeatures.oak(), 10000, LOTRBiomeFeatures.oakFancy(), 4000, LOTRBiomeFeatures.oakBees(), 100, LOTRBiomeFeatures.oakFancyBees(), 40, LOTRBiomeFeatures.oakParty(), 200, LOTRBiomeFeatures.birch(), 250, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.birchBees(), 2, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.aspen(), 500, LOTRBiomeFeatures.aspenLarge(), 100, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.pearBees(), 1, LOTRBiomeFeatures.cherry(), 20, LOTRBiomeFeatures.cherryBees(), 1 };
	}

	public static class Marshes extends ShireBiome {
		public Marshes(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.22F).scale(0.0F).temperature(0.8F).downfall(1.2F), major);
			biomeColors.resetGrass();
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
		protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakFancy(), 100, LOTRBiomeFeatures.oakSwamp(), 1000, LOTRBiomeFeatures.oakShrub(), 2500 };
			LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 2, 0.5F, 63, weightedTrees);
			LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 3, 0.5F, 64, weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.MUTED_WITH_FERNS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 8, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
			LOTRBiomeFeatures.addSwampFlowers(builder, 4);
			LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 3);
			LOTRBiomeFeatures.addWaterLiliesWithRareFlowers(builder, 4);
			LOTRBiomeFeatures.addSwampSeagrass(builder);
			LOTRBiomeFeatures.addFallenLogs(builder, 2);
			LOTRBiomeFeatures.addWildPipeweedChance(builder, 3);
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

	public static class Moors extends ShireBiome {
		public Moors(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.6F).scale(0.6F).temperature(0.6F).downfall(1.0F), major);
			biomeColors.resetGrass();
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addBoulders(builder);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 8, 4);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 3, 5, 40, 3);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 0, 0.1F, LOTRUtil.combineVarargs(shireTrees(), LOTRBiomeFeatures.oakFancy(), 80000, LOTRBiomeFeatures.oakFancyBees(), 800));
			LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.SHIRE_MOORS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MOORS);
			LOTRBiomeFeatures.addPlainsFlowers(builder, 10, LOTRBlocks.SHIRE_HEATHER.get(), 100);
			LOTRBiomeFeatures.addWildPipeweedChance(builder, 12);
		}
	}

	public static class WhiteDowns extends ShireBiome {
		public WhiteDowns(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.7F).scale(0.6F).temperature(0.6F).downfall(0.8F), major);
			biomeColors.resetGrass();
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addBoulders(builder);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 80, 3);
			LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 1, 1, 16, 3);
			LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 2, 3, 32, 2);
		}

		@Override
		protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addStoneVariants(builder);
			LOTRBiomeFeatures.addDiorite(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 0, 0.08F, shireTrees());
			LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.SHIRE_MOORS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MOORS);
			LOTRBiomeFeatures.addPlainsFlowers(builder, 2, LOTRBlocks.SHIRE_HEATHER.get(), 20);
			LOTRBiomeFeatures.addWildPipeweedChance(builder, 12);
		}

		@Override
		public RoadBlockProvider getRoadBlockProvider() {
			return RoadBlockProvider.CHALK_PATH;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setFillerDepth(0.0D);
			config.addSubSoilLayer(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1);
			config.addSubSoilLayer(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 4);
		}
	}
}
