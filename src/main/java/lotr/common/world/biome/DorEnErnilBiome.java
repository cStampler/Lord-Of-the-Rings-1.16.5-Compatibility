package lotr.common.world.biome;

import lotr.common.init.*;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public class DorEnErnilBiome extends LOTRBiomeBase {
	public DorEnErnilBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.04F).scale(0.28F).temperature(0.9F).downfall(0.9F), major);
	}

	protected DorEnErnilBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder, 6);
	}

	@Override
	protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWhiteSandSediments(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.GONDOR_ROCK.get()).defaultBlockState(), 1, 2, 200, 3);
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addReeds(builder);
		LOTRBiomeFeatures.addSugarCane(builder);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addGranite(builder);
		LOTRBiomeFeatures.addGondorRockPatches(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.DOL_AMROTH_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DOL_AMROTH_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DOL_AMROTH_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.LANTERN.defaultBlockState(), 1));
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.03F, TreeCluster.of(10, 30), dorEnErnilTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 4, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 4);
		LOTRBiomeFeatures.addAthelasChance(builder);
		LOTRBiomeFeatures.addWildPipeweedChance(builder, 24);
	}

	protected final Object[] dorEnErnilTrees() {
		return new Object[] { LOTRBiomeFeatures.birch(), 2000, LOTRBiomeFeatures.birchFancy(), 2000, LOTRBiomeFeatures.birchBees(), 20, LOTRBiomeFeatures.birchFancyBees(), 20, LOTRBiomeFeatures.oak(), 800, LOTRBiomeFeatures.oakTall(), 800, LOTRBiomeFeatures.oakFancy(), 800, LOTRBiomeFeatures.oakBees(), 8, LOTRBiomeFeatures.oakTallBees(), 8, LOTRBiomeFeatures.oakFancyBees(), 8, LOTRBiomeFeatures.cedar(), 1000, LOTRBiomeFeatures.cypress(), 500, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pearBees(), 1 };
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.DOL_AMROTH;
	}

	@Override
	public LOTRBiomeBase getShore() {
		return LOTRBiomes.WHITE_BEACH.getInitialisedBiomeWrapper();
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.addSubSoilLayer(((Block) LOTRBlocks.GONDOR_ROCK.get()).defaultBlockState(), 8, 10);
	}

	public static class Hills extends DorEnErnilBiome {
		public Hills(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.7F).scale(0.7F).temperature(0.7F).downfall(0.9F), major);
		}

		@Override
		protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addStoneVariants(builder);
			LOTRBiomeFeatures.addDiorite(builder);
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.65F, TreeCluster.of(10, 10), dorEnErnilTrees());
			LOTRBiomeFeatures.addGrass(this, builder, 5, GrassBlends.STANDARD);
			LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
			LOTRBiomeFeatures.addPlainsFlowers(builder, 2);
			LOTRBiomeFeatures.addAthelasChance(builder);
			LOTRBiomeFeatures.addWildPipeweedChance(builder, 24);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return null;
		}
	}
}
