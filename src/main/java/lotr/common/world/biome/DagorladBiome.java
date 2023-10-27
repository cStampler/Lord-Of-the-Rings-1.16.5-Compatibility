package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.ParticleEffectAmbience;

public class DagorladBiome extends LOTRBiomeBase {
	public DagorladBiome(boolean major) {
		super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.1F).temperature(0.8F).downfall(0.1F), major);
		biomeColors.setGrass(9208427);
		biomeColors.setSky(6181446);
		biomeColors.setClouds(3355443);
		biomeColors.setFog(6710886);
		biomeColors.setWater(2498845);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 40, 3);
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState(), 1, 4, 24, 2);
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_GRAVEL.get()).defaultBlockState(), 1, 4, 24, 2);
	}

	@Override
	protected void addPumpkins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesIncrease(this, builder, 0, 0.0125F, 3, LOTRBiomeFeatures.charred(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 1, GrassBlends.BASIC);
		LOTRBiomeFeatures.addMordorGrass(builder, 1);
		LOTRBiomeFeatures.addDeadBushAtSurfaceChance(builder, 2);
	}

	@Override
	protected ExtendedWeatherType getBiomeExtendedWeather() {
		return ExtendedWeatherType.ASHFALL;
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}

	@Override
	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		super.setupBiomeAmbience(builder);
		builder.ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.01F));
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setUnderwater(((Block) LOTRBlocks.MORDOR_GRAVEL.get()).defaultBlockState());
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.6D, 0.09D).threshold(0.3D).state(Blocks.COARSE_DIRT).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.6D, 0.09D).threshold(0.15D).state(Blocks.GRAVEL).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.6D, 0.09D).threshold(0.1D).state(LOTRBlocks.MORDOR_GRAVEL), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(4).scales(0.6D, 0.09D).threshold(0.2D).state(LOTRBlocks.MORDOR_DIRT)));
	}
}
