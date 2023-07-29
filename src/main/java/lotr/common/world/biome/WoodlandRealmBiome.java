package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.*;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class WoodlandRealmBiome extends LOTRBiomeBase {
	public WoodlandRealmBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.3F).temperature(0.7F).downfall(0.9F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addElk(builder, 8);
		this.addDeer(builder, 2);
		this.addBears(builder, 1);
	}

	@Override
	protected void addCobwebs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWaterSprings(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.WOOD_ELVEN_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GREEN_OAK_PLANKS.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GREEN_OAK_FENCE.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 2, 0.33F, TreeCluster.of(16, 10), LOTRBiomeFeatures.greenOak(), 5000, LOTRBiomeFeatures.greenOakBees(), 5, LOTRBiomeFeatures.greenOakParty(), 800, LOTRBiomeFeatures.redOak(), 400, LOTRBiomeFeatures.redOakBees(), 5, LOTRBiomeFeatures.redOakParty(), 100, LOTRBiomeFeatures.greenOakShrub(), 8000, LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakBees(), 1, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakFancyBees(), 1, LOTRBiomeFeatures.spruce(), 1000, LOTRBiomeFeatures.beech(), 500, LOTRBiomeFeatures.beechBees(), 1, LOTRBiomeFeatures.beechFancy(), 1000, LOTRBiomeFeatures.beechFancyBees(), 1, LOTRBiomeFeatures.larch(), 500, LOTRBiomeFeatures.fir(), 1000, LOTRBiomeFeatures.pine(), 500, LOTRBiomeFeatures.aspen(), 500, LOTRBiomeFeatures.aspenLarge(), 100);
		LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_WITH_FERNS);
		LOTRBiomeFeatures.addForestFlowers(builder, 3);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 1);
		LOTRBiomeFeatures.addFallenLogs(builder, 1);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.WOOD_ELVEN;
	}
}
