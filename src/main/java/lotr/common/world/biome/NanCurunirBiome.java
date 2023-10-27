package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class NanCurunirBiome extends LOTRBiomeBase {
	public NanCurunirBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.1F).temperature(0.6F).downfall(0.4F), major);
		biomeColors.setSky(8683640);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 40, 3);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.URUK_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.URUK_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.COBBLESTONE_WALL.defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ORC_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.05F, LOTRBiomeFeatures.oak(), 1000, LOTRBiomeFeatures.oakTall(), 1000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakDead(), 2000, LOTRBiomeFeatures.spruce(), 1000, LOTRBiomeFeatures.spruceDead(), 1000, LOTRBiomeFeatures.charred(), 3000);
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 1);
		LOTRBiomeFeatures.addFallenLogs(builder, 1);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.09D).threshold(0.15D).state(Blocks.COARSE_DIRT).topOnly()));
	}
}
