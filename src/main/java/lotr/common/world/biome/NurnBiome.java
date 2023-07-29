package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class NurnBiome extends LOTRBiomeBase {
	public NurnBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.2F).temperature(0.8F).downfall(0.5F), major);
	}

	protected NurnBiome(Builder builder, boolean major) {
		super(builder, 4413266, major);
		biomeColors.setGrass(10068025).setFoliage(7504951).setSky(10404589).setClouds(9342083).setWater(4413266);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 1, 3, 40, 4);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 60, 3);
	}

	@Override
	protected void addDirtGravel(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addMordorDirtGravel(builder);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addMordorOres(builder);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addReedsWithDriedChance(builder, 0.6F);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.MORDOR_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ORC_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.25F, TreeCluster.of(6, 30), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakDesert(), 5000, LOTRBiomeFeatures.oakDead(), 2000, LOTRBiomeFeatures.cedar(), 1000, LOTRBiomeFeatures.charred(), 2000);
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.EXTRA_WHEATGRASS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_WITH_EXTRA_WHEATGRASS);
		LOTRBiomeFeatures.addDefaultFlowers(builder, 1);
		LOTRBiomeFeatures.addMordorGrass(builder, 2);
		LOTRBiomeFeatures.addMordorThorns(builder, 200);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.NURN_PATH;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setFillerDepth(2.0D);
		config.addSubSoilLayer(((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState(), 3);
		config.addSubSoilLayer(((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 1000);
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.08D).threshold(0.4D).state(Blocks.COARSE_DIRT).topOnly()));
	}

	public static class Marshes extends NurnBiome {
		public Marshes(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.SWAMP).depth(-0.22F).scale(0.0F).temperature(0.7F).downfall(0.8F), major);
			biomeColors.setGrass(8291139);
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
			Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakFancy(), 100, LOTRBiomeFeatures.oakSwamp(), 1000, LOTRBiomeFeatures.oakDead(), 1500, LOTRBiomeFeatures.oakShrub(), 6000 };
			LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 2, 0.5F, 63, weightedTrees);
			LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 3, 0.5F, 64, weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.MUTED_WITH_FERNS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 10, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
			LOTRBiomeFeatures.addSwampFlowers(builder, 2);
			LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 4);
			LOTRBiomeFeatures.addSwampSeagrass(builder);
			LOTRBiomeFeatures.addFallenLogs(builder, 2);
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

	public static class Sea extends NurnBiome {
		public Sea(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.OCEAN).depth(-1.0F).scale(0.3F).temperature(0.8F).downfall(0.5F), major);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}
	}
}
