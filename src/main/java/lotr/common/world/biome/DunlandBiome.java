package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.*;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class DunlandBiome extends LOTRBiomeBase {
	public DunlandBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.3F).scale(0.5F).temperature(0.4F).downfall(0.7F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addWolves(builder, 2);
		this.addBears(builder, 1);
		this.addFoxes(builder, 2);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 8, 4);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.DUNLENDING_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(Blocks.COBBLESTONE.defaultBlockState(), 2).add(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.SPRUCE_FENCE.defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.spruce(), 5000, LOTRBiomeFeatures.oak(), 1000, LOTRBiomeFeatures.oakTall(), 1000, LOTRBiomeFeatures.oakFancy(), 200, LOTRBiomeFeatures.oakBees(), 10, LOTRBiomeFeatures.oakTallBees(), 10, LOTRBiomeFeatures.oakFancyBees(), 2, LOTRBiomeFeatures.pine(), 5000, LOTRBiomeFeatures.pineDead(), 50, LOTRBiomeFeatures.fir(), 5000 };
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.4F, TreeCluster.of(12, 10), weightedTrees);
		LOTRBiomeFeatures.addTreesAboveTreeline(this, builder, 6, 0.1F, 85, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addForestFlowers(builder, 2);
		LOTRBiomeFeatures.addFoxBerryBushes(builder);
	}
}
