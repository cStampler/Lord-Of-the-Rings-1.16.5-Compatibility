package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class NorthlandsBiome extends LOTRBiomeBase {
	public NorthlandsBiome(boolean major) {
		this(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.ICY).depth(0.1F).scale(0.2F).temperature(0.1F).downfall(0.3F), major);
	}

	protected NorthlandsBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addWolves(builder, 2);
		this.addDeer(builder, 1);
		this.addElk(builder, 2);
		this.addBears(builder, 3);
		this.addFoxes(builder, 2);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 80, 1);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 80, 3);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.04F, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruce()), 600, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceThin()), 400, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceDead()), 1000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.pine()), 500, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.pineDead()), 500, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.fir()), 1000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.maple()), 100, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.beech()), 100);
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.MOORS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MOORS);
		LOTRBiomeFeatures.addBorealFlowers(builder, 2);
		LOTRBiomeFeatures.addTundraBushesChance(builder, 2, new WeightedBlockStateProvider().add(Blocks.SPRUCE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3).add(((Block) LOTRBlocks.MAPLE_LEAVES.get()).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3).add(((Block) LOTRBlocks.BEECH_LEAVES.get()).defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3), 16);
	}

	@Override
	public boolean doesSnowGenerate(boolean defaultDoesSnowGenerate, IWorldReader world, BlockPos pos) {
		return defaultDoesSnowGenerate && (LOTRBiomeWrapper.isSnowBlockBelow(world, pos) || isTundraSnowy(pos));
	}

	protected final double getSnowVarietyNoise(BlockPos pos) {
		int x = pos.getX();
		int z = pos.getZ();
		double d1 = SNOW_VARIETY_NOISE.getValue(x * 0.002D, z * 0.002D, false);
		double d2 = SNOW_VARIETY_NOISE.getValue(x * 0.05D, z * 0.05D, false);
		double d3 = SNOW_VARIETY_NOISE.getValue(x * 0.3D, z * 0.3D, false);
		d2 *= 0.3D;
		d3 *= 0.3D;
		return d1 + d2 + d3;
	}

	protected boolean isTundraSnowy(BlockPos pos) {
		return getSnowVarietyNoise(pos) > 0.8D;
	}

	@Override
	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		super.setupBiomeAmbience(builder);
		builder.grassColorModifier(LOTRGrassColorModifiers.NORTHLANDS);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.07D).threshold(0.4D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.07D).threshold(0.6D).state(Blocks.STONE)));
	}

	public static class DenseForest extends NorthlandsBiome.Forest {
		public DenseForest(boolean major) {
			super(major);
		}

		@Override
		protected boolean isDenseForest() {
			return true;
		}
	}

	public static class DenseSnowyForest extends NorthlandsBiome.SnowyForest {
		public DenseSnowyForest(boolean major) {
			super(major);
		}

		@Override
		protected boolean isDenseForest() {
			return true;
		}
	}

	public static class Forest extends NorthlandsBiome {
		public Forest(boolean major) {
			this(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.FOREST).depth(0.1F).scale(0.5F).temperature(0.1F).downfall(0.7F), major);
		}

		protected Forest(Builder builder, boolean major) {
			super(builder, major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			if (isDenseForest()) {
				LOTRBiomeFeatures.addTrees(this, builder, 5, 0.5F, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruce()), 200, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceThin()), 100, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceMega()), 2000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceThinMega()), 200, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceDead()), 200, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.pine()), 700, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.pineDead()), 200, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.fir()), 500, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceShrub()), 500);
			} else {
				LOTRBiomeFeatures.addTrees(this, builder, 2, 0.8F, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruce()), 2000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceThin()), 1000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceDead()), 500, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.pine()), 2000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.pineDead()), 400, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.fir()), 2000, LOTRBiomeFeatures.snowWrapTree(LOTRBiomeFeatures.spruceShrub()), 600);
			}

			LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.MOORS_WITH_FERNS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_MOORS_WITH_FERNS);
			LOTRBiomeFeatures.addBorealFlowers(builder, 2);
			LOTRBiomeFeatures.addSparseFoxBerryBushes(builder);
		}

		protected boolean isDenseForest() {
			return false;
		}
	}

	public static class SnowyForest extends NorthlandsBiome.Forest {
		public SnowyForest(boolean major) {
			super(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.FOREST).depth(0.1F).scale(0.5F).temperature(0.05F).downfall(0.4F), major);
		}

		@Override
		protected boolean isTundraSnowy(BlockPos pos) {
			return getSnowVarietyNoise(pos) > -0.4D;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.07D).threshold(0.4D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.07D).threshold(0.6D).state(Blocks.STONE), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.3D, 0.07D).threshold(-0.3D).state(Blocks.SNOW_BLOCK).topOnly()));
		}
	}

	public static class SnowyNorthlands extends NorthlandsBiome {
		public SnowyNorthlands(boolean major) {
			super(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.ICY).depth(0.1F).scale(0.2F).temperature(0.05F).downfall(0.2F), major);
		}

		@Override
		protected boolean isTundraSnowy(BlockPos pos) {
			return true;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.07D).threshold(0.4D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.07D).threshold(0.6D).state(Blocks.STONE), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.3D, 0.07D).threshold(-0.6D).state(Blocks.SNOW_BLOCK).topOnly()));
		}
	}
}
