package lotr.common.world.biome;

import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;

public class GreyMountainsBiome extends LOTRBiomeBase {
	public GreyMountainsBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(1.8F).scale(2.0F).temperature(0.28F).downfall(0.3F), major);
	}

	protected GreyMountainsBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setSky(10862798);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		if (!isFoothills()) {
			LOTRBiomeFeatures.addWaterLavaSpringsReducedAboveground(builder, 80, 0.15F);
		} else {
			super.addLiquidSprings(builder);
		}

	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addStoneVariants(builder);
		LOTRBiomeFeatures.addDeepDiorite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 3, 0.1F, 100, LOTRBiomeFeatures.spruce(), 4000, LOTRBiomeFeatures.spruceThin(), 4000, LOTRBiomeFeatures.spruceMega(), 500, LOTRBiomeFeatures.spruceThinMega(), 100, LOTRBiomeFeatures.spruceDead(), 500, LOTRBiomeFeatures.larch(), 5000, LOTRBiomeFeatures.fir(), 5000, LOTRBiomeFeatures.pine(), 5000, LOTRBiomeFeatures.pineDead(), 250);
		LOTRBiomeFeatures.addGrass(this, builder, 2, GrassBlends.MUTED);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MUTED);
		LOTRBiomeFeatures.addMountainsFlowers(builder, 1);
	}

	@Override
	public Biome getRiver(IWorld world) {
		return isFoothills() ? super.getRiver(world) : null;
	}

	protected boolean isFoothills() {
		return false;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		if (!isFoothills()) {
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(150).state(Blocks.SNOW_BLOCK).topOnly(), MountainTerrainProvider.MountainLayer.layerBuilder().above(110).useStone()));
		}

	}

	public static class Foothills extends GreyMountainsBiome {
		public Foothills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.5F).scale(0.9F).temperature(0.5F).downfall(0.7F), major);
		}

		@Override
		protected boolean isFoothills() {
			return true;
		}
	}
}
