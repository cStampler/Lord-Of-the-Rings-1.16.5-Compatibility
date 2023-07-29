package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;

public class WhiteMountainsBiome extends LOTRBiomeBase {
	public WhiteMountainsBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(1.5F).scale(2.0F).temperature(0.6F).downfall(0.8F), major);
	}

	protected WhiteMountainsBiome(Builder builder, boolean major) {
		super(builder, major);
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
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 1, 0.1F, LOTRBiomeFeatures.oak(), 1000, LOTRBiomeFeatures.oakFancy(), 500, LOTRBiomeFeatures.birch(), 200, LOTRBiomeFeatures.birchFancy(), 50, LOTRBiomeFeatures.beech(), 200, LOTRBiomeFeatures.beechFancy(), 50, LOTRBiomeFeatures.spruce(), 3000, LOTRBiomeFeatures.larch(), 3000, LOTRBiomeFeatures.fir(), 5000, LOTRBiomeFeatures.pine(), 5000, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.MUTED);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_MUTED);
		LOTRBiomeFeatures.addMountainsFlowers(builder, 2);
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
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(100).state(LOTRBlocks.GONDOR_ROCK).excludeStone()));
		}

	}

	public static class Foothills extends WhiteMountainsBiome {
		public Foothills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.5F).scale(0.9F).temperature(0.6F).downfall(0.7F), major);
		}

		@Override
		protected boolean isFoothills() {
			return true;
		}
	}
}
