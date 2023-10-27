package lotr.common.world.biome;

import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class WilderlandBiome extends LOTRBiomeBase {
	public WilderlandBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.4F).temperature(0.9F).downfall(0.4F), major);
	}

	protected WilderlandBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 2);
		this.addBears(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 24, 4);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.05F, TreeCluster.of(8, 20), LOTRBiomeFeatures.oak(), 10000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 10, LOTRBiomeFeatures.oakFancyBees(), 1, LOTRBiomeFeatures.oakDead(), 5000, LOTRBiomeFeatures.spruce(), 2000, LOTRBiomeFeatures.spruceDead(), 1000);
		LOTRBiomeFeatures.addGrass(this, builder, 14, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 8, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addRhunPlainsFlowers(builder, 3);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
	}

	public static class Northern extends WilderlandBiome {
		public Northern(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.5F).temperature(0.5F).downfall(0.6F), major);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.05F, TreeCluster.of(8, 40), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 50, LOTRBiomeFeatures.oakFancyBees(), 10, LOTRBiomeFeatures.oakDead(), 5000, LOTRBiomeFeatures.spruce(), 3000, LOTRBiomeFeatures.spruceDead(), 1000, LOTRBiomeFeatures.fir(), 2000, LOTRBiomeFeatures.pine(), 2000);
			LOTRBiomeFeatures.addGrass(this, builder, 7, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 3, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addBorealFlowers(builder, 2);
		}

		private float getLocalSnowiness(BlockPos pos) {
			int x = pos.getX();
			int z = pos.getZ();
			double d1 = SNOW_VARIETY_NOISE.getValue(x * 0.002D, z * 0.002D, false);
			double d2 = SNOW_VARIETY_NOISE.getValue(x * 0.05D, z * 0.05D, false);
			double d3 = SNOW_VARIETY_NOISE.getValue(x * 0.3D, z * 0.3D, false);
			d1 *= 0.6D;
			d2 *= 0.2D;
			d3 *= 0.2D;
			float biased = (float) Math.max(d1 + d2 + d3, 0.0D) + 0.5F;
			return MathHelper.clamp(biased, 0.0F, 1.0F);
		}

		@Override
		public float getTemperatureRaw(float defaultTemperatureAtPos, BlockPos pos) {
			return defaultTemperatureAtPos - getLocalSnowiness(pos) * (getActualBiome().getBaseTemperature() - 0.15F);
		}
	}
}
