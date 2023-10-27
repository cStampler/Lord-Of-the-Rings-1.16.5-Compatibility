package lotr.common.world.biome;

import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.MountainTerrainProvider;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class MirkwoodBiome extends LOTRBiomeBase {
	public MirkwoodBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.4F).temperature(0.6F).downfall(0.8F), 1708838, major);
	}

	protected MirkwoodBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	protected MirkwoodBiome(Builder builder, int waterFogColor, boolean major) {
		super(builder, waterFogColor, major);
		biomeColors.setGrass(2841381).setFoliage(3096365).setClouds(11123133).setFog(2774107).setFoggy(true).setWater(waterFogColor);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addDeer(builder, 3);
		this.addElk(builder, 8);
		this.addBears(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 14, 0.0F, LOTRBiomeFeatures.mirkOak(), 8000, LOTRBiomeFeatures.mirkOakParty(), 2000, LOTRBiomeFeatures.mirkOakShrub(), 6000, LOTRBiomeFeatures.oakFancy(), 3000, LOTRBiomeFeatures.spruce(), 1000, LOTRBiomeFeatures.fir(), 1000, LOTRBiomeFeatures.pine(), 2000, LOTRBiomeFeatures.pineDead(), 200);
		LOTRBiomeFeatures.addGrass(this, builder, 12, GrassBlends.MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 6, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
		LOTRBiomeFeatures.addForestFlowers(builder, 1);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 2);
		LOTRBiomeFeatures.addMirkShroomsFreq(builder, 1);
		LOTRBiomeFeatures.addFallenLogs(builder, 2);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.MIRKWOOD_PATH.withRepair(0.9F);
	}

	public static class Mountains extends MirkwoodBiome {
		public Mountains(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(1.5F).scale(1.5F).temperature(0.28F).downfall(0.9F), 1708838, major);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			int treeline = 100;
			LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 3, 0.25F, treeline, LOTRBiomeFeatures.mirkOak(), 200, LOTRBiomeFeatures.spruce(), 300, LOTRBiomeFeatures.fir(), 1000, LOTRBiomeFeatures.pine(), 300, LOTRBiomeFeatures.pineDead(), 50);
			LOTRBiomeFeatures.addTreesBelowTreeline(this, builder, 1, 0.25F, treeline + 10, LOTRBiomeFeatures.mirkOakShrub(), 200, LOTRBiomeFeatures.spruceShrub(), 300, LOTRBiomeFeatures.firShrub(), 1000, LOTRBiomeFeatures.pineShrub(), 300);
			LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.MUTED_WITH_FERNS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 4, GrassBlends.DOUBLE_MUTED_WITH_FERNS);
			LOTRBiomeFeatures.addForestFlowers(builder, 1);
			LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 2);
			LOTRBiomeFeatures.addMirkShroomsFreq(builder, 1);
			LOTRBiomeFeatures.addFallenLogsBelowTreeline(builder, 1, treeline);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(150).state(Blocks.SNOW_BLOCK).topOnly(), MountainTerrainProvider.MountainLayer.layerBuilder().above(110).useStone()));
		}
	}

	public static class Northern extends MirkwoodBiome {
		public Northern(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.4F).temperature(0.5F).downfall(0.8F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
			this.addFoxes(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTrees(this, builder, 14, 0.1F, LOTRBiomeFeatures.greenOak(), 500, LOTRBiomeFeatures.greenOakBees(), 5, LOTRBiomeFeatures.greenOakParty(), 100, LOTRBiomeFeatures.greenOakShrub(), 500, LOTRBiomeFeatures.mirkOak(), 50, LOTRBiomeFeatures.mirkOakShrub(), 50, LOTRBiomeFeatures.oak(), 500, LOTRBiomeFeatures.oakBees(), 5, LOTRBiomeFeatures.oakFancy(), 500, LOTRBiomeFeatures.oakFancyBees(), 5, LOTRBiomeFeatures.oakParty(), 100, LOTRBiomeFeatures.oakShrub(), 1000, LOTRBiomeFeatures.spruce(), 1000, LOTRBiomeFeatures.spruceThin(), 500, LOTRBiomeFeatures.spruceMega(), 200, LOTRBiomeFeatures.spruceThinMega(), 200, LOTRBiomeFeatures.larch(), 500, LOTRBiomeFeatures.fir(), 2000, LOTRBiomeFeatures.pine(), 2000, LOTRBiomeFeatures.pineDead(), 200, LOTRBiomeFeatures.aspen(), 500, LOTRBiomeFeatures.aspenLarge(), 100);
			LOTRBiomeFeatures.addGrass(this, builder, 12, GrassBlends.WITH_FERNS);
			LOTRBiomeFeatures.addDoubleGrass(builder, 6, GrassBlends.DOUBLE_WITH_FERNS);
			LOTRBiomeFeatures.addForestFlowers(builder, 2);
			LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
			LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 2);
			LOTRBiomeFeatures.addFallenLogs(builder, 1);
			LOTRBiomeFeatures.addFoxBerryBushes(builder);
		}
	}
}
