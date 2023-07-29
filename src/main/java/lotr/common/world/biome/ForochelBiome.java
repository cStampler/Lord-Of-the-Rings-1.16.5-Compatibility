package lotr.common.world.biome;

import lotr.common.block.DripstoneBlock;
import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class ForochelBiome extends LOTRBiomeBase {
	public ForochelBiome(boolean major) {
		super(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.ICY).depth(0.1F).scale(0.2F).temperature(0.1F).downfall(0.3F), major);
		biomeColors.setSky(11783899);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addWolves(builder, 1);
		this.addDeer(builder, 1);
		this.addElk(builder, 2);
		this.addBears(builder, 2);
		this.addFoxes(builder, 2);
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.POLAR_BEAR, 1, 1, 2));
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 80, 1);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 80, 3);
	}

	@Override
	protected void addDripstones(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addDripstones(builder);
		LOTRBiomeFeatures.addDripstones(builder, (DripstoneBlock) LOTRBlocks.ICE_DRIPSTONE.get(), 2);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addStoneVariants(builder);
		LOTRBiomeFeatures.addPackedIceVeins(builder, 40);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.LOSSOTH_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.SNOW_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.LARCH_FENCE.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.BLUBBER_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.04F, TreeCluster.of(8, 100), LOTRBiomeFeatures.spruce(), 600, LOTRBiomeFeatures.spruceThin(), 400, LOTRBiomeFeatures.spruceDead(), 3000, LOTRBiomeFeatures.pine(), 200, LOTRBiomeFeatures.pineDead(), 600, LOTRBiomeFeatures.fir(), 1000);
		LOTRBiomeFeatures.addGrass(this, builder, 1, GrassBlends.MUTED);
		LOTRBiomeFeatures.addBorealFlowers(builder, 2);
	}

	@Override
	public boolean doesSnowGenerate(boolean defaultDoesSnowGenerate, IWorldReader world, BlockPos pos) {
		return defaultDoesSnowGenerate && (LOTRBiomeWrapper.isSnowBlockBelow(world, pos) || isForochelSnowy(pos));
	}

	private boolean isForochelSnowy(BlockPos pos) {
		int x = pos.getX();
		int z = pos.getZ();
		double d1 = SNOW_VARIETY_NOISE.getValue(x * 0.002D, z * 0.002D, false);
		double d2 = SNOW_VARIETY_NOISE.getValue(x * 0.05D, z * 0.05D, false);
		double d3 = SNOW_VARIETY_NOISE.getValue(x * 0.3D, z * 0.3D, false);
		d2 *= 0.3D;
		d3 *= 0.3D;
		return d1 + d2 + d3 > 0.62D;
	}

	@Override
	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		super.setupBiomeAmbience(builder);
		builder.grassColorModifier(LOTRGrassColorModifiers.FOROCHEL);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.06D).threshold(0.3D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.02D).threshold(0.35D).state(Blocks.SNOW_BLOCK).topOnly()));
	}
}
