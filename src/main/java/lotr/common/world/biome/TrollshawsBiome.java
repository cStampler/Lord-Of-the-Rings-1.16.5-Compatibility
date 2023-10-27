package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class TrollshawsBiome extends LOTRBiomeBase {
	public TrollshawsBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.15F).scale(0.9F).temperature(0.6F).downfall(0.8F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder);
		this.addWolves(builder, 2);
		this.addDeer(builder, 1);
		this.addBears(builder, 3);
		this.addElk(builder, 2);
		this.addFoxes(builder, 1);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 2, 6, 8, 3);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
		LOTRBiomeFeatures.addDiorite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] weightedTrees = { LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakTall(), 5000, LOTRBiomeFeatures.oakFancy(), 2000, LOTRBiomeFeatures.oakBees(), 10, LOTRBiomeFeatures.oakTallBees(), 10, LOTRBiomeFeatures.oakFancyBees(), 2, LOTRBiomeFeatures.beech(), 5000, LOTRBiomeFeatures.beechFancy(), 2000, LOTRBiomeFeatures.beechBees(), 5, LOTRBiomeFeatures.beechFancyBees(), 2, LOTRBiomeFeatures.spruce(), 1000, LOTRBiomeFeatures.fir(), 1000, LOTRBiomeFeatures.pine(), 1000, LOTRBiomeFeatures.pineDead(), 50, LOTRBiomeFeatures.maple(), 500, LOTRBiomeFeatures.mapleFancy(), 200, LOTRBiomeFeatures.mapleBees(), 10, LOTRBiomeFeatures.mapleFancyBees(), 10, LOTRBiomeFeatures.aspen(), 1000, LOTRBiomeFeatures.aspenLarge(), 200 };
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.4F, TreeCluster.of(6, 10), weightedTrees);
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 10, 0.0F, 82, weightedTrees);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_WITH_FERNS);
		LOTRBiomeFeatures.addForestFlowers(builder, 3);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 3);
		LOTRBiomeFeatures.addAthelasChance(builder);
		LOTRBiomeFeatures.addFoxBerryBushes(builder);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.2D, 0.06D, 0.007D).weights(1, 1, 2).threshold(0.45D).state(Blocks.GRANITE), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.2D, 0.06D, 0.007D).weights(1, 1, 2).threshold(0.325D).state(Blocks.DIORITE)));
	}
}
