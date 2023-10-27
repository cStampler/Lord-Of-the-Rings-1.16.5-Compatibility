package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class EasternDesolationBiome extends MordorBiome {
	public EasternDesolationBiome(boolean major) {
		super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.DESERT).depth(0.2F).scale(0.2F).temperature(0.8F).downfall(0.3F), major);
		biomeColors.setGrass(8880748);
		biomeColors.setSky(9538431);
		biomeColors.resetClouds().setCloudCoverage(0.6F);
		biomeColors.resetFog();
	}

	@Override
	protected void addBasalt(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	public ExtendedWeatherType getBiomeExtendedWeather() {
		return ExtendedWeatherType.NONE;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		super.setupSurface(config);
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.06D).threshold(0.3D).state(Blocks.COARSE_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.06D).threshold(0.25D).state(LOTRBlocks.MORDOR_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.3D, 0.06D).threshold(0.35D).state(LOTRBlocks.MORDOR_GRAVEL)));
	}
}
