package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class AngmarBiome extends LOTRBiomeBase {
	public AngmarBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.6F).temperature(0.2F).downfall(0.3F), major);
	}

	protected AngmarBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(7896151).setSky(5654333).setClouds(3815994).setFog(3815994);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addWolves(builder, 2);
		this.addBears(builder, 1);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 6, 1);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addStoneOrcishOres(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.ANGMAR_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ANGMAR_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_ANGMAR_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_ANGMAR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.CRACKED_STONE_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ORC_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.spruceThin(), 1000, LOTRBiomeFeatures.spruce(), 2000, LOTRBiomeFeatures.spruceDead(), 1000, LOTRBiomeFeatures.charred(), 1000, LOTRBiomeFeatures.fir(), 1000, LOTRBiomeFeatures.pine(), 2000, LOTRBiomeFeatures.pineDead(), 500 };
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.25F, weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 4, 0.1F, 80, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.MUTED);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MUTED);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.07D).threshold(0.25D).state(Blocks.STONE)));
	}

	public static class Mountains extends AngmarBiome {
		public Mountains(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(1.6F).scale(1.5F).temperature(0.25F).downfall(0.3F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		}

		@Override
		protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addStoneVariants(builder);
			LOTRBiomeFeatures.addDeepDiorite(builder);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(130).state(Blocks.SNOW_BLOCK).topOnly(), MountainTerrainProvider.MountainLayer.layerBuilder().above(110).useStone()));
		}
	}
}
