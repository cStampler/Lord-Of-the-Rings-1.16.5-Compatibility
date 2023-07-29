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

public class RohanBiome extends LOTRBiomeBase {
	public RohanBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.15F).temperature(0.8F).downfall(0.8F), major);
	}

	protected RohanBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 6);
		this.addBears(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState(), 1, 4, 64, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 64, 3);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addGranite(builder);
		LOTRBiomeFeatures.addRohanRockPatches(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.ROHAN_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ROHAN_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.COBBLESTONE_WALL.defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.01F, TreeCluster.of(12, 80), rohanTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 15, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 5, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 4);
		LOTRBiomeFeatures.addSimbelmyneChance(builder, 60);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.ROHAN;
	}

	protected final Object[] rohanTrees() {
		return new Object[] { LOTRBiomeFeatures.oak(), 4000, LOTRBiomeFeatures.oakFancy(), 8000, LOTRBiomeFeatures.oakBees(), 40, LOTRBiomeFeatures.oakFancyBees(), 80, LOTRBiomeFeatures.birch(), 200, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.birchBees(), 2, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.beech(), 200, LOTRBiomeFeatures.beechFancy(), 100, LOTRBiomeFeatures.beechBees(), 2, LOTRBiomeFeatures.beechFancyBees(), 1, LOTRBiomeFeatures.pine(), 200, LOTRBiomeFeatures.apple(), 20, LOTRBiomeFeatures.pear(), 20, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1 };
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.addSubSoilLayer(((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState(), 8, 10);
	}

	public static class Wold extends RohanBiome {
		public Wold(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.4F).scale(0.3F).temperature(0.7F).downfall(0.6F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState(), 1, 3, 120, 3);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 120, 3);
			LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState(), 0, 0, 6, 8);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 0, 0, 6, 8);
		}

		@Override
		protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addGranite(builder);
			LOTRBiomeFeatures.addDiorite(builder);
			LOTRBiomeFeatures.addRohanRockPatches(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = LOTRUtil.combineVarargs(rohanTrees(), LOTRBiomeFeatures.oakDead(), 4000, LOTRBiomeFeatures.beechDead(), 4000);
			LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.008F, TreeCluster.of(8, 100), weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addPlainsFlowers(builder, 1);
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			config.setFillerDepth(1.0D);
			config.addSubSoilLayer(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1);
			config.addSubSoilLayer(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 2);
			config.addSubSoilLayer(((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState(), 4, 6);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.005D).threshold(0.3D).state(Blocks.COARSE_DIRT).topOnly()));
		}
	}
}
