package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class LamedonBiome extends BaseGondorBiome {
	public LamedonBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.2F).scale(0.2F).temperature(0.7F).downfall(0.9F), major);
	}

	protected LamedonBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setGrass(11646287);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 6);
		addExtraSheep(builder, 3);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addBoulders(builder);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 16, 4);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, getLamedonBaseTreeRate(), 0.1F, TreeCluster.of(10, 20), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 50, LOTRBiomeFeatures.oakFancyBees(), 10, LOTRBiomeFeatures.birch(), 500, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.birchBees(), 5, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.beech(), 500, LOTRBiomeFeatures.beechFancy(), 100, LOTRBiomeFeatures.beechBees(), 5, LOTRBiomeFeatures.beechFancyBees(), 1, LOTRBiomeFeatures.larch(), 3000, LOTRBiomeFeatures.aspen(), 3000, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 3);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
		LOTRBiomeFeatures.addAthelasChance(builder);
		LOTRBiomeFeatures.addWildPipeweedChance(builder, 24);
	}

	protected int getLamedonBaseTreeRate() {
		return 0;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		super.setupSurface(config);
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.07D).threshold(0.35D).state(Blocks.COARSE_DIRT).topOnly()));
	}

	public static class Hills extends LamedonBiome {
		public Hills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.6F).scale(0.9F).temperature(0.6F).downfall(0.7F), major);
			biomeColors.resetGrass();
		}

		@Override
		protected int getLamedonBaseTreeRate() {
			return 1;
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}
	}
}
