package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import net.minecraft.block.Block;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.biome.ParticleEffectAmbience;

public class GorgorothBiome extends BaseMordorBiome {
	public GorgorothBiome(boolean major) {
		super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.DESERT).depth(0.7F).scale(0.4F).temperature(1.5F).downfall(0.0F), 2498845, major);
		biomeColors.setGrass(5980459).setFoliage(5987138).setSky(6700595).setClouds(4924185).setFog(3154711);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 2, 6, 30, 4);
	}

	@Override
	protected void addCarvers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCarversExtraCanyons(builder);
	}

	@Override
	protected void addFeatures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addFeatures(builder);
		LOTRBiomeFeatures.addMordorBasalt(builder, 1, 6);
	}

	@Override
	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWaterSprings(builder);
		LOTRBiomeFeatures.addLavaSprings(builder, 50);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesIncrease(this, builder, 0, 0.0125F, 7, LOTRBiomeFeatures.charred(), 1);
		LOTRBiomeFeatures.addMordorMoss(builder, 20);
		LOTRBiomeFeatures.addMordorGrass(builder, 2);
		LOTRBiomeFeatures.addMordorThorns(builder, 40);
		LOTRBiomeFeatures.addMorgulShrooms(builder, 32);
	}

	@Override
	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		super.setupBiomeAmbience(builder);
		builder.ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.025F));
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		super.setupSurface(config);
		config.setTop(((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState());
		config.setFiller(((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState());
		config.resetFillerDepthAndSubSoilLayers();
		config.addSubSoilLayer(((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 1000);
		config.setRockyTerrain(false);
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.08D).threshold(0.25D).state(LOTRBlocks.MORDOR_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.08D).threshold(0.4D).state(LOTRBlocks.MORDOR_GRAVEL)));
	}
}
