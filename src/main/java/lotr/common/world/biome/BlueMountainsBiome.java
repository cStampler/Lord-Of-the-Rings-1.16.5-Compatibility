package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class BlueMountainsBiome extends LOTRBiomeBase {
	public BlueMountainsBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(1.0F).scale(2.2F).temperature(0.22F).downfall(0.8F), major);
	}

	protected BlueMountainsBiome(Builder builder, boolean major) {
		super(builder, major);
		biomeColors.setSky(7506425);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
	}

	@Override
	protected void addCobwebs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
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
		LOTRBiomeFeatures.addExtraCoal(builder, 8, 10, 128);
		LOTRBiomeFeatures.addExtraIron(builder, 4, 10, 96);
		LOTRBiomeFeatures.addGlowstoneOre(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addAndesite(builder);
		LOTRBiomeFeatures.addDeepDiorite(builder);
		LOTRBiomeFeatures.addBlueRockPatches(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.BLUE_MOUNTAINS_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.BLUE_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.BLUE_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.LANTERN.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.4F, TreeCluster.of(10, 12), LOTRBiomeFeatures.oak(), 3000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.spruce(), 5000, LOTRBiomeFeatures.spruceDead(), 50, LOTRBiomeFeatures.birch(), 4000, LOTRBiomeFeatures.pine(), 5000, LOTRBiomeFeatures.pineDead(), 50, LOTRBiomeFeatures.fir(), 5000);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.MUTED);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_MUTED);
		LOTRBiomeFeatures.addMountainsFlowers(builder, 1, LOTRBlocks.DWARFWORT.get(), 1);
	}

	@Override
	public Biome getRiver(IWorld world) {
		return isFoothills() ? super.getRiver(world) : null;
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.DWARVEN;
	}

	protected boolean isFoothills() {
		return false;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		if (!isFoothills()) {
			config.setMountainTerrain(MountainTerrainProvider.createMountainTerrain(MountainTerrainProvider.MountainLayer.layerBuilder().above(110).state(Blocks.SNOW_BLOCK).topOnly(), MountainTerrainProvider.MountainLayer.layerBuilder().above(90).state(LOTRBlocks.BLUE_ROCK).excludeStone()));
		}

	}

	public static class Foothills extends BlueMountainsBiome {
		public Foothills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.EXTREME_HILLS).depth(0.5F).scale(0.9F).temperature(0.5F).downfall(0.8F), major);
		}

		@Override
		protected boolean isFoothills() {
			return true;
		}
	}
}
