package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class LothlorienBiome extends LOTRBiomeBase {
	public LothlorienBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.1F).scale(0.3F).temperature(0.8F).downfall(0.8F), major);
	}

	protected LothlorienBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(12837416);
		biomeColors.setFog(16770660);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 4);
		this.addDeer(builder, 2);
	}

	@Override
	protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWhiteSandSediments(builder);
	}

	@Override
	protected void addCobwebs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWaterSprings(builder);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addEdhelvirOre(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.GALADHRIM_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MALLORN_PLANKS.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MALLORN_FENCE.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GOLD_MALLORN_TORCH.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.BLUE_MALLORN_TORCH.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.GREEN_MALLORN_TORCH.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.SILVER_MALLORN_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 3, 0.1F, LOTRBiomeFeatures.mallorn(), 3000, LOTRBiomeFeatures.mallornBees(), 30, LOTRBiomeFeatures.mallornBoughs(), 6000, LOTRBiomeFeatures.mallornParty(), 1000, LOTRBiomeFeatures.oak(), 3000, LOTRBiomeFeatures.oakFancy(), 500, LOTRBiomeFeatures.oakBees(), 30, LOTRBiomeFeatures.oakFancyBees(), 5, LOTRBiomeFeatures.oakParty(), 100, LOTRBiomeFeatures.larch(), 2000, LOTRBiomeFeatures.beech(), 1000, LOTRBiomeFeatures.beechFancy(), 200, LOTRBiomeFeatures.beechBees(), 10, LOTRBiomeFeatures.beechFancyBees(), 2, LOTRBiomeFeatures.beechParty(), 100, LOTRBiomeFeatures.aspen(), 1000, LOTRBiomeFeatures.aspenLarge(), 200, LOTRBiomeFeatures.lairelosse(), 500);
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addForestFlowers(builder, 6, LOTRBlocks.ELANOR.get(), 20, LOTRBlocks.NIPHREDIL.get(), 20);
		LOTRBiomeFeatures.addTreeTorches(builder, 120, 60, 110, ((Block) LOTRBlocks.GOLD_MALLORN_WALL_TORCH.get()).defaultBlockState(), ((Block) LOTRBlocks.BLUE_MALLORN_WALL_TORCH.get()).defaultBlockState(), ((Block) LOTRBlocks.GREEN_MALLORN_WALL_TORCH.get()).defaultBlockState(), ((Block) LOTRBlocks.SILVER_MALLORN_WALL_TORCH.get()).defaultBlockState());
	}

	@Override
	public boolean hasBreakMallornResponse() {
		return true;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setPodzol(false);
		config.setSurfaceNoisePaths(true);
	}

	public static class Eaves extends LothlorienBiome {
		public Eaves(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.1F).scale(0.2F).temperature(0.8F).downfall(0.8F), major);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 2, 0.5F, LOTRBiomeFeatures.mallorn(), 500, LOTRBiomeFeatures.mallornBees(), 5, LOTRBiomeFeatures.mallornBoughs(), 100, LOTRBiomeFeatures.oak(), 3000, LOTRBiomeFeatures.oakFancy(), 500, LOTRBiomeFeatures.oakBees(), 30, LOTRBiomeFeatures.oakFancyBees(), 5, LOTRBiomeFeatures.larch(), 2000, LOTRBiomeFeatures.beech(), 1000, LOTRBiomeFeatures.beechFancy(), 200, LOTRBiomeFeatures.beechBees(), 10, LOTRBiomeFeatures.beechFancyBees(), 2, LOTRBiomeFeatures.aspen(), 1000, LOTRBiomeFeatures.aspenLarge(), 200);
			LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addForestFlowers(builder, 2, LOTRBlocks.ELANOR.get(), 10, LOTRBlocks.NIPHREDIL.get(), 10);
		}
	}
}
