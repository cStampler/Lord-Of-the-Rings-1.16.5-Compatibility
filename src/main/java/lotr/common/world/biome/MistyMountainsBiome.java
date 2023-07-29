package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;

public class MistyMountainsBiome extends LOTRBiomeBase {
	public MistyMountainsBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(2.0F).scale(2.0F).temperature(0.2F).downfall(0.5F), major);
	}

	protected MistyMountainsBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setSky(12241873);
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
		LOTRBiomeFeatures.addMithrilOre(builder, 4);
		LOTRBiomeFeatures.addGlowstoneOre(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addGranite(builder);
		LOTRBiomeFeatures.addDeepDiorite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Object[] treeParams = { LOTRBiomeFeatures.spruce(), 400, LOTRBiomeFeatures.spruceThin(), 400, LOTRBiomeFeatures.spruceMega(), 100, LOTRBiomeFeatures.spruceThinMega(), 20, LOTRBiomeFeatures.spruceDead(), 50, LOTRBiomeFeatures.fir(), 500, LOTRBiomeFeatures.pine(), 500, LOTRBiomeFeatures.pineDead(), 50, LOTRBiomeFeatures.larch(), 300 };
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.05F, treeParams);
		LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 2, 0.1F, 100, treeParams);
		LOTRBiomeFeatures.addGrass(this, builder, 3, GrassBlends.MUTED);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MUTED);
		LOTRBiomeFeatures.addMountainsFlowers(builder, 1, LOTRBlocks.DWARFWORT.get(), 1);
	}

	@Override
	public Biome getRiver(IWorld world) {
		return isFoothills() ? super.getRiver(world) : null;
	}

	@Override
	public boolean hasMountainsMist() {
		return true;
	}

	protected boolean isFoothills() {
		return false;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		if (!isFoothills()) {
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(120).state(Blocks.SNOW_BLOCK).topOnly(), MountainTerrainProvider.MountainLayer.layerBuilder().above(90).useStone()));
		}

	}

	public static class Foothills extends MistyMountainsBiome {
		public Foothills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.7F).scale(0.9F).temperature(0.25F).downfall(0.6F), major);
		}

		@Override
		public boolean hasMountainsMist() {
			return false;
		}

		@Override
		protected boolean isFoothills() {
			return true;
		}
	}
}
