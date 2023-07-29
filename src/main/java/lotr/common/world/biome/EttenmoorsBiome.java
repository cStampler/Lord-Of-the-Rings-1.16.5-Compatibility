package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.*;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class EttenmoorsBiome extends LOTRBiomeBase {
	public EttenmoorsBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.5F).scale(0.7F).temperature(0.2F).downfall(0.6F), major);
		biomeColors.setGrass(11910259);
		biomeColors.setSky(12965352);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addWolves(builder, 2);
		this.addElk(builder, 1);
		this.addBears(builder, 1);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 3, 2);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 2, 5, 12, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.fir(), 400, LOTRBiomeFeatures.pine(), 800, LOTRBiomeFeatures.pineDead(), 100, LOTRBiomeFeatures.spruce(), 400, LOTRBiomeFeatures.spruceThin(), 400, LOTRBiomeFeatures.spruceDead(), 100 };
		Object[] weightedTreesWithDead = LOTRUtil.combineVarargs(weightedTrees, LOTRBiomeFeatures.spruceDead(), 3000);
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.05F, weightedTreesWithDead);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 2, 0.5F, 87, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.MOORS);
		LOTRBiomeFeatures.addBorealFlowers(builder, 1);
		LOTRBiomeFeatures.addAthelasChance(builder);
		LOTRBiomeFeatures.addTundraBushesChance(builder, 3, new WeightedBlockStateProvider().add(Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3).add(((Block) LOTRBlocks.FIR_LEAVES.get()).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3).add(((Block) LOTRBlocks.PINE_LEAVES.get()).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3), 40);
	}
}
