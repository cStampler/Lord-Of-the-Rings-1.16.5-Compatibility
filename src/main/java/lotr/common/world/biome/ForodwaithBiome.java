package lotr.common.world.biome;

import lotr.common.block.DripstoneBlock;
import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.MountainTerrainProvider;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class ForodwaithBiome extends LOTRBiomeBase {
	public ForodwaithBiome(boolean major) {
		this(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.ICY).depth(0.1F).scale(0.1F).temperature(0.0F).downfall(0.2F), major);
	}

	protected ForodwaithBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setSky(10069160);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.POLAR_BEAR, 1, 1, 2));
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 80, 2);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 2, 80, 5);
	}

	@Override
	protected void addDripstones(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addDripstones(builder);
		LOTRBiomeFeatures.addDripstones(builder, (DripstoneBlock) LOTRBlocks.ICE_DRIPSTONE.get(), 2);
	}

	@Override
	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addLavaSprings(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addStoneVariants(builder);
		LOTRBiomeFeatures.addPackedIceVeins(builder, 40);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setTop(Blocks.SNOW_BLOCK.defaultBlockState());
	}

	public static class Mountains extends ForodwaithBiome {
		public Mountains(boolean major) {
			super(new Builder().precipitation(RainType.SNOW).biomeCategory(Category.ICY).depth(2.0F).scale(2.0F).temperature(0.0F).downfall(0.2F), major);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(100).state(Blocks.SNOW_BLOCK).topOnly(), MountainTerrainProvider.MountainLayer.layerBuilder().above(70).useStone()));
		}
	}
}
