package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.MountainTerrainProvider;
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

public class MordorBiome extends BaseMordorBiome {
	public MordorBiome(boolean major) {
		this(new Builder().precipitation(RainType.NONE).biomeCategory(Category.DESERT).depth(0.3F).scale(0.5F).temperature(1.5F).downfall(0.0F), major);
	}

	protected MordorBiome(Builder builder, boolean major) {
		super(builder, 3884089, major);
		biomeColors.setGrass(7496538).setFoliage(5987138).setSky(6313301).setClouds(6705223).setFog(6701621);
	}

	protected void addBasalt(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addMordorBasalt(builder, 16, 40);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 1, 3, 60, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 60, 3);
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 0, 1, 12, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 0, 1, 24, 3);
	}

	@Override
	protected void addFeatures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addFeatures(builder);
		addBasalt(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesBelowTreelineIncrease(this, builder, 0, 0.025F, 5, 100, LOTRBiomeFeatures.charred(), 100, LOTRBiomeFeatures.oakDead(), 50, LOTRBiomeFeatures.oakDesert(), 20);
		LOTRBiomeFeatures.addGrass(this, builder, 2, GrassBlends.MUTED_WITHOUT_THISTLES);
		LOTRBiomeFeatures.addMordorMoss(builder, 20);
		LOTRBiomeFeatures.addMordorGrass(builder, 4);
		LOTRBiomeFeatures.addMordorThorns(builder, 40);
		LOTRBiomeFeatures.addMorgulShrooms(builder, 32);
	}

	@Override
	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		super.setupBiomeAmbience(builder);
		builder.ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.01F));
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		super.setupSurface(config);
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.06D).threshold(0.1D).state(LOTRBlocks.MORDOR_ROCK), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.06D).threshold(0.5D).state(Blocks.COARSE_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.3D, 0.06D).threshold(0.2D).state(LOTRBlocks.MORDOR_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(4).scales(0.3D, 0.06D).threshold(0.25D).state(LOTRBlocks.MORDOR_GRAVEL)));
	}

	public static class Mountains extends MordorBiome {
		public Mountains(boolean major) {
			super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.EXTREME_HILLS).depth(2.0F).scale(2.0F).temperature(1.5F).downfall(0.0F), major);
		}

		@Override
		protected void addBasalt(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setRockyTerrain(false);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.06D).threshold(0.0D).state(LOTRBlocks.MORDOR_ROCK), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.06D).threshold(0.65D).state(Blocks.COARSE_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.3D, 0.06D).threshold(0.65D).state(LOTRBlocks.MORDOR_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(4).scales(0.3D, 0.06D).threshold(0.1D).state(LOTRBlocks.MORDOR_GRAVEL)));
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(100).state(LOTRBlocks.MORDOR_ROCK)));
		}
	}
}
