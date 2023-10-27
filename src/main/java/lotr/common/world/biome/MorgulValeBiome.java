package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.ParticleEffectAmbience;

public class MorgulValeBiome extends BaseMordorBiome {
	public MorgulValeBiome(boolean major) {
		super(new Builder().precipitation(RainType.NONE).biomeCategory(Category.PLAINS).depth(0.1F).scale(0.1F).temperature(0.8F).downfall(0.4F), 3563598, major);
		biomeColors.setGrass(6054733).setFoliage(4475954).setSky(7835270).setClouds(5860197).setFog(6318950);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 0, 1, 48, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 0, 1, 48, 3);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addExtraMordorGulduril(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.2F, LOTRBiomeFeatures.oak(), 2000, LOTRBiomeFeatures.oakDesert(), 5000, LOTRBiomeFeatures.oakDead(), 5000, LOTRBiomeFeatures.charred(), 5000);
		LOTRBiomeFeatures.addGrass(this, builder, 3, GrassBlends.MUTED);
		LOTRBiomeFeatures.addFlowers(builder, 1, LOTRBlocks.MORGUL_FLOWER.get(), 20);
		LOTRBiomeFeatures.addExtraMorgulFlowersByWater(builder, 4);
		LOTRBiomeFeatures.addMordorMoss(builder, 40);
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
		config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.06D).threshold(0.35D).state(LOTRBlocks.MORDOR_DIRT), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.3D, 0.06D).threshold(0.35D).state(LOTRBlocks.MORDOR_GRAVEL), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(3).scales(0.3D, 0.06D).threshold(0.55D).state(LOTRBlocks.MORDOR_ROCK)));
	}
}
