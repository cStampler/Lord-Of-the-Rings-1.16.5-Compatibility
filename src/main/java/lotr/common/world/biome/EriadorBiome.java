package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.*;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class EriadorBiome extends LOTRBiomeBase {
	public EriadorBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.4F).temperature(0.8F).downfall(0.8F), major);
	}

	protected EriadorBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder);
		this.addBears(builder, 2);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, 2, ((Block) LOTRBlocks.RANGER_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ARNOR_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_ARNOR_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_ARNOR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.OAK_FENCE.defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.08F, TreeCluster.of(8, 12), eriadorTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 9, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 4, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 2, LOTRBlocks.LAVENDER.get(), 20);
		LOTRBiomeFeatures.addAthelasChance(builder);
	}

	protected final Object[] eriadorTrees() {
		return new Object[] { LOTRBiomeFeatures.oak(), 10000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 10, LOTRBiomeFeatures.oakFancyBees(), 1, LOTRBiomeFeatures.birch(), 1000, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.birchBees(), 1, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.spruce(), 2000, LOTRBiomeFeatures.beech(), 200, LOTRBiomeFeatures.beechFancy(), 20, LOTRBiomeFeatures.beechBees(), 1, LOTRBiomeFeatures.beechFancyBees(), 1, LOTRBiomeFeatures.maple(), 50, LOTRBiomeFeatures.mapleFancy(), 5, LOTRBiomeFeatures.mapleBees(), 1, LOTRBiomeFeatures.mapleFancyBees(), 1, LOTRBiomeFeatures.aspen(), 500, LOTRBiomeFeatures.aspenLarge(), 50, LOTRBiomeFeatures.apple(), 20, LOTRBiomeFeatures.pear(), 20, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1 };
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.ARNOR.withRepair(0.9F);
	}

	public static class Downs extends EriadorBiome {
		public Downs(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.7F).scale(0.6F).temperature(0.6F).downfall(0.7F), major);
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addBoulders(builder);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 4, 40, 3);
			LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 1, 1, 16, 3);
			LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 2, 3, 40, 1);
		}

		@Override
		protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addStoneVariants(builder);
			LOTRBiomeFeatures.addDiorite(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 0, 0.03F, eriadorTrees());
			LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.MOORS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MOORS);
			LOTRBiomeFeatures.addPlainsFlowers(builder, 2);
			LOTRBiomeFeatures.addAthelasChance(builder);
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
			config.addSubSoilLayer(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 3);
		}
	}

	public static class ErynVorn extends EriadorBiome {
		public ErynVorn(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.1F).scale(0.4F).temperature(0.8F).downfall(0.9F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
			this.addWolves(builder);
			this.addBears(builder);
			this.addFoxes(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = LOTRUtil.combineVarargs(eriadorTrees(), LOTRBiomeFeatures.pine(), 10000, LOTRBiomeFeatures.pineDead(), 100, LOTRBiomeFeatures.fir(), 3000, LOTRBiomeFeatures.spruce(), 1000);
			LOTRBiomeFeatures.addTrees(this, builder, 10, 0.0F, weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 9, GrassBlends.WITH_FERNS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_WITH_FERNS);
			LOTRBiomeFeatures.addForestFlowers(builder, 4);
			LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
			LOTRBiomeFeatures.addAthelasChance(builder);
			LOTRBiomeFeatures.addFoxBerryBushes(builder);
		}
	}

	public static class EvendimHills extends EriadorBiome {
		public EvendimHills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.6F).scale(1.2F).temperature(0.7F).downfall(0.8F), major);
		}

		@Override
		protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] lowTrees = { LOTRBiomeFeatures.pine(), 1200, LOTRBiomeFeatures.pineDead(), 20, LOTRBiomeFeatures.spruce(), 300, LOTRBiomeFeatures.spruceThin(), 100, LOTRBiomeFeatures.spruceDead(), 100, LOTRBiomeFeatures.aspen(), 100, LOTRBiomeFeatures.aspenLarge(), 20, LOTRBiomeFeatures.oak(), 100, LOTRBiomeFeatures.oakFancy(), 20, LOTRBiomeFeatures.oakBees(), 1, LOTRBiomeFeatures.oakFancyBees(), 1, LOTRBiomeFeatures.birch(), 50, LOTRBiomeFeatures.birchFancy(), 10, LOTRBiomeFeatures.birchBees(), 1, LOTRBiomeFeatures.birchFancyBees(), 1 };
			Object[] highTrees = { LOTRBiomeFeatures.pine(), 300, LOTRBiomeFeatures.pineDead(), 600, LOTRBiomeFeatures.spruceThin(), 100, LOTRBiomeFeatures.spruceDead(), 200 };
			LOTRBiomeFeatures.addTrees(this, builder, 0, 0.03F, highTrees);
			LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 10, 0.25F, 82, lowTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.MOORS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_MOORS);
			LOTRBiomeFeatures.addForestFlowers(builder, 2);
			LOTRBiomeFeatures.addAthelasChance(builder);
			LOTRBiomeFeatures.addTundraBushesFreq(builder, 2, new WeightedBlockStateProvider().add(((Block) LOTRBlocks.PINE_LEAVES.get()).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 1), 32);
		}
	}

	public static class Minhiriath extends EriadorBiome {
		public Minhiriath(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.3F).temperature(0.7F).downfall(0.5F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addBoulders(builder);
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 32, 3);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = LOTRUtil.combineVarargs(eriadorTrees(), LOTRBiomeFeatures.oakDead(), 20000, LOTRBiomeFeatures.spruceDead(), 6000, LOTRBiomeFeatures.beechDead(), 2000, LOTRBiomeFeatures.birchDead(), 1000);
			LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.06F, TreeCluster.of(8, 30), weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addPlainsFlowers(builder, 2, LOTRBlocks.LAVENDER.get(), 20);
			LOTRBiomeFeatures.addAthelasChance(builder);
		}
	}
}
