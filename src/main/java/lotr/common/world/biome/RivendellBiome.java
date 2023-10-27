package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
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

public class RivendellBiome extends LOTRBiomeBase {
	public RivendellBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.12F).scale(0.6F).temperature(0.7F).downfall(1.0F), major);
	}

	protected RivendellBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setWater(6933979);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.COW, 40, 4, 4));
		this.addHorsesDonkeys(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addBoulders(builder);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 0, 1, 12, 6);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 24, 3);
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 0, 1, 60, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addEdhelvirOre(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.RIVENDELL_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HIGH_ELVEN_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.BIRCH_FENCE.defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HIGH_ELVEN_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 2000, LOTRBiomeFeatures.oakBees(), 50, LOTRBiomeFeatures.oakFancyBees(), 20, LOTRBiomeFeatures.birch(), 5000, LOTRBiomeFeatures.birchFancy(), 1000, LOTRBiomeFeatures.birchBees(), 50, LOTRBiomeFeatures.birchFancyBees(), 10, LOTRBiomeFeatures.beech(), 500, LOTRBiomeFeatures.beechFancy(), 200, LOTRBiomeFeatures.beechBees(), 5, LOTRBiomeFeatures.beechFancyBees(), 2, LOTRBiomeFeatures.larch(), 1500, LOTRBiomeFeatures.aspen(), 1000, LOTRBiomeFeatures.aspenLarge(), 500, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1 };
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.25F, TreeCluster.of(12, 16), weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreelineIncrease(this, builder, 2, 0.2F, 2, 85, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 5);
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}

	public static class Hills extends RivendellBiome {
		public Hills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(3.7F).scale(1.0F).temperature(0.6F).downfall(0.9F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addLiquidSprings(builder);

			for (int i = 0; i < 2; ++i) {
				LOTRBiomeFeatures.addWaterSprings(builder, 80);
			}

		}

		@Override
		protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			Object[] weightedTrees = { LOTRBiomeFeatures.pine(), 5000, LOTRBiomeFeatures.pineShrub(), 3000, LOTRBiomeFeatures.fir(), 2000, LOTRBiomeFeatures.spruce(), 2000, LOTRBiomeFeatures.larch(), 2000, LOTRBiomeFeatures.aspen(), 1000, LOTRBiomeFeatures.aspenLarge(), 500, LOTRBiomeFeatures.oak(), 1000, LOTRBiomeFeatures.oakFancy(), 500, LOTRBiomeFeatures.oakBees(), 10, LOTRBiomeFeatures.oakFancyBees(), 5, LOTRBiomeFeatures.birch(), 1000, LOTRBiomeFeatures.birchFancy(), 500, LOTRBiomeFeatures.birchBees(), 10, LOTRBiomeFeatures.birchFancyBees(), 5 };
			LOTRBiomeFeatures.addTreesIncrease(this, builder, 7, 0.3F, 3, weightedTrees);
			LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addMountainsFlowers(builder, 2);
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setFillerDepth(0.0D);
			config.addSubSoilLayer(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1);
			config.addSubSoilLayer(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 5);
		}
	}
}
