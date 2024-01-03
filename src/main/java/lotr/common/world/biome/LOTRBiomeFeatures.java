package lotr.common.world.biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import lotr.common.block.DripstoneBlock;
import lotr.common.compatibility.SnowRealMagicCompatibility;
import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import lotr.common.world.gen.carver.LOTRWorldCarvers;
import lotr.common.world.gen.feature.BoulderFeatureConfig;
import lotr.common.world.gen.feature.CobwebFeatureConfig;
import lotr.common.world.gen.feature.CraftingMonumentFeatureConfig;
import lotr.common.world.gen.feature.CrystalFeatureConfig;
import lotr.common.world.gen.feature.DripstoneFeatureConfig;
import lotr.common.world.gen.feature.FallenLogFeatureConfig;
import lotr.common.world.gen.feature.GrassPatchFeatureConfig;
import lotr.common.world.gen.feature.LOTRFeatures;
import lotr.common.world.gen.feature.LatitudeBasedFeatureConfig;
import lotr.common.world.gen.feature.MordorBasaltFeatureConfig;
import lotr.common.world.gen.feature.MordorMossFeatureConfig;
import lotr.common.world.gen.feature.ReedsFeatureConfig;
import lotr.common.world.gen.feature.TerrainSharpenFeatureConfig;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.WeightedFeature;
import lotr.common.world.gen.feature.WeightedRandomFeatureConfig;
import lotr.common.world.gen.feature.WrappedTreeFeatureConfig;
import lotr.common.world.gen.feature.grassblend.DoubleGrassBlend;
import lotr.common.world.gen.feature.grassblend.SingleGrassBlend;
import lotr.common.world.gen.placement.ByWaterConfig;
import lotr.common.world.gen.placement.TreeClustersConfig;
import lotr.common.world.gen.tree.AspenFoliagePlacer;
import lotr.common.world.gen.tree.BoughsFoliagePlacer;
import lotr.common.world.gen.tree.BoughsTrunkPlacer;
import lotr.common.world.gen.tree.CedarFoliagePlacer;
import lotr.common.world.gen.tree.CedarTrunkPlacer;
import lotr.common.world.gen.tree.ClusterFoliagePlacer;
import lotr.common.world.gen.tree.CulumaldaFoliagePlacer;
import lotr.common.world.gen.tree.CypressFoliagePlacer;
import lotr.common.world.gen.tree.DeadTrunkPlacer;
import lotr.common.world.gen.tree.DesertFoliagePlacer;
import lotr.common.world.gen.tree.DesertTrunkPlacer;
import lotr.common.world.gen.tree.EmptyFoliagePlacer;
import lotr.common.world.gen.tree.FangornTrunkPlacer;
import lotr.common.world.gen.tree.FirFoliagePlacer;
import lotr.common.world.gen.tree.HollyFoliagePlacer;
import lotr.common.world.gen.tree.LOTRFoliagePlacers;
import lotr.common.world.gen.tree.LOTRPineFoliagePlacer;
import lotr.common.world.gen.tree.LOTRTreeDecorators;
import lotr.common.world.gen.tree.LOTRTrunkPlacers;
import lotr.common.world.gen.tree.LairelosseFoliagePlacer;
import lotr.common.world.gen.tree.MirkOakFoliagePlacer;
import lotr.common.world.gen.tree.MirkOakLeavesGrowthDecorator;
import lotr.common.world.gen.tree.MirkOakTrunkPlacer;
import lotr.common.world.gen.tree.MirkOakWebsDecorator;
import lotr.common.world.gen.tree.PartyTrunkPlacer;
import lotr.common.world.gen.tree.PineBranchDecorator;
import lotr.common.world.gen.tree.PineStripDecorator;
import lotr.common.world.gen.tree.ShirePineFoliagePlacer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.gen.GenerationStage.Carving;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.blockplacer.ColumnBlockPlacer;
import net.minecraft.world.gen.blockplacer.DoublePlantBlockPlacer;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig.Builder;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateProvidingFeatureConfig;
import net.minecraft.world.gen.feature.BlockWithContextConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.FeatureSpreadConfig;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.Features.Placements;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LiquidsConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.SingleRandomFeature;
import net.minecraft.world.gen.feature.SphereReplaceConfig;
import net.minecraft.world.gen.feature.TwoLayerFeature;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.BushFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FancyFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.placement.AtSurfaceWithExtraConfig;
import net.minecraft.world.gen.placement.CaveEdgeConfig;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.placement.TopSolidWithNoiseConfig;
import net.minecraft.world.gen.treedecorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.trunkplacer.FancyTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRBiomeFeatures {
	public static RuleTest MORDOR_ROCK_FILLER;
	public static RuleTest SAND_FILLER;
	private static BlockState STONE;
	private static BlockState DIRT;
	private static BlockState COARSE_DIRT;
	private static BlockState GRAVEL;
	private static BlockState GRANITE;
	private static BlockState DIORITE;
	private static BlockState ANDESITE;
	private static BlockState PACKED_ICE;
	private static BlockState BLUE_ICE;
	private static BlockState GONDOR_ROCK;
	private static BlockState MORDOR_ROCK;
	private static BlockState ROHAN_ROCK;
	private static BlockState BLUE_ROCK;
	private static BlockState RED_ROCK;
	private static BlockState MORDOR_DIRT;
	private static BlockState MORDOR_GRAVEL;
	private static BlockState COAL_ORE;
	private static BlockState IRON_ORE;
	private static BlockState COPPER_ORE;
	private static BlockState TIN_ORE;
	private static BlockState GOLD_ORE;
	private static BlockState SILVER_ORE;
	private static BlockState SULFUR_ORE;
	private static BlockState NITER_ORE;
	private static BlockState SALT_ORE;
	private static BlockState LAPIS_ORE;
	private static BlockState MITHRIL_ORE;
	private static BlockState EDHELVIR_ORE;
	private static BlockState GLOWSTONE_ORE;
	private static BlockState DURNOR_ORE;
	private static BlockState MORGUL_IRON_ORE_MORDOR;
	private static BlockState MORGUL_IRON_ORE_STONE;
	private static BlockState GULDURIL_ORE_MORDOR;
	private static BlockState GULDURIL_ORE_STONE;
	private static BlockState EDHELVIR_CRYSTAL;
	private static BlockState GULDURIL_CRYSTAL;
	private static BlockState GLOWSTONE_CRYSTAL;
	private static BlockState COBWEB;
	private static BlockState WATER;
	private static BlockState LAVA;
	private static BlockState OAK_LOG;
	private static BlockState OAK_WOOD;
	private static BlockState OAK_STRIPPED_LOG;
	private static BlockState OAK_BRANCH;
	private static BlockState OAK_LEAVES;
	public static BaseTreeFeatureConfig OAK_TREE_VINES;
	public static BaseTreeFeatureConfig OAK_TREE_BEES_VINES;
	public static BaseTreeFeatureConfig OAK_TREE_TALL;
	public static BaseTreeFeatureConfig OAK_TREE_TALL_BEES;
	public static BaseTreeFeatureConfig OAK_TREE_TALL_VINES;
	public static BaseTreeFeatureConfig OAK_TREE_TALL_BEES_VINES;
	public static WrappedTreeFeatureConfig OAK_DESERT;
	public static WrappedTreeFeatureConfig OAK_DESERT_BEES;
	public static WrappedTreeFeatureConfig OAK_DEAD;
	public static BaseTreeFeatureConfig OAK_PARTY;
	public static BaseTreeFeatureConfig OAK_FANGORN;
	public static BaseTreeFeatureConfig OAK_SHRUB;
	private static BlockState SPRUCE_LOG;
	private static BlockState SPRUCE_WOOD;
	private static BlockState SPRUCE_BRANCH;
	private static BlockState SPRUCE_LEAVES;
	public static WrappedTreeFeatureConfig SPRUCE_DEAD;
	public static BaseTreeFeatureConfig SPRUCE_SHRUB;
	private static BlockState BIRCH_LOG;
	private static BlockState BIRCH_WOOD;
	private static BlockState BIRCH_BRANCH;
	private static BlockState BIRCH_LEAVES;
	public static BaseTreeFeatureConfig BIRCH_TREE_FANCY;
	public static BaseTreeFeatureConfig BIRCH_TREE_FANCY_BEES;
	public static BaseTreeFeatureConfig BIRCH_TREE_ALT;
	public static BaseTreeFeatureConfig BIRCH_TREE_ALT_BEES;
	public static WrappedTreeFeatureConfig BIRCH_DEAD;
	public static BaseTreeFeatureConfig BIRCH_PARTY;
	private static BlockState DARK_OAK_LOG;
	private static BlockState DARK_OAK_WOOD;
	private static BlockState DARK_OAK_BRANCH;
	private static BlockState DARK_OAK_LEAVES;
	public static BaseTreeFeatureConfig DARK_OAK_PARTY;
	public static BaseTreeFeatureConfig DARK_OAK_SHRUB;
	private static BlockState PINE_LOG;
	private static BlockState PINE_LOG_SLAB;
	private static BlockState PINE_LEAVES;
	private static IPlantable PINE_SAPLING;
	public static BaseTreeFeatureConfig PINE_TREE;
	public static BaseTreeFeatureConfig SHIRE_PINE_TREE;
	public static BaseTreeFeatureConfig PINE_DEAD;
	public static BaseTreeFeatureConfig PINE_SHRUB;
	private static BlockState MALLORN_LOG;
	private static BlockState MALLORN_WOOD;
	private static BlockState MALLORN_BRANCH;
	private static BlockState MALLORN_LEAVES;
	private static IPlantable MALLORN_SAPLING;
	public static BaseTreeFeatureConfig MALLORN_TREE;
	public static BaseTreeFeatureConfig MALLORN_TREE_BEES;
	public static BaseTreeFeatureConfig MALLORN_TREE_BOUGHS;
	public static BaseTreeFeatureConfig MALLORN_PARTY;
	private static BlockState MIRK_OAK_LOG;
	private static BlockState MIRK_OAK_WOOD;
	private static BlockState MIRK_OAK_BRANCH;
	private static BlockState MIRK_OAK_LEAVES;
	private static IPlantable MIRK_OAK_SAPLING;
	public static BaseTreeFeatureConfig MIRK_OAK_TREE;
	public static BaseTreeFeatureConfig MIRK_OAK_PARTY;
	public static BaseTreeFeatureConfig MIRK_OAK_SHRUB;
	private static BlockState CHARRED_LOG;
	private static BlockState CHARRED_WOOD;
	private static BlockState CHARRED_BRANCH;
	public static WrappedTreeFeatureConfig CHARRED_TREE;
	private static BlockState APPLE_LOG;
	private static BlockState APPLE_LEAVES;
	private static BlockState APPLE_LEAVES_RED;
	private static BlockState APPLE_LEAVES_GREEN;
	private static BlockStateProvider APPLE_LEAVES_RED_POOL;
	private static BlockStateProvider APPLE_LEAVES_GREEN_POOL;
	private static BlockStateProvider APPLE_LEAVES_MIX_POOL;
	private static IPlantable APPLE_SAPLING;
	public static BaseTreeFeatureConfig APPLE_TREE_RED;
	public static BaseTreeFeatureConfig APPLE_TREE_RED_BEES;
	public static BaseTreeFeatureConfig APPLE_TREE_GREEN;
	public static BaseTreeFeatureConfig APPLE_TREE_GREEN_BEES;
	public static BaseTreeFeatureConfig APPLE_TREE_MIX;
	public static BaseTreeFeatureConfig APPLE_TREE_MIX_BEES;
	private static BlockState PEAR_LOG;
	private static BlockState PEAR_LEAVES;
	private static BlockState PEAR_LEAVES_FRUIT;
	private static BlockStateProvider PEAR_LEAVES_POOL;
	private static IPlantable PEAR_SAPLING;
	public static BaseTreeFeatureConfig PEAR_TREE;
	public static BaseTreeFeatureConfig PEAR_TREE_BEES;
	private static BlockState CHERRY_LOG;
	private static BlockState CHERRY_LEAVES;
	private static BlockState CHERRY_LEAVES_FRUIT;
	private static BlockStateProvider CHERRY_LEAVES_POOL;
	private static IPlantable CHERRY_SAPLING;
	public static BaseTreeFeatureConfig CHERRY_TREE;
	public static BaseTreeFeatureConfig CHERRY_TREE_BEES;
	private static BlockState LEBETHRON_LOG;
	private static BlockState LEBETHRON_WOOD;
	private static BlockState LEBETHRON_BRANCH;
	private static BlockState LEBETHRON_LEAVES;
	private static IPlantable LEBETHRON_SAPLING;
	public static BaseTreeFeatureConfig LEBETHRON_TREE;
	public static BaseTreeFeatureConfig LEBETHRON_TREE_BEES;
	public static BaseTreeFeatureConfig LEBETHRON_TREE_FANCY;
	public static BaseTreeFeatureConfig LEBETHRON_TREE_FANCY_BEES;
	public static BaseTreeFeatureConfig LEBETHRON_PARTY;
	private static BlockState BEECH_LOG;
	private static BlockState BEECH_WOOD;
	private static BlockState BEECH_STRIPPED_LOG;
	private static BlockState BEECH_BRANCH;
	private static BlockState BEECH_LEAVES;
	private static IPlantable BEECH_SAPLING;
	public static BaseTreeFeatureConfig BEECH_TREE;
	public static BaseTreeFeatureConfig BEECH_TREE_BEES;
	public static BaseTreeFeatureConfig BEECH_TREE_FANCY;
	public static BaseTreeFeatureConfig BEECH_TREE_FANCY_BEES;
	public static BaseTreeFeatureConfig BEECH_PARTY;
	public static BaseTreeFeatureConfig BEECH_FANGORN;
	public static WrappedTreeFeatureConfig BEECH_DEAD;
	private static BlockState MAPLE_LOG;
	private static BlockState MAPLE_WOOD;
	private static BlockState MAPLE_BRANCH;
	private static BlockState MAPLE_LEAVES;
	private static IPlantable MAPLE_SAPLING;
	public static BaseTreeFeatureConfig MAPLE_TREE;
	public static BaseTreeFeatureConfig MAPLE_TREE_BEES;
	public static BaseTreeFeatureConfig MAPLE_TREE_FANCY;
	public static BaseTreeFeatureConfig MAPLE_TREE_FANCY_BEES;
	public static BaseTreeFeatureConfig MAPLE_PARTY;
	private static BlockState ASPEN_LOG;
	private static BlockState ASPEN_LEAVES;
	private static IPlantable ASPEN_SAPLING;
	public static BaseTreeFeatureConfig ASPEN_TREE;
	private static BlockState LAIRELOSSE_LOG;
	private static BlockState LAIRELOSSE_LEAVES;
	private static IPlantable LAIRELOSSE_SAPLING;
	public static BaseTreeFeatureConfig LAIRELOSSE_TREE;
	private static BlockState CEDAR_LOG;
	private static BlockState CEDAR_WOOD;
	private static BlockState CEDAR_BRANCH;
	private static BlockState CEDAR_LEAVES;
	private static IPlantable CEDAR_SAPLING;
	public static BaseTreeFeatureConfig CEDAR_TREE;
	public static BaseTreeFeatureConfig CEDAR_TREE_LARGE;
	private static BlockState FIR_LOG;
	private static BlockState FIR_LEAVES;
	private static IPlantable FIR_SAPLING;
	public static BaseTreeFeatureConfig FIR_TREE;
	public static BaseTreeFeatureConfig FIR_SHRUB;
	private static BlockState LARCH_LOG;
	private static BlockState LARCH_LEAVES;
	private static IPlantable LARCH_SAPLING;
	public static BaseTreeFeatureConfig LARCH_TREE;
	private static BlockState HOLLY_LOG;
	private static BlockState HOLLY_LEAVES;
	private static IPlantable HOLLY_SAPLING;
	public static BaseTreeFeatureConfig HOLLY_TREE;
	public static BaseTreeFeatureConfig HOLLY_TREE_BEES;
	private static BlockState GREEN_OAK_LOG;
	private static BlockState GREEN_OAK_WOOD;
	private static BlockState GREEN_OAK_BRANCH;
	private static BlockState GREEN_OAK_LEAVES;
	private static IPlantable GREEN_OAK_SAPLING;
	private static BlockState RED_OAK_LEAVES;
	private static IPlantable RED_OAK_SAPLING;
	public static BaseTreeFeatureConfig GREEN_OAK_TREE;
	public static BaseTreeFeatureConfig GREEN_OAK_TREE_BEES;
	public static BaseTreeFeatureConfig RED_OAK_TREE;
	public static BaseTreeFeatureConfig RED_OAK_TREE_BEES;
	public static BaseTreeFeatureConfig GREEN_OAK_PARTY;
	public static BaseTreeFeatureConfig RED_OAK_PARTY;
	public static BaseTreeFeatureConfig GREEN_OAK_SHRUB;
	private static BlockState CYPRESS_LOG;
	private static BlockState CYPRESS_LEAVES;
	private static IPlantable CYPRESS_SAPLING;
	public static BaseTreeFeatureConfig CYPRESS_TREE;
	private static BlockState CULUMALDA_LOG;
	private static BlockState CULUMALDA_LEAVES;
	private static IPlantable CULUMALDA_SAPLING;
	public static BaseTreeFeatureConfig CULUMALDA_TREE;
	public static BaseTreeFeatureConfig CULUMALDA_TREE_BEES;
	private static BlockState SIMBELMYNE;
	private static BlockState ATHELAS;
	private static BlockState WILD_PIPEWEED;
	public static BlockClusterFeatureConfig SIMBELMYNE_CONFIG;
	public static BlockClusterFeatureConfig ATHELAS_CONFIG;
	public static BlockClusterFeatureConfig WILD_PIPEWEED_CONFIG;
	private static BlockState LILAC;
	private static BlockState ROSE_BUSH;
	private static BlockState PEONY;
	private static BlockState SUNFLOWER;
	private static BlockState HIBISCUS;
	private static BlockState FLAME_OF_HARAD;
	public static BlockClusterFeatureConfig LILAC_CONFIG;
	public static BlockClusterFeatureConfig ROSE_BUSH_CONFIG;
	public static BlockClusterFeatureConfig PEONY_CONFIG;
	public static BlockClusterFeatureConfig SUNFLOWER_CONFIG;
	public static BlockClusterFeatureConfig HIBISCUS_CONFIG;
	public static BlockClusterFeatureConfig FLAME_OF_HARAD_CONFIG;
	private static BlockState DEAD_BUSH;
	private static BlockClusterFeatureConfig DEAD_BUSH_CONFIG;
	private static BlockState CACTUS;
	private static BlockClusterFeatureConfig CACTUS_CONFIG;
	private static BlockState SAND;
	private static BlockState RED_SAND;
	private static BlockState WHITE_SAND;
	private static BlockState CLAY;
	private static BlockState QUAGMIRE;
	private static BlockState GRASS_BLOCK;
	private static BlockState BROWN_MUSHROOM;
	private static BlockState RED_MUSHROOM;
	private static BlockState MIRK_SHROOM;
	private static BlockClusterFeatureConfig BROWN_MUSHROOM_CONFIG;
	private static BlockClusterFeatureConfig RED_MUSHROOM_CONFIG;
	private static BlockClusterFeatureConfig MIRK_SHROOM_CONFIG;
	private static BlockState SUGAR_CANE;
	private static BlockClusterFeatureConfig SUGAR_CANE_CONFIG;
	private static BlockState REEDS;
	private static BlockState DRIED_REEDS;
	private static Function<Float, ReedsFeatureConfig> REEDS_CONFIG_FOR_DRIED_CHANCE;
	private static BlockState PAPYRUS;
	private static ReedsFeatureConfig PAPYRUS_CONFIG;
	private static BlockState RUSHES;
	public static BlockClusterFeatureConfig RUSHES_CONFIG;
	private static BlockState PUMPKIN;
	public static BlockClusterFeatureConfig PUMPKIN_PATCH_CONFIG;
	private static BlockState LILY_PAD;
	private static BlockClusterFeatureConfig LILY_PAD_CONFIG;
	private static BlockState WHITE_WATER_LILY;
	private static BlockState YELLOW_WATER_LILY;
	private static BlockState PURPLE_WATER_LILY;
	private static BlockState PINK_WATER_LILY;
	private static BlockClusterFeatureConfig LILY_PAD_WITH_FLOWERS_CONFIG;
	private static BlockClusterFeatureConfig LILY_PAD_WITH_RARE_FLOWERS_CONFIG;
	private static BlockState SPONGE;
	private static BlockClusterFeatureConfig SPONGE_CONFIG;
	public static BlockState SWEET_BERRY_BUSH;
	public static BlockClusterFeatureConfig SWEET_BERRY_BUSH_CONFIG;
	private static BlockState MORDOR_MOSS;
	public static MordorMossFeatureConfig MORDOR_MOSS_CONFIG;
	private static BlockState MORDOR_GRASS;
	public static BlockClusterFeatureConfig MORDOR_GRASS_CONFIG;
	private static BlockState MORDOR_THORN;
	public static BlockClusterFeatureConfig MORDOR_THORN_CONFIG;
	private static BlockState MORGUL_SHROOM;
	public static BlockClusterFeatureConfig MORGUL_SHROOM_CONFIG;
	private static BlockState MORGUL_FLOWER;
	public static BlockClusterFeatureConfig MORGUL_FLOWER_CONFIG;
	public static LiquidsConfig WATER_SPRING_CONFIG;
	public static LiquidsConfig LAVA_SPRING_CONFIG;

	public static ConfiguredFeature<?, ?> acacia() {
		return Features.ACACIA;
	}

	public static void addAndesite(BiomeGenerationSettings.Builder builder) {
		addStoneVariety(builder, ANDESITE, 10, 80);
	}

	public static void addAthelasChance(BiomeGenerationSettings.Builder builder) {
		addAthelasChance(builder, 30);
	}

	public static void addAthelasChance(BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(ATHELAS_CONFIG).decorated(placeHeightmapDoubleChance(chance)));
	}

	public static void addBlueIcePatches(BiomeGenerationSettings.Builder builder) {
		ConfiguredFeature<?, ?> blueIce = Feature.BLUE_ICE.configured(IFeatureConfig.NONE);
		int blueIceFreq = 19;
		ConfiguredPlacement<?> blueIcePlacement = (Placement.RANGE.configured(new TopSolidRangeConfig(30, 32, 64)).squared()).countRandom(blueIceFreq);
		addLatitudeBased(builder, Decoration.SURFACE_STRUCTURES, blueIce, blueIcePlacement, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.ICE));
	}

	public static void addBlueRockPatches(BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, BLUE_ROCK, 61)).range(96)).squared()).count(6));
	}

	public static void addBorealFlowers(BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] borealFlowers = { Blocks.POPPY, 10, Blocks.DANDELION, 20, Blocks.BLUE_ORCHID, 10, LOTRBlocks.BLUEBELL.get(), 5 };
		addFlowers(builder, freq, LOTRUtil.combineVarargs(borealFlowers, extraFlowers));
	}

	public static void addBoulders(BiomeGenerationSettings.Builder builder, BlockState block, int minWidth, int maxWidth, int chanceInChunk, int genAmount) {
		int heightCheck = 3;
		addBoulders(builder, block, minWidth, maxWidth, chanceInChunk, genAmount, heightCheck);
	}

	public static void addBoulders(BiomeGenerationSettings.Builder builder, BlockState block, int minWidth, int maxWidth, int chanceInChunk, int genAmount, int heightCheck) {
		BlockStateProvider blockProv = new SimpleBlockStateProvider(block);
		BoulderFeatureConfig config = new BoulderFeatureConfig(blockProv, minWidth, maxWidth, heightCheck);
		int baseCount = 0;
		float increaseChance = 1.0F / chanceInChunk;
		ConfiguredPlacement<?> placement = (Placement.HEIGHTMAP.configured(NoPlacementConfig.INSTANCE).squared()).decorated(Placement.COUNT_EXTRA.configured(new AtSurfaceWithExtraConfig(baseCount, increaseChance, genAmount)));
		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, LOTRFeatures.BOULDER.configured(config).decorated(placement));
	}

	public static void addCactiAtSurfaceChance(BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(CACTUS_CONFIG).decorated(placeHeightmapChance(chance)));
	}

	public static void addCactiFreq(BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(CACTUS_CONFIG).decorated(placeHeightmapFreq(freq)));
	}

	public static void addCarvers(BiomeGenerationSettings.Builder builder) {
		float caveChance = 0.14285715F;
		float canyonChance = 0.02F;
		addCarvers(builder, caveChance, canyonChance);
	}

	public static void addCarvers(BiomeGenerationSettings.Builder builder, float caveChance, float canyonChance) {
		builder.addCarver(Carving.AIR, LOTRWorldCarvers.CAVE.configured(new ProbabilityConfig(caveChance)));
		builder.addCarver(Carving.AIR, LOTRWorldCarvers.CANYON.configured(new ProbabilityConfig(canyonChance)));
	}

	public static void addCarversExtraCanyons(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		float caveChance = 0.14285715F;
		float canyonChance = 0.2F;
		addCarvers(builder, caveChance, canyonChance);
	}

	public static void addClayGravelSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(CLAY, FeatureSpread.of(2, 1), 1, Lists.newArrayList(DIRT, COARSE_DIRT, CLAY))).decorated(placeTopSolidFreq(1)));
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(GRAVEL, FeatureSpread.of(2, 3), 2, Lists.newArrayList(DIRT, COARSE_DIRT, GRASS_BLOCK))).decorated(placeTopSolidFreq(1)));
	}

	public static void addCobwebs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addCobwebs(builder, 2, COBWEB);
	}

	public static void addCobwebs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int invChance, BlockState webBlock) {
		CobwebFeatureConfig config = new CobwebFeatureConfig(new SimpleBlockStateProvider(webBlock), 64, 6, 4, 6);
		builder.addFeature(Decoration.UNDERGROUND_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) LOTRFeatures.COBWEBS.configured(config).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 60))).squared()).chance(invChance));
	}

	public static void addCommonGranite(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addStoneVariety(builder, GRANITE, 12, 96);
	}

	public static void addCoral(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		ConfiguredFeature<?, ?> coralTree = Feature.CORAL_TREE.configured(IFeatureConfig.NONE);
		ConfiguredFeature<?, ?> coralClaw = Feature.CORAL_CLAW.configured(IFeatureConfig.NONE);
		ConfiguredFeature<?, ?> coralShroom = Feature.CORAL_MUSHROOM.configured(IFeatureConfig.NONE);
		ConfiguredFeature<?, ?> coralRandomiser = Feature.SIMPLE_RANDOM_SELECTOR.configured(new SingleRandomFeature(ImmutableList.of(() -> coralTree, () -> coralClaw, () -> coralShroom)));
		int noiseToCount = 10;
		double noiseFactor = 400.0D;
		ConfiguredPlacement<?> placement = (Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE).squared()).decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(noiseToCount, noiseFactor, 0.0D)));
		addLatitudeBased(builder, Decoration.VEGETAL_DECORATION, coralRandomiser, placement, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.CORAL));
	}

	public static void addCraftingMonument(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, BlockState table, BlockStateProvider baseProvider, BlockStateProvider postProvider, BlockStateProvider torchProvider) {
		addCraftingMonument(builder, 1, table, baseProvider, postProvider, torchProvider);
	}

	public static void addCraftingMonument(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int relativeChance, BlockState table, BlockStateProvider baseProvider, BlockStateProvider postProvider, BlockStateProvider torchProvider) {
		CraftingMonumentFeatureConfig config = new CraftingMonumentFeatureConfig(table, baseProvider, postProvider, torchProvider);
		int chance = 512 * relativeChance;
		ConfiguredPlacement<?> placement = placeHeightmapChance(chance);
		builder.addFeature(Decoration.SURFACE_STRUCTURES, LOTRFeatures.CRAFTING_MONUMENT.configured(config).decorated(placement));
	}

	public static void addDeadBushAtSurfaceChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEAD_BUSH_CONFIG).decorated(placeHeightmapChance(chance)));
	}

	public static void addDeadBushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(DEAD_BUSH_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addDeepDiorite(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addStoneVariety(builder, DIORITE, 5, 32);
	}

	public static void addDefaultDoubleFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] defaultFlowers = { Blocks.LILAC, 10, Blocks.ROSE_BUSH, 10, Blocks.PEONY, 10 };
		addDoubleFlowers(builder, freq, LOTRUtil.combineVarargs(defaultFlowers, extraFlowers));
	}

	public static void addDefaultFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] defaultFlowers = { Blocks.POPPY, 1, Blocks.DANDELION, 2 };
		addFlowers(builder, freq, LOTRUtil.combineVarargs(defaultFlowers, extraFlowers));
	}

	public static void addDiorite(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addStoneVariety(builder, DIORITE, 5, 80);
	}

	public static void addDirtGravel(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, DIRT, 33)).range(256)).squared()).count(10));
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, GRAVEL, 33)).range(256)).squared()).count(8));
	}

	public static void addDoubleFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... weightedFlowers) {
		try {
			List<WeightedFeature<?>> weightedDoubleFlowerFeatures = new ArrayList<>();

			for (int i = 0; i < weightedFlowers.length; i += 2) {
				Object obj1 = weightedFlowers[i];
				BlockState state;
				if (obj1 instanceof BlockState) {
					state = (BlockState) obj1;
				} else {
					state = ((Block) obj1).defaultBlockState();
				}

				int weight = (Integer) weightedFlowers[i + 1];
				net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder flowerConfigBuilder = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(state), new DoublePlantBlockPlacer()).tries(64).noProjection();
				if (state.getBlock() instanceof IWaterLoggable) {
					flowerConfigBuilder.canReplace();
				}

				weightedDoubleFlowerFeatures.add(WeightedFeature.make((Supplier<ConfiguredFeature<?, ?>>) Feature.RANDOM_PATCH.configured(flowerConfigBuilder.build()), weight));
			}

			ConfiguredFeature<?,?> doubleFlowerFeature = LOTRFeatures.WEIGHTED_RANDOM.configured(new WeightedRandomFeatureConfig(weightedDoubleFlowerFeatures));
			builder.addFeature(Decoration.VEGETAL_DECORATION, doubleFlowerFeature.decorated(placeFlowers()).count(freq));
		} catch (ArrayIndexOutOfBoundsException | ClassCastException var9) {
			throw new IllegalArgumentException("Error adding biome double flowers! A list of (blockstate1, weight1), (blockstate2, weight2)... is required", var9);
		}
	}

	public static void addDoubleGrass(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, DoubleGrassBlend blend) {
		WeightedRandomFeatureConfig wrGrassConfig = blend.getFeatureConfig();
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) LOTRFeatures.getWeightedRandom().configured(wrGrassConfig).decorated(placeFlowers()).count(freq));
	}

	public static void addDriftwood(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		ConfiguredPlacement<?> logPlacement = placeTopSolidChance(chance);
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.FALLEN_LOG.configured(new FallenLogFeatureConfig(true, true)).decorated(logPlacement));
	}

	public static void addDripstones(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addDripstones(builder, (DripstoneBlock) null);
	}

	public static void addDripstones(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, DripstoneBlock block) {
		addDripstones(builder, (DripstoneBlock) null, 3);
	}

	public static void addDripstones(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, DripstoneBlock block, int freq) {
		DripstoneFeatureConfig config = new DripstoneFeatureConfig(block == null ? null : block.defaultBlockState(), 64, 8, 4, 8, 0.33F);
		builder.addFeature(Decoration.UNDERGROUND_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) LOTRFeatures.DRIPSTONE.configured(config).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 60))).squared()).count(freq));
	}

	public static void addEdhelvirOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		int oreFreq = 4;
		int crystalFreq = 2;
		ConfiguredPlacement<?> orePlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 48));
		ConfiguredPlacement<?> crystalPlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 48));
		OreFeatureConfig oreConfig = new OreFeatureConfig(FillerBlockType.NATURAL_STONE, EDHELVIR_ORE, 7);
		CrystalFeatureConfig crystalConfig = new CrystalFeatureConfig(new SimpleBlockStateProvider(EDHELVIR_CRYSTAL), 64, 6, 4, 6);
		builder.addFeature(Decoration.UNDERGROUND_ORES, (Feature.ORE.configured(oreConfig).decorated(orePlacement).squared()).count(oreFreq));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature)((ConfiguredFeature)LOTRFeatures.CRYSTALS.configured(crystalConfig).decorated(crystalPlacement).squared()).count(crystalFreq));
	}

	public static void addExtraCoal(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int size, int freq, int height) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, COAL_ORE, size + 1)).range(height)).squared()).count(freq));
	}

	public static void addExtraGold(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int size, int freq, int height) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, GOLD_ORE, size + 1)).range(height)).squared()).count(freq));
	}

	public static void addExtraIron(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int size, int freq, int height) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, COAL_ORE, size + 1)).range(height)).squared()).count(freq));
	}

	public static void addExtraMordorGulduril(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		int extraOreFreq = 10;
		int extraCrystalFreq = 1;
		addGuldurilOre(builder, true, extraOreFreq, extraCrystalFreq, 60);
	}

	public static void addExtraMorgulFlowersByWater(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(MORGUL_FLOWER_CONFIG).decorated(LOTRFeatures.BY_WATER.configured(new ByWaterConfig(8, 20)).decorated(placeHeightmapFreq(freq))));
	}

	public static void addExtraSalt(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int size, int freq, int height) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, SALT_ORE, size + 1)).range(height)).squared()).count(freq));
	}

	public static void addExtraSilver(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int size, int freq, int height) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, SILVER_ORE, size + 1)).range(height)).squared()).count(freq));
	}

	public static void addExtraUnderwaterSeagrass(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.SIMPLE_BLOCK.configured(new BlockWithContextConfig(Blocks.SEAGRASS.defaultBlockState(), ImmutableList.of(STONE), ImmutableList.of(WATER), ImmutableList.of(WATER))).decorated(Placement.CARVING_MASK.configured(new CaveEdgeConfig(Carving.LIQUID, 0.1F))));
	}

	private static void addFallenLeavesWithPlacement(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, ConfiguredPlacement placement, LatitudeBasedFeatureConfig.LatitudeConfiguration latConfig) {
		ConfiguredFeature fallenLeavesFeature = LOTRFeatures.FALLEN_LEAVES.configured(IFeatureConfig.NONE);
		if (latConfig == null) {
			builder.addFeature(Decoration.VEGETAL_DECORATION, fallenLeavesFeature.decorated(placement));
		} else {
			addLatitudeBased(builder, Decoration.VEGETAL_DECORATION, fallenLeavesFeature, placement, latConfig);
		}

	}

	public static void addFallenLogs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		ConfiguredPlacement logPlacement = placeTopSolidFreq(freq);
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.FALLEN_LOG.configured(new FallenLogFeatureConfig(false, false)).decorated(logPlacement));
	}

	public static void addFallenLogsBelowTreeline(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, int treeline) {
		ConfiguredPlacement logPlacement = LOTRFeatures.TREE_CLUSTERS.configured(TreeClustersConfig.builder().count(freq).layerLimit(treeline, true).build());
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.FALLEN_LOG.configured(new FallenLogFeatureConfig(false, false)).decorated(logPlacement));
	}

	public static void addFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... weightedFlowers) {
		try {
			WeightedBlockStateProvider stateProvider = new WeightedBlockStateProvider();

			for (int i = 0; i < weightedFlowers.length; i += 2) {
				Object obj1 = weightedFlowers[i];
				BlockState state;
				if (obj1 instanceof BlockState) {
					state = (BlockState) obj1;
				} else {
					state = ((Block) obj1).defaultBlockState();
				}

				int weight = (Integer) weightedFlowers[i + 1];
				stateProvider.add(state, weight);
			}

			BlockClusterFeatureConfig flowerConfig = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(stateProvider, new SimpleBlockPlacer()).tries(64).build();
			builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(flowerConfig).decorated(placeFlowers()).count(freq));
		} catch (ArrayIndexOutOfBoundsException | ClassCastException var8) {
			throw new IllegalArgumentException("Error adding biome flowers! A list of (blockstate1, weight1), (blockstate2, weight2)... is required", var8);
		}
	}

	public static void addForestFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] forestFlowers = { Blocks.POPPY, 10, Blocks.DANDELION, 20, Blocks.LILY_OF_THE_VALLEY, 2, LOTRBlocks.BLUEBELL.get(), 5, LOTRBlocks.MARIGOLD.get(), 10 };
		addFlowers(builder, freq, LOTRUtil.combineVarargs(forestFlowers, extraFlowers));
	}

	public static void addFoxBerryBushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addFoxBerryBushesChance(builder, 4);
	}

	public static void addFoxBerryBushesChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SWEET_BERRY_BUSH_CONFIG).decorated(placeHeightmapDoubleChance(chance)));
	}

	public static void addFreezeTopLayer(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		Feature<NoFeatureConfig> freezeTopLayer = SnowRealMagicCompatibility.getFreezeTopLayerFeature();
		builder.addFeature(Decoration.TOP_LAYER_MODIFICATION, freezeTopLayer.configured(IFeatureConfig.NONE).decorated(Placement.NOPE.configured(IPlacementConfig.NONE)));
	}

	public static void addGlowstoneOre(BiomeGenerationSettings.Builder builder) {
		int oreFreq = 6;
		int crystalFreq = 2;
		ConfiguredPlacement<?> orePlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 48));
		ConfiguredPlacement<?> crystalPlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 48));
		OreFeatureConfig oreConfig = new OreFeatureConfig(FillerBlockType.NATURAL_STONE, GLOWSTONE_ORE, 5);
		CrystalFeatureConfig crystalConfig = new CrystalFeatureConfig(new SimpleBlockStateProvider(GLOWSTONE_CRYSTAL), 64, 6, 4, 6);
		builder.addFeature(Decoration.UNDERGROUND_ORES, (Feature.ORE.configured(oreConfig).decorated(orePlacement).squared()).count(oreFreq));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature)((ConfiguredFeature)LOTRFeatures.CRYSTALS.configured(crystalConfig).decorated(crystalPlacement).squared()).count(crystalFreq));
	}

	public static void addGondorRockPatches(BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, ((Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, GONDOR_ROCK, 61)).range(80)).squared()).count(4));
	}

	public static void addGranite(BiomeGenerationSettings.Builder builder) {
		addStoneVariety(builder, GRANITE, 5, 80);
	}

	public static void addGrass(LOTRBiomeBase biome, BiomeGenerationSettings.Builder builder, int freq, SingleGrassBlend blend) {
		WeightedRandomFeatureConfig<IFeatureConfig> wrGrassConfig = blend.getFeatureConfig();
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.getWeightedRandom().configured(wrGrassConfig).decorated(placeHeightmapDoubleFreq(freq)));
		biome.setGrassBonemealGenerator(wrGrassConfig);
	}

	public static void addGrassPatches(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, List<BlockState> targets, int rMin, int rMax, int depthMin, int depthMax, int freq) {
		GrassPatchFeatureConfig config = new GrassPatchFeatureConfig(targets, rMin, rMax, depthMin, depthMax);
		ConfiguredPlacement<?> placement = placeHeightmapFreq(freq);
		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, LOTRFeatures.GRASS_PATCH.configured(config).decorated(placement));
	}


	public static void addGuldurilOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, boolean mordor) {
		int oreFreq = 4;
		int crystalFreq = 2;
		addGuldurilOre(builder, mordor, oreFreq, crystalFreq, 32);
	}

	private static void addGuldurilOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, boolean mordor, int oreFreq, int crystalFreq, int topY) {
		ConfiguredPlacement<?> orePlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, topY));
		ConfiguredPlacement<?> crystalPlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, topY));
		OreFeatureConfig oreConfig = mordor ? new OreFeatureConfig(MORDOR_ROCK_FILLER, GULDURIL_ORE_MORDOR, 9) : new OreFeatureConfig(FillerBlockType.NATURAL_STONE, GULDURIL_ORE_STONE, 9);
		CrystalFeatureConfig crystalConfig = new CrystalFeatureConfig(new SimpleBlockStateProvider(GULDURIL_CRYSTAL), 64, 6, 4, 6);
		builder.addFeature(Decoration.UNDERGROUND_ORES, (Feature.ORE.configured(oreConfig).decorated(orePlacement).squared()).count(oreFreq));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) LOTRFeatures.CRYSTALS.configured(crystalConfig).decorated(crystalPlacement).squared()).count(crystalFreq));
	}

	public static void addHaradDoubleFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] haradFlowers = { LOTRBlocks.HIBISCUS.get(), 10, LOTRBlocks.FLAME_OF_HARAD.get(), 2 };
		addDoubleFlowers(builder, freq, LOTRUtil.combineVarargs(haradFlowers, extraFlowers));
	}

	public static void addHaradFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] haradFlowers = { LOTRBlocks.RED_SAND_GEM.get(), 5, LOTRBlocks.YELLOW_SAND_GEM.get(), 10, LOTRBlocks.HARAD_DAISY.get(), 5, LOTRBlocks.SOUTHBELL.get(), 5 };
		addFlowers(builder, freq, LOTRUtil.combineVarargs(haradFlowers, extraFlowers));
	}

	public static void addIcebergs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		ConfiguredFeature<?,?> iceberg = Feature.ICEBERG.configured(new BlockStateFeatureConfig(PACKED_ICE));
		ConfiguredFeature<?,?> blueIceberg = Feature.ICEBERG.configured(new BlockStateFeatureConfig(BLUE_ICE));
		int icebergChance = 16;
		int blueIcebergChance = 200;
		ConfiguredPlacement<?> icebergPlacement = Placement.ICEBERG.configured(NoPlacementConfig.INSTANCE).chance(icebergChance);
		ConfiguredPlacement<?> blueIcebergPlacement = Placement.ICEBERG.configured(NoPlacementConfig.INSTANCE).chance(blueIcebergChance);
		addLatitudeBased(builder, Decoration.LOCAL_MODIFICATIONS, iceberg, icebergPlacement, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.ICE));
		addLatitudeBased(builder, Decoration.LOCAL_MODIFICATIONS, blueIceberg, blueIcebergPlacement, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.ICE));
	}

	public static void addKelp(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, ((ConfiguredFeature) Feature.KELP.configured(IFeatureConfig.NONE).decorated(Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE)).squared()).decorated(Placement.COUNT_NOISE_BIASED.configured(new TopSolidWithNoiseConfig(80, 80.0D, 0.0D))));
	}

	public static void addLakes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		int waterChance = 4;
		waterChance = waterChance * 2;
		int lavaChance = 8;
		lavaChance = lavaChance * 2;
		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, (Feature.LAKE.configured(new BlockStateFeatureConfig(WATER)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 62))).squared()).chance(waterChance));
		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, (Feature.LAKE.configured(new BlockStateFeatureConfig(LAVA)).decorated(Placement.RANGE_BIASED.configured(new TopSolidRangeConfig(8, 8, 62))).squared()).chance(lavaChance));
	}

	public static void addLapisOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, LAPIS_ORE, 7)).decorated(Placement.DEPTH_AVERAGE.configured(new DepthAverageConfig(16, 16))).squared());
	}

	public static void addLatitudeBased(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, Decoration genStage, ConfiguredFeature feature, ConfiguredPlacement placement, LatitudeBasedFeatureConfig.LatitudeConfiguration latConfig) {
		LatitudeBasedFeatureConfig latFeatureConfig = new LatitudeBasedFeatureConfig(feature, latConfig);
		builder.addFeature(genStage, LOTRFeatures.LATITUDE_BASED.configured(latFeatureConfig).decorated(placement));
	}

	public static void addLavaSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addLavaSprings(builder, 20);
	}

	public static void addLavaSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) Feature.SPRING.configured(LAVA_SPRING_CONFIG).decorated(Placement.RANGE_VERY_BIASED.configured(new TopSolidRangeConfig(8, 16, 256))).squared()).count(freq));
	}

	private static void addLeafBushesWithPlacement(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, ConfiguredPlacement placement, LatitudeBasedFeatureConfig.LatitudeConfiguration latConfig) {
		ConfiguredFeature<?,?> bushesFeature = LOTRFeatures.LEAF_BUSHES.configured(IFeatureConfig.NONE);
		if (latConfig == null) {
			builder.addFeature(Decoration.VEGETAL_DECORATION, bushesFeature.decorated(placement));
		} else {
			addLatitudeBased(builder, Decoration.VEGETAL_DECORATION, bushesFeature, placement, latConfig);
		}

	}

	public static void addLessCommonReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addReedsWithFreqAndDriedChance(builder, 4, 0.1F);
	}

	public static void addMirkShroomsFreq(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(MIRK_SHROOM_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addMithrilOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int invChance) {
		ConfiguredPlacement orePlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 16));
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, MITHRIL_ORE, 7)).decorated(orePlacement).chance(invChance));
	}

	public static void addDiamondOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int invChance) {
		ConfiguredPlacement orePlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 16));
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, MITHRIL_ORE, 60)).decorated(orePlacement).chance(invChance));
	}


	public static void addMordorBasalt(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int flatChance, int spikyChance) {
		List<Block> surfaceBlocks = ImmutableList.of();
		Set<BlockState> surfaceBlockStates = surfaceBlocks.stream().map(Block::defaultBlockState).collect(Collectors.toSet());

		MordorBasaltFeatureConfig spikyConfig = new MordorBasaltFeatureConfig(surfaceBlockStates, FeatureSpread.of(3, 9), FeatureSpread.of(1, 2), 0.6F, 0.95F, 0.15F, 0.35F, 0.2F);
		MordorBasaltFeatureConfig flatConfig = new MordorBasaltFeatureConfig(surfaceBlockStates, FeatureSpread.of(3, 9), FeatureSpread.of(1, 2), 0.3F, 0.95F, 0.0F, 0.1F, 0.0F);

		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, LOTRFeatures.MORDOR_BASALT.configured(spikyConfig).decorated(placeHeightmapChance(spikyChance)));
		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, LOTRFeatures.MORDOR_BASALT.configured(flatConfig).decorated(placeHeightmapChance(flatChance)));
	}

	public static void addMordorDirtGravel(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(MORDOR_ROCK_FILLER, MORDOR_DIRT, 61)).range(256)).squared()).count(10));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(MORDOR_ROCK_FILLER, MORDOR_GRAVEL, 33)).range(256)).squared()).count(10));
	}

	public static void addMordorGrass(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(MORDOR_GRASS_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addMordorMoss(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.MORDOR_MOSS.configured(MORDOR_MOSS_CONFIG).decorated(placeHeightmapChance(chance)));
	}

	public static void addMordorOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(MORDOR_ROCK_FILLER, DURNOR_ORE, 13)).range(64)).squared()).count(20));
		addMorgulIronOre(builder, true);
		addGuldurilOre(builder, true);
	}

	public static void addMordorThorns(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(MORDOR_THORN_CONFIG).decorated(placeHeightmapDoubleChance(chance)));
	}

	public static void addMoreCommonPapyrus(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addPapyrus(builder, 20);
	}

	public static void addMoreMushroomsFreq(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(placeHeightmapDoubleFreq(freq * 2)));
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addMoreSwampReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		int freq = 1;
		ReedsFeatureConfig noDriedConfig = (ReedsFeatureConfig) REEDS_CONFIG_FOR_DRIED_CHANCE.apply(0.0F);
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.REEDS.configured(noDriedConfig).decorated(placeHeightmapFreq(freq)));
	}

	public static void addMorgulIronOre(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, boolean mordor) {
		int oreFreq = 20;
		ConfiguredPlacement orePlacement = Placement.RANGE.configured(new TopSolidRangeConfig(0, 0, 64));
		OreFeatureConfig oreConfig = mordor ? new OreFeatureConfig(MORDOR_ROCK_FILLER, MORGUL_IRON_ORE_MORDOR, 9) : new OreFeatureConfig(FillerBlockType.NATURAL_STONE, MORGUL_IRON_ORE_STONE, 9);
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(oreConfig).decorated(orePlacement).squared()).count(oreFreq));
	}

	public static void addMorgulShrooms(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(MORGUL_SHROOM_CONFIG).decorated(placeHeightmapDoubleChance(chance)));
	}

	public static void addMountainsFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] mountainsFlowers = { Blocks.POPPY, 10, Blocks.DANDELION, 20, Blocks.BLUE_ORCHID, 10, LOTRBlocks.BLUEBELL.get(), 5 };
		addFlowers(builder, freq, LOTRUtil.combineVarargs(mountainsFlowers, extraFlowers));
	}

	public static void addMushrooms(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(BROWN_MUSHROOM_CONFIG).decorated(placeHeightmapDoubleChance(4)));
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RED_MUSHROOM_CONFIG).decorated(placeHeightmapDoubleChance(8)));
	}

	public static void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, COAL_ORE, 17)).range(128)).squared()).count(20));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, IRON_ORE, 9)).range(64)).squared()).count(20));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, COPPER_ORE, 9)).range(128)).squared()).count(16));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, TIN_ORE, 9)).range(128)).squared()).count(16));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, GOLD_ORE, 9)).range(32)).squared()).count(2));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, SILVER_ORE, 9)).range(32)).squared()).count(3));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, SULFUR_ORE, 9)).range(64)).squared()).count(2));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, NITER_ORE, 9)).range(64)).squared()).count(2));
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, SALT_ORE, 13)).range(64)).squared()).count(2));
	}

	public static void addPackedIceVeins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		int size = 16;
		int yMin = 32;
		int yMax = 256;
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, PACKED_ICE, size)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(yMin, yMin, yMax))).squared()).count(freq));
	}

	public static void addPapyrus(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addPapyrus(builder, 10);
	}

	public static void addPapyrus(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.REEDS.configured(PAPYRUS_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addPlainsFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] plainsFlowers = { Blocks.POPPY, 20, Blocks.DANDELION, 30, Blocks.AZURE_BLUET, 20, Blocks.OXEYE_DAISY, 20, Blocks.CORNFLOWER, 5, Blocks.ORANGE_TULIP, 3, Blocks.RED_TULIP, 3, Blocks.PINK_TULIP, 3, Blocks.WHITE_TULIP, 3, LOTRBlocks.BLUEBELL.get(), 5, LOTRBlocks.MARIGOLD.get(), 10, LOTRBlocks.LAVENDER.get(), 5 };
		addFlowers(builder, freq, LOTRUtil.combineVarargs(plainsFlowers, extraFlowers));
	}

	public static void addPumpkins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(PUMPKIN_PATCH_CONFIG).decorated(placeHeightmapDoubleChance(32)));
	}

	public static void addQuagmire(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(QUAGMIRE, FeatureSpread.of(2, 2), 2, Lists.newArrayList(DIRT, COARSE_DIRT, GRASS_BLOCK))).decorated(placeTopSolidFreq(freq)));
	}

	public static void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addReedsWithDriedChance(builder, 0.1F);
	}

	public static void addReedsWithDriedChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, float driedChance) {
		addReedsWithFreqAndDriedChance(builder, 10, driedChance);
	}

	public static void addReedsWithFreqAndDriedChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, float driedChance) {
		ReedsFeatureConfig config = (ReedsFeatureConfig) REEDS_CONFIG_FOR_DRIED_CHANCE.apply(driedChance);
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.REEDS.configured(config).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addRhunForestFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] rhunFlowers = { LOTRBlocks.MARIGOLD.get(), 10, LOTRBlocks.WHITE_CHRYSANTHEMUM.get(), 10, LOTRBlocks.YELLOW_CHRYSANTHEMUM.get(), 10, LOTRBlocks.PINK_CHRYSANTHEMUM.get(), 10, LOTRBlocks.RED_CHRYSANTHEMUM.get(), 10, LOTRBlocks.ORANGE_CHRYSANTHEMUM.get(), 10 };
		addForestFlowers(builder, freq, LOTRUtil.combineVarargs(rhunFlowers, extraFlowers));
	}

	public static void addRhunPlainsFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		Object[] rhunFlowers = { LOTRBlocks.MARIGOLD.get(), 10, LOTRBlocks.WHITE_CHRYSANTHEMUM.get(), 10, LOTRBlocks.YELLOW_CHRYSANTHEMUM.get(), 10, LOTRBlocks.PINK_CHRYSANTHEMUM.get(), 10, LOTRBlocks.RED_CHRYSANTHEMUM.get(), 10, LOTRBlocks.ORANGE_CHRYSANTHEMUM.get(), 10 };
		addPlainsFlowers(builder, freq, LOTRUtil.combineVarargs(rhunFlowers, extraFlowers));
	}

	public static void addRiverRushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addRushes(builder, 7);
	}

	public static void addRohanRockPatches(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, ROHAN_ROCK, 61)).range(80)).squared()).count(4));
	}

	public static void addRushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(RUSHES_CONFIG).decorated(placeHeightmapFreq(freq)));
	}

	public static void addSaltInSand(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int size, int freq, int minHeight, int maxHeight) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(SAND_FILLER, SALT_ORE, size + 1)).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(minHeight, minHeight, maxHeight))).squared()).count(freq));
	}

	public static void addSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addSandSediments(builder, SAND);
	}

	public static void addSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, BlockState sandBlock) {
		int freq = 3;
		builder.addFeature(Decoration.UNDERGROUND_ORES, Feature.DISK.configured(new SphereReplaceConfig(sandBlock, FeatureSpread.of(2, 4), 2, Lists.newArrayList(DIRT, COARSE_DIRT, GRASS_BLOCK))).decorated(placeTopSolidFreq(freq)));
	}

	public static void addSeaCarvers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		builder.addCarver(Carving.AIR, LOTRWorldCarvers.CAVE.configured(new ProbabilityConfig(0.06666667F)));
		builder.addCarver(Carving.AIR, LOTRWorldCarvers.CANYON.configured(new ProbabilityConfig(0.02F)));
		builder.addCarver(Carving.LIQUID, LOTRWorldCarvers.UNDERWATER_CAVE.configured(new ProbabilityConfig(0.06666667F)));
		builder.addCarver(Carving.LIQUID, LOTRWorldCarvers.UNDERWATER_CANYON.configured(new ProbabilityConfig(0.02F)));
	}

	public static void addSeagrass(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, float tallProb) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, ((ConfiguredFeature) Feature.SEAGRASS.configured(new ProbabilityConfig(tallProb)).count(freq)).decorated(placeTopSolidFreq(1)));
	}

	public static void addSeaPickles(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		ConfiguredFeature seaPickle = Feature.SEA_PICKLE.configured(new FeatureSpreadConfig(20));
		ConfiguredPlacement placement = placeTopSolidChance(16);
		addLatitudeBased(builder, Decoration.VEGETAL_DECORATION, seaPickle, placement, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.CORAL));
	}

	public static void addSimbelmyneChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(SIMBELMYNE_CONFIG).decorated(placeFlowers()).chance(chance));
	}

	public static void addSparseFoxBerryBushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addFoxBerryBushesChance(builder, 12);
	}

	public static void addSponges(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		ConfiguredFeature sponge = LOTRFeatures.UNDERWATER_SPONGE.configured(SPONGE_CONFIG);
		ConfiguredPlacement placement = placeTopSolidChance(20);
		addLatitudeBased(builder, Decoration.VEGETAL_DECORATION, sponge, placement, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.CORAL));
	}

	public static void addStoneOrcishOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addMorgulIronOre(builder, false);
		addGuldurilOre(builder, false);
	}

	private static void addStoneVariety(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, BlockState stone, int freq, int height) {
		builder.addFeature(Decoration.UNDERGROUND_ORES, (ConfiguredFeature) ((ConfiguredFeature) ((ConfiguredFeature) Feature.ORE.configured(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, stone, 61)).range(height)).squared()).count(freq));
	}

	public static void addSugarCane(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		int freq = 10;
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SUGAR_CANE_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addSunflowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(SUNFLOWER_CONFIG).decorated(placeHeightmapChance(chance)));
	}

	public static void addSwampFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, Object... extraFlowers) {
		addDefaultFlowers(builder, freq, extraFlowers);
	}

	public static void addSwampRushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addRushes(builder, 2);
	}

	public static void addSwampSeagrass(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addSeagrass(builder, 100, 0.4F);
	}

	public static void addTerrainSharpener(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, List<BlockState> targets, int minHeight, int maxHeight, int freq) {
		TerrainSharpenFeatureConfig config = new TerrainSharpenFeatureConfig(targets, minHeight, maxHeight);
		ConfiguredPlacement placement = placeHeightmapFreq(freq);
		builder.addFeature(Decoration.LOCAL_MODIFICATIONS, LOTRFeatures.TERRAIN_SHARPEN.configured(config).decorated(placement));
	}

	public static void addTrees(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).build()).build());
	}

	public static void addTreesAboveTreeline(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, int treeline, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).layerLimit(treeline, false).build()).build());
	}

	public static void addTreesAboveTreelineIncrease(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, int extraCount, int treeline, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).extraCount(extraCount).layerLimit(treeline, false).build()).build());
	}

	public static void addTreesBelowTreeline(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, int treeline, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).layerLimit(treeline, true).build()).build());
	}

	public static void addTreesBelowTreelineIncrease(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, int extraCount, int treeline, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).extraCount(extraCount).layerLimit(treeline, true).build()).build());
	}

	public static void addTreesFromGeneratingConfig(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, GeneratingTreesConfig config) {
		ConfiguredPlacement treePlacement = LOTRFeatures.TREE_CLUSTERS.configured(config.clusterConfig);
		addTreesWithPlacement(builder, treePlacement, config.latitudeConfig, config.weightedTrees);
		if (config.shouldUpdateBiomeTreeAmount()) {
			biome.updateBiomePodzolVariables(config.getTreeCountApproximation(), config.getTreeLayerUpperLimit());
		}

		addLeafBushesWithPlacement(builder, LOTRFeatures.TREE_CLUSTERS.configured(config.makePlacementForLeafBushes()), config.latitudeConfig);
		addFallenLeavesWithPlacement(builder, LOTRFeatures.TREE_CLUSTERS.configured(config.makePlacementForFallenLeaves()), config.latitudeConfig);
	}

	public static void addTreesIncrease(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, int extraCount, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).extraCount(extraCount).build()).build());
	}

	public static void addTreesWithClusters(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int count, float extraChance, TreeCluster cluster, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).cluster(cluster).build()).build());
	}

	public static void addTreesWithLatitudeConfig(LOTRBiomeBase biome, net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, LatitudeBasedFeatureConfig.LatitudeConfiguration latitudeConfig, int count, float extraChance, Object... weightedTrees) {
		addTreesFromGeneratingConfig(biome, builder, GeneratingTreesConfig.builder().weightedTrees(weightedTrees).clusterConfig(TreeClustersConfig.builder().count(count).extraChance(extraChance).build()).latitudeConfig(latitudeConfig).build());
	}

	private static void addTreesWithPlacement(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, ConfiguredPlacement placement, LatitudeBasedFeatureConfig.LatitudeConfiguration latConfig, Object... weightedTrees) {
		WeightedRandomFeatureConfig wrConfig = WeightedRandomFeatureConfig.fromEntries(weightedTrees);
		ConfiguredFeature randomTreeFeature = LOTRFeatures.WEIGHTED_RANDOM.configured(wrConfig);
		if (latConfig == null) {
			builder.addFeature(Decoration.VEGETAL_DECORATION, randomTreeFeature.decorated(placement));
		} else {
			addLatitudeBased(builder, Decoration.VEGETAL_DECORATION, randomTreeFeature, placement, latConfig);
		}

	}

	public static void addTreeTorches(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, int yMin, int yMax, BlockState... torches) {
		WeightedBlockStateProvider stateProvider = new WeightedBlockStateProvider();
		Arrays.asList(torches).stream().forEach(state -> {
			stateProvider.add(state, 1);
		});
		ConfiguredPlacement placement = (ConfiguredPlacement) ((ConfiguredPlacement) Placement.RANGE.configured(new TopSolidRangeConfig(yMin, yMin, yMax)).squared()).count(freq);
		builder.addFeature(Decoration.VEGETAL_DECORATION, LOTRFeatures.TREE_TORCHES.configured(new BlockStateProvidingFeatureConfig(stateProvider)).decorated(placement));
	}

	public static void addTundraBushes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, BlockStateProvider blockProvider, int triesPerPatch, ConfiguredPlacement placement) {
		BlockClusterFeatureConfig config = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(blockProvider, new SimpleBlockPlacer()).tries(triesPerPatch).whitelist(ImmutableSet.of(Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.STONE, Blocks.SNOW_BLOCK)).noProjection().build();
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(config).decorated(placement));
	}

	public static void addTundraBushesChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance, BlockStateProvider blockProvider, int triesPerPatch) {
		addTundraBushes(builder, blockProvider, triesPerPatch, placeHeightmapChance(chance));
	}

	public static void addTundraBushesFreq(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq, BlockStateProvider blockProvider, int triesPerPatch) {
		addTundraBushes(builder, blockProvider, triesPerPatch, placeHeightmapFreq(freq));
	}

	public static void addWaterLavaSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addWaterSprings(builder);
		addLavaSprings(builder);
	}

	public static void addWaterLavaSpringsReducedAboveground(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int reducedAboveY, float aboveReductionFactor) {
		int defaultWater = 50;
		int defaultLava = 20;
		int height = 256;
		float belowFactor = (float) reducedAboveY / (float) height;
		float aboveFactor = (float) (height - reducedAboveY) / (float) height;
		aboveFactor *= aboveReductionFactor;
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) Feature.SPRING.configured(WATER_SPRING_CONFIG).decorated(Placement.RANGE_BIASED.configured(new TopSolidRangeConfig(8, 8, reducedAboveY))).squared()).count((int) (defaultWater * belowFactor)));
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) Feature.SPRING.configured(LAVA_SPRING_CONFIG).decorated(Placement.RANGE_VERY_BIASED.configured(new TopSolidRangeConfig(8, 16, reducedAboveY))).squared()).count((int) (defaultLava * belowFactor)));
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) Feature.SPRING.configured(WATER_SPRING_CONFIG).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(reducedAboveY, reducedAboveY, height))).squared()).count((int) (defaultWater * aboveFactor)));
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) Feature.SPRING.configured(LAVA_SPRING_CONFIG).decorated(Placement.RANGE.configured(new TopSolidRangeConfig(reducedAboveY, reducedAboveY, height))).squared()).count((int) (defaultLava * aboveFactor)));
	}

	public static void addWaterLilies(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(LILY_PAD_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addWaterLiliesWithFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(LILY_PAD_WITH_FLOWERS_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addWaterLiliesWithRareFlowers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.RANDOM_PATCH.configured(LILY_PAD_WITH_RARE_FLOWERS_CONFIG).decorated(placeHeightmapDoubleFreq(freq)));
	}

	public static void addWaterSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addWaterSprings(builder, 50);
	}

	public static void addWaterSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int freq) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, (ConfiguredFeature) ((ConfiguredFeature) Feature.SPRING.configured(WATER_SPRING_CONFIG).decorated(Placement.RANGE_BIASED.configured(new TopSolidRangeConfig(8, 8, 256))).squared()).count(freq));
	}

	public static void addWhiteSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addSandSediments(builder, WHITE_SAND);
	}

	public static void addWildPipeweedChance(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder, int chance) {
		builder.addFeature(Decoration.VEGETAL_DECORATION, Feature.FLOWER.configured(WILD_PIPEWEED_CONFIG).decorated(placeHeightmapDoubleChance(chance)));
	}

	public static ConfiguredFeature apple() {
		return LOTRFeatures.getWeightedRandom().configured(WeightedRandomFeatureConfig.fromEntries(Feature.TREE.configured(APPLE_TREE_RED), 19, Feature.TREE.configured(APPLE_TREE_GREEN), 19, Feature.TREE.configured(APPLE_TREE_MIX), 2));
	}

	public static ConfiguredFeature appleBees() {
		return LOTRFeatures.getWeightedRandom().configured(WeightedRandomFeatureConfig.fromEntries(Feature.TREE.configured(APPLE_TREE_RED_BEES), 19, Feature.TREE.configured(APPLE_TREE_GREEN_BEES), 19, Feature.TREE.configured(APPLE_TREE_MIX_BEES), 2));
	}

	public static ConfiguredFeature aspen() {
		return Feature.TREE.configured(ASPEN_TREE);
	}

	public static ConfiguredFeature aspenLarge() {
		return aspen();
	}

	public static ConfiguredFeature beech() {
		return Feature.TREE.configured(BEECH_TREE);
	}

	public static ConfiguredFeature beechBees() {
		return Feature.TREE.configured(BEECH_TREE_BEES);
	}

	public static ConfiguredFeature beechDead() {
		return LOTRFeatures.WRAPPED_TREE.configured(BEECH_DEAD);
	}

	public static ConfiguredFeature beechFancy() {
		return Feature.TREE.configured(BEECH_TREE_FANCY);
	}

	public static ConfiguredFeature beechFancyBees() {
		return Feature.TREE.configured(BEECH_TREE_FANCY_BEES);
	}

	public static ConfiguredFeature beechFangorn() {
		return Feature.TREE.configured(BEECH_FANGORN);
	}

	public static ConfiguredFeature beechParty() {
		return Feature.TREE.configured(BEECH_PARTY);
	}

	public static ConfiguredFeature birch() {
		return LOTRFeatures.getWeightedRandom().configured(WeightedRandomFeatureConfig.fromEntries(Features.BIRCH, 1, Feature.TREE.configured(BIRCH_TREE_ALT), 2));
	}

	public static ConfiguredFeature birchBees() {
		return LOTRFeatures.getWeightedRandom().configured(WeightedRandomFeatureConfig.fromEntries(Features.BIRCH_BEES_005, 1, Feature.TREE.configured(BIRCH_TREE_ALT_BEES), 2));
	}

	public static ConfiguredFeature birchDead() {
		return LOTRFeatures.WRAPPED_TREE.configured(BIRCH_DEAD);
	}

	public static ConfiguredFeature birchFancy() {
		return Feature.TREE.configured(BIRCH_TREE_FANCY);
	}

	public static ConfiguredFeature birchFancyBees() {
		return Feature.TREE.configured(BIRCH_TREE_FANCY_BEES);
	}

	public static ConfiguredFeature birchParty() {
		return Feature.TREE.configured(BIRCH_PARTY);
	}

	private static BaseTreeFeatureConfig buildClassicTree(BlockState log, BlockState leaves, int baseHeight, int heightRandA) {
		return buildClassicTree(log, new SimpleBlockStateProvider(leaves), baseHeight, heightRandA, false, false);
	}

	private static BaseTreeFeatureConfig buildClassicTree(BlockState log, BlockStateProvider leavesPool, int baseHeight, int heightRandA) {
		return buildClassicTree(log, leavesPool, baseHeight, heightRandA, false, false);
	}

	private static BaseTreeFeatureConfig buildClassicTree(BlockState log, BlockStateProvider leavesPool, int baseHeight, int heightRandA, boolean bees, boolean vines) {
		return buildClassicTreeWithSpecifiedFoliage(log, leavesPool, baseHeight, heightRandA, new BlobFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), bees, vines);
	}

	private static BaseTreeFeatureConfig buildClassicTreeWithBees(BlockState log, BlockState leaves, int baseHeight, int heightRandA) {
		return buildClassicTree(log, new SimpleBlockStateProvider(leaves), baseHeight, heightRandA, true, false);
	}

	private static BaseTreeFeatureConfig buildClassicTreeWithBees(BlockState log, BlockStateProvider leavesPool, int baseHeight, int heightRandA) {
		return buildClassicTree(log, leavesPool, baseHeight, heightRandA, true, false);
	}

	private static BaseTreeFeatureConfig buildClassicTreeWithBeesAndVines(BlockState log, BlockState leaves, int baseHeight, int heightRandA) {
		return buildClassicTree(log, new SimpleBlockStateProvider(leaves), baseHeight, heightRandA, true, true);
	}

	private static BaseTreeFeatureConfig buildClassicTreeWithSpecifiedFoliage(BlockState log, BlockStateProvider leavesPool, int baseHeight, int heightRandA, FoliagePlacer foliage, boolean bees, boolean vines) {
		List decorators = new ArrayList();
		if (bees) {
			decorators.add(Placements.BEEHIVE_005);
		}

		if (vines) {
			decorators.add(TrunkVineTreeDecorator.INSTANCE);
			decorators.add(LeaveVineTreeDecorator.INSTANCE);
		}

		return new Builder(new SimpleBlockStateProvider(log), leavesPool, foliage, new StraightTrunkPlacer(baseHeight, heightRandA, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.copyOf(decorators)).build();
	}

	private static BaseTreeFeatureConfig buildClassicTreeWithVines(BlockState log, BlockState leaves, int baseHeight, int heightRandA) {
		return buildClassicTree(log, new SimpleBlockStateProvider(leaves), baseHeight, heightRandA, false, true);
	}

	private static BaseTreeFeatureConfig buildFancyTree(BlockState log, BlockState leaves) {
		return buildFancyTree(log, leaves, false);
	}

	private static BaseTreeFeatureConfig buildFancyTree(BlockState log, BlockState leaves, boolean bees) {
		List decorators = new ArrayList();
		if (bees) {
			decorators.add(Placements.BEEHIVE_005);
		}

		return new Builder(new SimpleBlockStateProvider(log), new SimpleBlockStateProvider(leaves), new FancyFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(4), 4), new FancyTrunkPlacer(3, 11, 0), new TwoLayerFeature(0, 0, 0, OptionalInt.of(4))).ignoreVines().heightmap(Type.MOTION_BLOCKING).decorators(ImmutableList.copyOf(decorators)).build();
	}

	private static BaseTreeFeatureConfig buildFancyTreeWithBees(BlockState log, BlockState leaves) {
		return buildFancyTree(log, leaves, true);
	}

	private static BaseTreeFeatureConfig buildFangornTree(BlockState log, BlockState wood, BlockState strippedLog, BlockState leaves) {
		return new Builder(new SimpleBlockStateProvider(log), new SimpleBlockStateProvider(leaves), new ClusterFoliagePlacer(FeatureSpread.fixed(3), FeatureSpread.fixed(0)), new FangornTrunkPlacer(20, 20, 0, wood, strippedLog), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE, LeaveVineTreeDecorator.INSTANCE)).build();
	}

	private static BaseTreeFeatureConfig buildMirkPartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves) {
		return buildMirkPartyTree(log, wood, branch, leaves, ImmutableList.of());
	}

	private static BaseTreeFeatureConfig buildMirkPartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves, List decorators) {
		return buildPartyTree(log, wood, branch, leaves, 10, 14, new MirkOakFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 1), decorators);
	}

	private static BaseTreeFeatureConfig buildNormalFoliagePartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves, int baseHeight, int heightRandA) {
		return buildPartyTree(log, wood, branch, leaves, baseHeight, heightRandA, new ClusterFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0)), ImmutableList.of());
	}

	private static BaseTreeFeatureConfig buildNormalHeightPartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves, FoliagePlacer foliage) {
		return buildPartyTree(log, wood, branch, leaves, 10, 14, foliage, ImmutableList.of());
	}

	private static BaseTreeFeatureConfig buildNormalPartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves) {
		return buildNormalFoliagePartyTree(log, wood, branch, leaves, 10, 14);
	}

	private static BaseTreeFeatureConfig buildPartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves, int baseHeight, int heightRandA, FoliagePlacer foliage) {
		return buildPartyTree(log, wood, branch, leaves, baseHeight, heightRandA, foliage, ImmutableList.of());
	}

	private static BaseTreeFeatureConfig buildPartyTree(BlockState log, BlockState wood, BlockState branch, BlockState leaves, int baseHeight, int heightRandA, FoliagePlacer foliage, List decorators) {
		return new Builder(new SimpleBlockStateProvider(log), new SimpleBlockStateProvider(leaves), foliage, new PartyTrunkPlacer(baseHeight, heightRandA, 0, wood, branch), new TwoLayerFeature(1, 1, 2)).ignoreVines().decorators(ImmutableList.copyOf(decorators)).build();
	}

	private static BaseTreeFeatureConfig buildShrub(BlockState log, BlockState leaves) {
		return new Builder(new SimpleBlockStateProvider(log), new SimpleBlockStateProvider(leaves), new BushFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(1), 2), new StraightTrunkPlacer(1, 0, 0), new TwoLayerFeature(0, 0, 0)).heightmap(Type.MOTION_BLOCKING_NO_LEAVES).build();
	}

	public static ConfiguredFeature cedar() {
		return Feature.TREE.configured(CEDAR_TREE);
	}

	public static ConfiguredFeature cedarLarge() {
		return Feature.TREE.configured(CEDAR_TREE_LARGE);
	}

	public static ConfiguredFeature charred() {
		return LOTRFeatures.WRAPPED_TREE.configured(CHARRED_TREE);
	}

	public static ConfiguredFeature cherry() {
		return Feature.TREE.configured(CHERRY_TREE);
	}

	public static ConfiguredFeature cherryBees() {
		return Feature.TREE.configured(CHERRY_TREE_BEES);
	}

	public static ConfiguredFeature culumalda() {
		return Feature.TREE.configured(CULUMALDA_TREE);
	}

	public static ConfiguredFeature culumaldaBees() {
		return Feature.TREE.configured(CULUMALDA_TREE_BEES);
	}

	public static ConfiguredFeature cypress() {
		return Feature.TREE.configured(CYPRESS_TREE);
	}

	public static ConfiguredFeature darkOak() {
		return Features.DARK_OAK;
	}

	public static ConfiguredFeature darkOakParty() {
		return Feature.TREE.configured(DARK_OAK_PARTY);
	}

	public static ConfiguredFeature darkOakShrub() {
		return Feature.TREE.configured(DARK_OAK_SHRUB);
	}

	public static ConfiguredFeature fir() {
		return Feature.TREE.configured(FIR_TREE);
	}

	public static ConfiguredFeature firShrub() {
		return Feature.TREE.configured(FIR_SHRUB);
	}

	public static ConfiguredFeature greenOak() {
		return Feature.TREE.configured(GREEN_OAK_TREE);
	}

	public static ConfiguredFeature greenOakBees() {
		return Feature.TREE.configured(GREEN_OAK_TREE_BEES);
	}

	public static ConfiguredFeature greenOakParty() {
		return Feature.TREE.configured(GREEN_OAK_PARTY);
	}

	public static ConfiguredFeature greenOakShrub() {
		return Feature.TREE.configured(GREEN_OAK_SHRUB);
	}

	public static ConfiguredFeature holly() {
		return Feature.TREE.configured(HOLLY_TREE);
	}

	public static ConfiguredFeature hollyBees() {
		return Feature.TREE.configured(HOLLY_TREE_BEES);
	}

	public static ConfiguredFeature jungle() {
		return Features.JUNGLE_TREE;
	}

	public static ConfiguredFeature jungleMega() {
		return Features.MEGA_JUNGLE_TREE;
	}

	public static ConfiguredFeature jungleShrub() {
		return Features.JUNGLE_BUSH;
	}

	public static ConfiguredFeature lairelosse() {
		return Feature.TREE.configured(LAIRELOSSE_TREE);
	}

	public static ConfiguredFeature larch() {
		return Feature.TREE.configured(LARCH_TREE);
	}

	public static ConfiguredFeature lebethron() {
		return Feature.TREE.configured(LEBETHRON_TREE);
	}

	public static ConfiguredFeature lebethronBees() {
		return Feature.TREE.configured(LEBETHRON_TREE_BEES);
	}

	public static ConfiguredFeature lebethronFancy() {
		return Feature.TREE.configured(LEBETHRON_TREE_FANCY);
	}

	public static ConfiguredFeature lebethronFancyBees() {
		return Feature.TREE.configured(LEBETHRON_TREE_FANCY_BEES);
	}

	public static ConfiguredFeature lebethronParty() {
		return Feature.TREE.configured(LEBETHRON_PARTY);
	}

	public static ConfiguredFeature mallorn() {
		return Feature.TREE.configured(MALLORN_TREE);
	}

	public static ConfiguredFeature mallornBees() {
		return Feature.TREE.configured(MALLORN_TREE_BEES);
	}

	public static ConfiguredFeature mallornBoughs() {
		return Feature.TREE.configured(MALLORN_TREE_BOUGHS);
	}

	public static ConfiguredFeature mallornParty() {
		return Feature.TREE.configured(MALLORN_PARTY);
	}

	public static ConfiguredFeature maple() {
		return Feature.TREE.configured(MAPLE_TREE);
	}

	public static ConfiguredFeature mapleBees() {
		return Feature.TREE.configured(MAPLE_TREE_BEES);
	}

	public static ConfiguredFeature mapleFancy() {
		return Feature.TREE.configured(MAPLE_TREE_FANCY);
	}

	public static ConfiguredFeature mapleFancyBees() {
		return Feature.TREE.configured(MAPLE_TREE_FANCY_BEES);
	}

	public static ConfiguredFeature mapleParty() {
		return Feature.TREE.configured(MAPLE_PARTY);
	}

	public static ConfiguredFeature mirkOak() {
		return Feature.TREE.configured(MIRK_OAK_TREE);
	}

	public static ConfiguredFeature mirkOakParty() {
		return Feature.TREE.configured(MIRK_OAK_PARTY);
	}

	public static ConfiguredFeature mirkOakShrub() {
		return Feature.TREE.configured(MIRK_OAK_SHRUB);
	}

	public static ConfiguredFeature oak() {
		return Features.OAK;
	}

	public static ConfiguredFeature oakBees() {
		return Features.OAK_BEES_005;
	}

	public static ConfiguredFeature oakBeesVines() {
		return Feature.TREE.configured(OAK_TREE_BEES_VINES);
	}

	public static ConfiguredFeature oakDead() {
		return LOTRFeatures.WRAPPED_TREE.configured(OAK_DEAD);
	}

	public static ConfiguredFeature oakDesert() {
		return LOTRFeatures.WRAPPED_TREE.configured(OAK_DESERT);
	}

	public static ConfiguredFeature oakDesertBees() {
		return LOTRFeatures.WRAPPED_TREE.configured(OAK_DESERT_BEES);
	}

	public static ConfiguredFeature oakFancy() {
		return Features.FANCY_OAK;
	}

	public static ConfiguredFeature oakFancyBees() {
		return Features.FANCY_OAK_BEES_005;
	}

	public static ConfiguredFeature oakFangorn() {
		return Feature.TREE.configured(OAK_FANGORN);
	}

	public static ConfiguredFeature oakParty() {
		return Feature.TREE.configured(OAK_PARTY);
	}

	public static ConfiguredFeature oakShrub() {
		return Feature.TREE.configured(OAK_SHRUB);
	}

	public static ConfiguredFeature oakSwamp() {
		return Features.SWAMP_TREE;
	}

	public static ConfiguredFeature oakTall() {
		return Feature.TREE.configured(OAK_TREE_TALL);
	}

	public static ConfiguredFeature oakTallBees() {
		return Feature.TREE.configured(OAK_TREE_TALL_BEES);
	}

	public static ConfiguredFeature oakTallBeesVines() {
		return Feature.TREE.configured(OAK_TREE_TALL_BEES_VINES);
	}

	public static ConfiguredFeature oakTallVines() {
		return Feature.TREE.configured(OAK_TREE_TALL_VINES);
	}

	public static ConfiguredFeature oakVines() {
		return Feature.TREE.configured(OAK_TREE_VINES);
	}

	public static ConfiguredFeature pear() {
		return Feature.TREE.configured(PEAR_TREE);
	}

	public static ConfiguredFeature pearBees() {
		return Feature.TREE.configured(PEAR_TREE_BEES);
	}

	public static ConfiguredFeature pine() {
		return Feature.TREE.configured(PINE_TREE);
	}

	public static ConfiguredFeature pineDead() {
		return Feature.TREE.configured(PINE_DEAD);
	}

	public static ConfiguredFeature pineShrub() {
		return Feature.TREE.configured(PINE_SHRUB);
	}

	private static ConfiguredPlacement placeFlowers() {
		return Placement.SPREAD_32_ABOVE.configured(NoPlacementConfig.INSTANCE).decorated(Placement.HEIGHTMAP.configured(IPlacementConfig.NONE)).squared();
	}

	private static ConfiguredPlacement placeHeightmapChance(int chance) {
		return (ConfiguredPlacement) ((ConfiguredPlacement) Placement.HEIGHTMAP.configured(IPlacementConfig.NONE).squared()).chance(chance);
	}

	private static ConfiguredPlacement placeHeightmapDoubleChance(int chance) {
		return (ConfiguredPlacement) ((ConfiguredPlacement) Placement.HEIGHTMAP_SPREAD_DOUBLE.configured(IPlacementConfig.NONE).squared()).chance(chance);
	}

	private static ConfiguredPlacement placeHeightmapDoubleFreq(int freq) {
		return (ConfiguredPlacement) ((ConfiguredPlacement) Placement.HEIGHTMAP_SPREAD_DOUBLE.configured(IPlacementConfig.NONE).squared()).count(freq);
	}

	private static ConfiguredPlacement placeHeightmapFreq(int freq) {
		return (ConfiguredPlacement) ((ConfiguredPlacement) Placement.HEIGHTMAP.configured(IPlacementConfig.NONE).squared()).count(freq);
	}

	private static ConfiguredPlacement placeTopSolidChance(int chance) {
		return (ConfiguredPlacement) ((ConfiguredPlacement) Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE).squared()).chance(chance);
	}

	private static ConfiguredPlacement placeTopSolidFreq(int freq) {
		return (ConfiguredPlacement) ((ConfiguredPlacement) Placement.TOP_SOLID_HEIGHTMAP.configured(IPlacementConfig.NONE).squared()).count(freq);
	}

	public static ConfiguredFeature redOak() {
		return Feature.TREE.configured(RED_OAK_TREE);
	}

	public static ConfiguredFeature redOakBees() {
		return Feature.TREE.configured(RED_OAK_TREE_BEES);
	}

	public static ConfiguredFeature redOakParty() {
		return Feature.TREE.configured(RED_OAK_PARTY);
	}

	public static void setup(RegistryEvent.Register<Block> event) {
		if (event.getRegistry() == ForgeRegistries.BLOCKS) {
			LOTRWorldCarvers.register();
			MORDOR_ROCK_FILLER = new BlockMatchRuleTest((Block) LOTRBlocks.MORDOR_ROCK.get());
			SAND_FILLER = new TagMatchRuleTest(BlockTags.SAND);
			STONE = Blocks.STONE.defaultBlockState();
			DIRT = Blocks.DIRT.defaultBlockState();
			COARSE_DIRT = Blocks.COARSE_DIRT.defaultBlockState();
			GRAVEL = Blocks.GRAVEL.defaultBlockState();
			GRANITE = Blocks.GRANITE.defaultBlockState();
			DIORITE = Blocks.DIORITE.defaultBlockState();
			ANDESITE = Blocks.ANDESITE.defaultBlockState();
			PACKED_ICE = Blocks.PACKED_ICE.defaultBlockState();
			BLUE_ICE = Blocks.BLUE_ICE.defaultBlockState();
			GONDOR_ROCK = ((Block) LOTRBlocks.GONDOR_ROCK.get()).defaultBlockState();
			MORDOR_ROCK = ((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState();
			ROHAN_ROCK = ((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState();
			BLUE_ROCK = ((Block) LOTRBlocks.BLUE_ROCK.get()).defaultBlockState();
			RED_ROCK = ((Block) LOTRBlocks.RED_ROCK.get()).defaultBlockState();
			MORDOR_DIRT = ((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState();
			MORDOR_GRAVEL = ((Block) LOTRBlocks.MORDOR_GRAVEL.get()).defaultBlockState();
			COAL_ORE = Blocks.COAL_ORE.defaultBlockState();
			IRON_ORE = Blocks.IRON_ORE.defaultBlockState();
			COPPER_ORE = ((Block) LOTRBlocks.COPPER_ORE.get()).defaultBlockState();
			TIN_ORE = ((Block) LOTRBlocks.TIN_ORE.get()).defaultBlockState();
			GOLD_ORE = Blocks.GOLD_ORE.defaultBlockState();
			SILVER_ORE = ((Block) LOTRBlocks.SILVER_ORE.get()).defaultBlockState();
			SULFUR_ORE = ((Block) LOTRBlocks.SULFUR_ORE.get()).defaultBlockState();
			NITER_ORE = ((Block) LOTRBlocks.NITER_ORE.get()).defaultBlockState();
			SALT_ORE = ((Block) LOTRBlocks.SALT_ORE.get()).defaultBlockState();
			LAPIS_ORE = Blocks.LAPIS_ORE.defaultBlockState();
			MITHRIL_ORE = ((Block) LOTRBlocks.MITHRIL_ORE.get()).defaultBlockState();
			EDHELVIR_ORE = ((Block) LOTRBlocks.EDHELVIR_ORE.get()).defaultBlockState();
			GLOWSTONE_ORE = ((Block) LOTRBlocks.GLOWSTONE_ORE.get()).defaultBlockState();
			DURNOR_ORE = ((Block) LOTRBlocks.DURNOR_ORE.get()).defaultBlockState();
			MORGUL_IRON_ORE_MORDOR = ((Block) LOTRBlocks.MORGUL_IRON_ORE_MORDOR.get()).defaultBlockState();
			MORGUL_IRON_ORE_STONE = ((Block) LOTRBlocks.MORGUL_IRON_ORE_STONE.get()).defaultBlockState();
			GULDURIL_ORE_MORDOR = ((Block) LOTRBlocks.GULDURIL_ORE_MORDOR.get()).defaultBlockState();
			GULDURIL_ORE_STONE = ((Block) LOTRBlocks.GULDURIL_ORE_STONE.get()).defaultBlockState();
			EDHELVIR_CRYSTAL = ((Block) LOTRBlocks.EDHELVIR_CRYSTAL.get()).defaultBlockState();
			GULDURIL_CRYSTAL = ((Block) LOTRBlocks.GULDURIL_CRYSTAL.get()).defaultBlockState();
			GLOWSTONE_CRYSTAL = ((Block) LOTRBlocks.GLOWSTONE_CRYSTAL.get()).defaultBlockState();
			COBWEB = Blocks.COBWEB.defaultBlockState();
			WATER = Blocks.WATER.defaultBlockState();
			LAVA = Blocks.LAVA.defaultBlockState();
			LOTRTrunkPlacers.register();
			LOTRFoliagePlacers.register();
			LOTRTreeDecorators.register();
			OAK_LOG = Blocks.OAK_LOG.defaultBlockState();
			OAK_WOOD = Blocks.OAK_WOOD.defaultBlockState();
			OAK_STRIPPED_LOG = Blocks.STRIPPED_OAK_LOG.defaultBlockState();
			OAK_BRANCH = ((Block) LOTRBlocks.OAK_BRANCH.get()).defaultBlockState();
			OAK_LEAVES = Blocks.OAK_LEAVES.defaultBlockState();
			OAK_TREE_VINES = buildClassicTreeWithVines(OAK_LOG, OAK_LEAVES, 4, 2);
			OAK_TREE_BEES_VINES = buildClassicTreeWithBeesAndVines(OAK_LOG, OAK_LEAVES, 4, 2);
			OAK_TREE_TALL = buildClassicTree(OAK_LOG, OAK_LEAVES, 6, 3);
			OAK_TREE_TALL_BEES = buildClassicTree(OAK_LOG, OAK_LEAVES, 6, 3);
			OAK_TREE_TALL_VINES = buildClassicTreeWithVines(OAK_LOG, OAK_LEAVES, 6, 3);
			OAK_TREE_TALL_BEES_VINES = buildClassicTreeWithBeesAndVines(OAK_LOG, OAK_LEAVES, 6, 3);
			OAK_DESERT = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(OAK_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new DesertFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 2), new DesertTrunkPlacer(3, 2, 0, OAK_WOOD), new TwoLayerFeature(1, 0, 1)).ignoreVines().build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.DESERT);
			OAK_DESERT_BEES = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(OAK_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new DesertFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 2), new DesertTrunkPlacer(3, 2, 0, OAK_WOOD), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(Placements.BEEHIVE_005)).build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.DESERT);
			OAK_DEAD = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(OAK_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new EmptyFoliagePlacer(), new DeadTrunkPlacer(2, 3, 0, OAK_WOOD, OAK_BRANCH), new TwoLayerFeature(1, 0, 1)).build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.DESERT);
			OAK_PARTY = buildNormalPartyTree(OAK_LOG, OAK_WOOD, OAK_BRANCH, OAK_LEAVES);
			OAK_FANGORN = buildFangornTree(OAK_LOG, OAK_WOOD, OAK_STRIPPED_LOG, OAK_LEAVES);
			OAK_SHRUB = buildShrub(OAK_LOG, OAK_LEAVES);
			SPRUCE_LOG = Blocks.SPRUCE_LOG.defaultBlockState();
			SPRUCE_WOOD = Blocks.SPRUCE_WOOD.defaultBlockState();
			SPRUCE_BRANCH = ((Block) LOTRBlocks.SPRUCE_BRANCH.get()).defaultBlockState();
			SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.defaultBlockState();
			SPRUCE_DEAD = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(SPRUCE_LOG), new SimpleBlockStateProvider(SPRUCE_LEAVES), new EmptyFoliagePlacer(), new DeadTrunkPlacer(2, 3, 0, SPRUCE_WOOD, SPRUCE_BRANCH), new TwoLayerFeature(1, 0, 1)).build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.DESERT);
			SPRUCE_SHRUB = buildShrub(SPRUCE_LOG, SPRUCE_LEAVES);
			BIRCH_LOG = Blocks.BIRCH_LOG.defaultBlockState();
			BIRCH_WOOD = Blocks.BIRCH_WOOD.defaultBlockState();
			BIRCH_BRANCH = ((Block) LOTRBlocks.BIRCH_BRANCH.get()).defaultBlockState();
			BIRCH_LEAVES = Blocks.BIRCH_LEAVES.defaultBlockState();
			BIRCH_TREE_FANCY = buildFancyTree(BIRCH_LOG, BIRCH_LEAVES);
			BIRCH_TREE_FANCY_BEES = buildFancyTreeWithBees(BIRCH_LOG, BIRCH_LEAVES);
			BIRCH_TREE_ALT = new Builder(new SimpleBlockStateProvider(BIRCH_LOG), new SimpleBlockStateProvider(BIRCH_LEAVES), new AspenFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(2), FeatureSpread.of(2, 2)), new StraightTrunkPlacer(8, 8, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			BIRCH_TREE_ALT_BEES = new Builder(new SimpleBlockStateProvider(BIRCH_LOG), new SimpleBlockStateProvider(BIRCH_LEAVES), new AspenFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(2), FeatureSpread.of(2, 2)), new StraightTrunkPlacer(8, 8, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(Placements.BEEHIVE_005)).build();
			BIRCH_DEAD = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(BIRCH_LOG), new SimpleBlockStateProvider(BIRCH_LEAVES), new EmptyFoliagePlacer(), new DeadTrunkPlacer(2, 3, 0, BIRCH_WOOD, BIRCH_BRANCH), new TwoLayerFeature(1, 0, 1)).build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.DESERT);
			BIRCH_PARTY = buildNormalPartyTree(BIRCH_LOG, BIRCH_WOOD, BIRCH_BRANCH, BIRCH_LEAVES);
			DARK_OAK_LOG = Blocks.DARK_OAK_LOG.defaultBlockState();
			DARK_OAK_WOOD = Blocks.DARK_OAK_WOOD.defaultBlockState();
			DARK_OAK_BRANCH = ((Block) LOTRBlocks.DARK_OAK_BRANCH.get()).defaultBlockState();
			DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.defaultBlockState();
			DARK_OAK_PARTY = buildNormalHeightPartyTree(DARK_OAK_LOG, DARK_OAK_WOOD, DARK_OAK_BRANCH, DARK_OAK_LEAVES, new DarkOakFoliagePlacer(FeatureSpread.fixed(0), FeatureSpread.fixed(0)));
			DARK_OAK_SHRUB = buildShrub(DARK_OAK_LOG, DARK_OAK_LEAVES);
			PINE_LOG = ((Block) LOTRBlocks.PINE_LOG.get()).defaultBlockState();
			PINE_LOG_SLAB = ((Block) LOTRBlocks.PINE_LOG_SLAB.get()).defaultBlockState();
			PINE_LEAVES = ((Block) LOTRBlocks.PINE_LEAVES.get()).defaultBlockState();
			PINE_SAPLING = (IPlantable) LOTRBlocks.PINE_SAPLING.get();
			BlockStateProvider pineBranchProvider = new WeightedBlockStateProvider().add(PINE_LOG, 1).add(PINE_LOG_SLAB, 2);
			PINE_TREE = new Builder(new SimpleBlockStateProvider(PINE_LOG), new SimpleBlockStateProvider(PINE_LEAVES), new LOTRPineFoliagePlacer(FeatureSpread.of(3, 0), FeatureSpread.fixed(1), FeatureSpread.of(6, 6)), new StraightTrunkPlacer(12, 12, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(new PineBranchDecorator(pineBranchProvider, 0.33F), new PineStripDecorator(0.1F, 0.3F, 0.7F))).build();
			SHIRE_PINE_TREE = new Builder(new SimpleBlockStateProvider(PINE_LOG), new SimpleBlockStateProvider(PINE_LEAVES), new ShirePineFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.fixed(1), FeatureSpread.of(6, 3)), new StraightTrunkPlacer(10, 10, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(new PineBranchDecorator(pineBranchProvider, 0.33F), new PineStripDecorator(0.1F, 0.3F, 0.7F))).build();
			PINE_DEAD = new Builder(new SimpleBlockStateProvider(PINE_LOG), new SimpleBlockStateProvider(PINE_LEAVES), new EmptyFoliagePlacer(), new StraightTrunkPlacer(12, 12, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(new PineBranchDecorator(pineBranchProvider, 0.33F), new PineStripDecorator(0.1F, 0.3F, 0.7F))).build();
			PINE_SHRUB = buildShrub(PINE_LOG, PINE_LEAVES);
			MALLORN_LOG = ((Block) LOTRBlocks.MALLORN_LOG.get()).defaultBlockState();
			MALLORN_WOOD = ((Block) LOTRBlocks.MALLORN_WOOD.get()).defaultBlockState();
			MALLORN_BRANCH = ((Block) LOTRBlocks.MALLORN_BRANCH.get()).defaultBlockState();
			MALLORN_LEAVES = ((Block) LOTRBlocks.MALLORN_LEAVES.get()).defaultBlockState();
			MALLORN_SAPLING = (IPlantable) LOTRBlocks.MALLORN_SAPLING.get();
			MALLORN_TREE = buildClassicTree(MALLORN_LOG, MALLORN_LEAVES, 6, 3);
			MALLORN_TREE_BEES = buildClassicTreeWithBees(MALLORN_LOG, MALLORN_LEAVES, 6, 3);
			MALLORN_TREE_BOUGHS = new Builder(new SimpleBlockStateProvider(MALLORN_LOG), new SimpleBlockStateProvider(MALLORN_LEAVES), new BoughsFoliagePlacer(FeatureSpread.fixed(4), FeatureSpread.fixed(0), 3), new BoughsTrunkPlacer(10, 4, 0, MALLORN_WOOD, MALLORN_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			MALLORN_PARTY = buildPartyTree(MALLORN_LOG, MALLORN_WOOD, MALLORN_BRANCH, MALLORN_LEAVES, 15, 15, new AcaciaFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0)));
			MIRK_OAK_LOG = ((Block) LOTRBlocks.MIRK_OAK_LOG.get()).defaultBlockState();
			MIRK_OAK_WOOD = ((Block) LOTRBlocks.MIRK_OAK_WOOD.get()).defaultBlockState();
			MIRK_OAK_BRANCH = ((Block) LOTRBlocks.MIRK_OAK_BRANCH.get()).defaultBlockState();
			MIRK_OAK_LEAVES = ((Block) LOTRBlocks.MIRK_OAK_LEAVES.get()).defaultBlockState();
			MIRK_OAK_SAPLING = (IPlantable) LOTRBlocks.MIRK_OAK_SAPLING.get();
			List mirkOakDecorators = ImmutableList.of(new MirkOakLeavesGrowthDecorator(), new MirkOakWebsDecorator(true, 0.25F, 0.1F, 0.15F), new MirkOakWebsDecorator(true, 0.05F, 0.35F, 0.2F), new LeaveVineTreeDecorator());
			MIRK_OAK_TREE = new Builder(new SimpleBlockStateProvider(MIRK_OAK_LOG), new SimpleBlockStateProvider(MIRK_OAK_LEAVES), new MirkOakFoliagePlacer(FeatureSpread.of(3, 1), FeatureSpread.fixed(0), 3), new MirkOakTrunkPlacer(4, 5, 0, MIRK_OAK_WOOD, MIRK_OAK_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(mirkOakDecorators).build();
			MIRK_OAK_PARTY = buildMirkPartyTree(MIRK_OAK_LOG, MIRK_OAK_WOOD, MIRK_OAK_BRANCH, MIRK_OAK_LEAVES, mirkOakDecorators);
			MIRK_OAK_SHRUB = buildShrub(MIRK_OAK_LOG, MIRK_OAK_LEAVES);
			CHARRED_LOG = ((Block) LOTRBlocks.CHARRED_LOG.get()).defaultBlockState();
			CHARRED_WOOD = ((Block) LOTRBlocks.CHARRED_WOOD.get()).defaultBlockState();
			CHARRED_BRANCH = ((Block) LOTRBlocks.CHARRED_BRANCH.get()).defaultBlockState();
			CHARRED_TREE = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(CHARRED_LOG), new SimpleBlockStateProvider(OAK_LEAVES), new EmptyFoliagePlacer(), new DeadTrunkPlacer(2, 3, 0, CHARRED_WOOD, CHARRED_BRANCH), new TwoLayerFeature(1, 0, 1)).build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.CHARRED);
			APPLE_LOG = ((Block) LOTRBlocks.APPLE_LOG.get()).defaultBlockState();
			APPLE_LEAVES = ((Block) LOTRBlocks.APPLE_LEAVES.get()).defaultBlockState();
			APPLE_LEAVES_RED = ((Block) LOTRBlocks.APPLE_LEAVES_RED.get()).defaultBlockState();
			APPLE_LEAVES_GREEN = ((Block) LOTRBlocks.APPLE_LEAVES_GREEN.get()).defaultBlockState();
			APPLE_LEAVES_RED_POOL = new WeightedBlockStateProvider().add(APPLE_LEAVES, 15).add(APPLE_LEAVES_RED, 1);
			APPLE_LEAVES_GREEN_POOL = new WeightedBlockStateProvider().add(APPLE_LEAVES, 15).add(APPLE_LEAVES_GREEN, 1);
			APPLE_LEAVES_MIX_POOL = new WeightedBlockStateProvider().add(APPLE_LEAVES, 30).add(APPLE_LEAVES_RED, 1).add(APPLE_LEAVES_GREEN, 1);
			APPLE_SAPLING = (IPlantable) LOTRBlocks.APPLE_SAPLING.get();
			APPLE_TREE_RED = buildClassicTree(APPLE_LOG, APPLE_LEAVES_RED_POOL, 4, 3);
			APPLE_TREE_RED_BEES = buildClassicTreeWithBees(APPLE_LOG, APPLE_LEAVES_RED_POOL, 4, 3);
			APPLE_TREE_GREEN = buildClassicTree(APPLE_LOG, APPLE_LEAVES_GREEN_POOL, 4, 3);
			APPLE_TREE_GREEN_BEES = buildClassicTreeWithBees(APPLE_LOG, APPLE_LEAVES_GREEN_POOL, 4, 3);
			APPLE_TREE_MIX = buildClassicTree(APPLE_LOG, APPLE_LEAVES_MIX_POOL, 4, 3);
			APPLE_TREE_MIX_BEES = buildClassicTreeWithBees(APPLE_LOG, APPLE_LEAVES_MIX_POOL, 4, 3);
			PEAR_LOG = ((Block) LOTRBlocks.PEAR_LOG.get()).defaultBlockState();
			PEAR_LEAVES = ((Block) LOTRBlocks.PEAR_LEAVES.get()).defaultBlockState();
			PEAR_LEAVES_FRUIT = ((Block) LOTRBlocks.PEAR_LEAVES_FRUIT.get()).defaultBlockState();
			PEAR_LEAVES_POOL = new WeightedBlockStateProvider().add(PEAR_LEAVES, 15).add(PEAR_LEAVES_FRUIT, 1);
			PEAR_SAPLING = (IPlantable) LOTRBlocks.PEAR_SAPLING.get();
			PEAR_TREE = buildClassicTree(PEAR_LOG, PEAR_LEAVES_POOL, 4, 1);
			PEAR_TREE_BEES = buildClassicTreeWithBees(PEAR_LOG, PEAR_LEAVES_POOL, 4, 1);
			CHERRY_LOG = ((Block) LOTRBlocks.CHERRY_LOG.get()).defaultBlockState();
			CHERRY_LEAVES = ((Block) LOTRBlocks.CHERRY_LEAVES.get()).defaultBlockState();
			CHERRY_LEAVES_FRUIT = ((Block) LOTRBlocks.CHERRY_LEAVES_FRUIT.get()).defaultBlockState();
			CHERRY_LEAVES_POOL = new WeightedBlockStateProvider().add(CHERRY_LEAVES, 7).add(CHERRY_LEAVES_FRUIT, 1);
			CHERRY_SAPLING = (IPlantable) LOTRBlocks.CHERRY_SAPLING.get();
			CHERRY_TREE = buildClassicTree(CHERRY_LOG, CHERRY_LEAVES_POOL, 4, 4);
			CHERRY_TREE_BEES = buildClassicTreeWithBees(CHERRY_LOG, CHERRY_LEAVES_POOL, 4, 4);
			LEBETHRON_LOG = ((Block) LOTRBlocks.LEBETHRON_LOG.get()).defaultBlockState();
			LEBETHRON_WOOD = ((Block) LOTRBlocks.LEBETHRON_WOOD.get()).defaultBlockState();
			LEBETHRON_BRANCH = ((Block) LOTRBlocks.LEBETHRON_BRANCH.get()).defaultBlockState();
			LEBETHRON_LEAVES = ((Block) LOTRBlocks.LEBETHRON_LEAVES.get()).defaultBlockState();
			LEBETHRON_SAPLING = (IPlantable) LOTRBlocks.LEBETHRON_SAPLING.get();
			LEBETHRON_TREE = buildClassicTree(LEBETHRON_LOG, LEBETHRON_LEAVES, 5, 4);
			LEBETHRON_TREE_BEES = buildClassicTreeWithBees(LEBETHRON_LOG, LEBETHRON_LEAVES, 5, 4);
			LEBETHRON_TREE_FANCY = buildFancyTree(LEBETHRON_LOG, LEBETHRON_LEAVES);
			LEBETHRON_TREE_FANCY_BEES = buildFancyTreeWithBees(LEBETHRON_LOG, LEBETHRON_LEAVES);
			LEBETHRON_PARTY = buildNormalPartyTree(LEBETHRON_LOG, LEBETHRON_WOOD, LEBETHRON_BRANCH, LEBETHRON_LEAVES);
			BEECH_LOG = ((Block) LOTRBlocks.BEECH_LOG.get()).defaultBlockState();
			BEECH_WOOD = ((Block) LOTRBlocks.BEECH_WOOD.get()).defaultBlockState();
			BEECH_STRIPPED_LOG = ((Block) LOTRBlocks.STRIPPED_BEECH_LOG.get()).defaultBlockState();
			BEECH_BRANCH = ((Block) LOTRBlocks.BEECH_BRANCH.get()).defaultBlockState();
			BEECH_LEAVES = ((Block) LOTRBlocks.BEECH_LEAVES.get()).defaultBlockState();
			BEECH_SAPLING = (IPlantable) LOTRBlocks.BEECH_SAPLING.get();
			BEECH_TREE = buildClassicTree(BEECH_LOG, BEECH_LEAVES, 5, 4);
			BEECH_TREE_BEES = buildClassicTreeWithBees(BEECH_LOG, BEECH_LEAVES, 5, 4);
			BEECH_TREE_FANCY = buildFancyTree(BEECH_LOG, BEECH_LEAVES);
			BEECH_TREE_FANCY_BEES = buildFancyTreeWithBees(BEECH_LOG, BEECH_LEAVES);
			BEECH_PARTY = buildNormalPartyTree(BEECH_LOG, BEECH_WOOD, BEECH_BRANCH, BEECH_LEAVES);
			BEECH_FANGORN = buildFangornTree(BEECH_LOG, BEECH_WOOD, BEECH_STRIPPED_LOG, BEECH_LEAVES);
			BEECH_DEAD = new WrappedTreeFeatureConfig(new Builder(new SimpleBlockStateProvider(BEECH_LOG), new SimpleBlockStateProvider(BEECH_LEAVES), new EmptyFoliagePlacer(), new DeadTrunkPlacer(2, 3, 0, BEECH_WOOD, BEECH_BRANCH), new TwoLayerFeature(1, 0, 1)).build(), WrappedTreeFeatureConfig.AlternativeTreeSoil.DESERT);
			MAPLE_LOG = ((Block) LOTRBlocks.MAPLE_LOG.get()).defaultBlockState();
			MAPLE_WOOD = ((Block) LOTRBlocks.MAPLE_WOOD.get()).defaultBlockState();
			MAPLE_BRANCH = ((Block) LOTRBlocks.MAPLE_BRANCH.get()).defaultBlockState();
			MAPLE_LEAVES = ((Block) LOTRBlocks.MAPLE_LEAVES.get()).defaultBlockState();
			MAPLE_SAPLING = (IPlantable) LOTRBlocks.MAPLE_SAPLING.get();
			MAPLE_TREE = buildClassicTree(MAPLE_LOG, MAPLE_LEAVES, 4, 4);
			MAPLE_TREE_BEES = buildClassicTreeWithBees(MAPLE_LOG, MAPLE_LEAVES, 4, 4);
			MAPLE_TREE_FANCY = buildFancyTree(MAPLE_LOG, MAPLE_LEAVES);
			MAPLE_TREE_FANCY_BEES = buildFancyTreeWithBees(MAPLE_LOG, MAPLE_LEAVES);
			MAPLE_PARTY = buildNormalPartyTree(MAPLE_LOG, MAPLE_WOOD, MAPLE_BRANCH, MAPLE_LEAVES);
			ASPEN_LOG = ((Block) LOTRBlocks.ASPEN_LOG.get()).defaultBlockState();
			ASPEN_LEAVES = ((Block) LOTRBlocks.ASPEN_LEAVES.get()).defaultBlockState();
			ASPEN_SAPLING = (IPlantable) LOTRBlocks.ASPEN_SAPLING.get();
			ASPEN_TREE = new Builder(new SimpleBlockStateProvider(ASPEN_LOG), new SimpleBlockStateProvider(ASPEN_LEAVES), new AspenFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(2), FeatureSpread.of(2, 2)), new StraightTrunkPlacer(8, 7, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			LAIRELOSSE_LOG = ((Block) LOTRBlocks.LAIRELOSSE_LOG.get()).defaultBlockState();
			LAIRELOSSE_LEAVES = ((Block) LOTRBlocks.LAIRELOSSE_LEAVES.get()).defaultBlockState();
			LAIRELOSSE_SAPLING = (IPlantable) LOTRBlocks.LAIRELOSSE_SAPLING.get();
			LAIRELOSSE_TREE = new Builder(new SimpleBlockStateProvider(LAIRELOSSE_LOG), new SimpleBlockStateProvider(LAIRELOSSE_LEAVES), new LairelosseFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(1), FeatureSpread.of(1, 2)), new StraightTrunkPlacer(5, 3, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			CEDAR_LOG = ((Block) LOTRBlocks.CEDAR_LOG.get()).defaultBlockState();
			CEDAR_WOOD = ((Block) LOTRBlocks.CEDAR_WOOD.get()).defaultBlockState();
			CEDAR_BRANCH = ((Block) LOTRBlocks.CEDAR_BRANCH.get()).defaultBlockState();
			CEDAR_LEAVES = ((Block) LOTRBlocks.CEDAR_LEAVES.get()).defaultBlockState();
			CEDAR_SAPLING = (IPlantable) LOTRBlocks.CEDAR_SAPLING.get();
			CEDAR_TREE = new Builder(new SimpleBlockStateProvider(CEDAR_LOG), new SimpleBlockStateProvider(CEDAR_LEAVES), new CedarFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new CedarTrunkPlacer(10, 6, 0, CEDAR_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			CEDAR_TREE_LARGE = new Builder(new SimpleBlockStateProvider(CEDAR_LOG), new SimpleBlockStateProvider(CEDAR_LEAVES), new CedarFoliagePlacer(FeatureSpread.fixed(2), FeatureSpread.fixed(0), 3), new CedarTrunkPlacer(15, 15, 0, CEDAR_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			FIR_LOG = ((Block) LOTRBlocks.FIR_LOG.get()).defaultBlockState();
			FIR_LEAVES = ((Block) LOTRBlocks.FIR_LEAVES.get()).defaultBlockState();
			FIR_SAPLING = (IPlantable) LOTRBlocks.FIR_SAPLING.get();
			FIR_TREE = new Builder(new SimpleBlockStateProvider(FIR_LOG), new SimpleBlockStateProvider(FIR_LEAVES), new FirFoliagePlacer(FeatureSpread.of(3, 0), FeatureSpread.fixed(2), FeatureSpread.of(7, 4)), new StraightTrunkPlacer(6, 7, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			FIR_SHRUB = buildShrub(FIR_LOG, FIR_LEAVES);
			LARCH_LOG = ((Block) LOTRBlocks.LARCH_LOG.get()).defaultBlockState();
			LARCH_LEAVES = ((Block) LOTRBlocks.LARCH_LEAVES.get()).defaultBlockState();
			LARCH_SAPLING = (IPlantable) LOTRBlocks.LARCH_SAPLING.get();
			LARCH_TREE = new Builder(new SimpleBlockStateProvider(LARCH_LOG), new SimpleBlockStateProvider(LARCH_LEAVES), new SpruceFoliagePlacer(FeatureSpread.of(2, 1), FeatureSpread.of(0, 2), FeatureSpread.of(2, 1)), new StraightTrunkPlacer(8, 8, 0), new TwoLayerFeature(2, 0, 2)).ignoreVines().build();
			HOLLY_LOG = ((Block) LOTRBlocks.HOLLY_LOG.get()).defaultBlockState();
			HOLLY_LEAVES = ((Block) LOTRBlocks.HOLLY_LEAVES.get()).defaultBlockState();
			HOLLY_SAPLING = (IPlantable) LOTRBlocks.HOLLY_SAPLING.get();
			HOLLY_TREE = new Builder(new SimpleBlockStateProvider(HOLLY_LOG), new SimpleBlockStateProvider(HOLLY_LEAVES), new HollyFoliagePlacer(FeatureSpread.of(2, 0), FeatureSpread.fixed(0), FeatureSpread.of(1, 2)), new StraightTrunkPlacer(9, 5, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			HOLLY_TREE_BEES = new Builder(new SimpleBlockStateProvider(HOLLY_LOG), new SimpleBlockStateProvider(HOLLY_LEAVES), new HollyFoliagePlacer(FeatureSpread.of(2, 0), FeatureSpread.fixed(0), FeatureSpread.of(1, 2)), new StraightTrunkPlacer(9, 5, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(Placements.BEEHIVE_005)).build();
			GREEN_OAK_LOG = ((Block) LOTRBlocks.GREEN_OAK_LOG.get()).defaultBlockState();
			GREEN_OAK_WOOD = ((Block) LOTRBlocks.GREEN_OAK_WOOD.get()).defaultBlockState();
			GREEN_OAK_BRANCH = ((Block) LOTRBlocks.GREEN_OAK_BRANCH.get()).defaultBlockState();
			GREEN_OAK_LEAVES = ((Block) LOTRBlocks.GREEN_OAK_LEAVES.get()).defaultBlockState();
			GREEN_OAK_SAPLING = (IPlantable) LOTRBlocks.GREEN_OAK_SAPLING.get();
			RED_OAK_LEAVES = ((Block) LOTRBlocks.RED_OAK_LEAVES.get()).defaultBlockState();
			RED_OAK_SAPLING = (IPlantable) LOTRBlocks.RED_OAK_SAPLING.get();
			GREEN_OAK_TREE = new Builder(new SimpleBlockStateProvider(GREEN_OAK_LOG), new SimpleBlockStateProvider(GREEN_OAK_LEAVES), new MirkOakFoliagePlacer(FeatureSpread.fixed(3), FeatureSpread.fixed(0), 3), new MirkOakTrunkPlacer(4, 4, 0, GREEN_OAK_WOOD, GREEN_OAK_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			GREEN_OAK_TREE_BEES = new Builder(new SimpleBlockStateProvider(GREEN_OAK_LOG), new SimpleBlockStateProvider(GREEN_OAK_LEAVES), new MirkOakFoliagePlacer(FeatureSpread.fixed(3), FeatureSpread.fixed(0), 3), new MirkOakTrunkPlacer(4, 4, 0, GREEN_OAK_WOOD, GREEN_OAK_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(Placements.BEEHIVE_005)).build();
			RED_OAK_TREE = new Builder(new SimpleBlockStateProvider(GREEN_OAK_LOG), new SimpleBlockStateProvider(RED_OAK_LEAVES), new MirkOakFoliagePlacer(FeatureSpread.of(3, 1), FeatureSpread.fixed(0), 3), new MirkOakTrunkPlacer(6, 3, 0, GREEN_OAK_WOOD, GREEN_OAK_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			RED_OAK_TREE_BEES = new Builder(new SimpleBlockStateProvider(GREEN_OAK_LOG), new SimpleBlockStateProvider(RED_OAK_LEAVES), new MirkOakFoliagePlacer(FeatureSpread.of(3, 1), FeatureSpread.fixed(0), 3), new MirkOakTrunkPlacer(6, 3, 0, GREEN_OAK_WOOD, GREEN_OAK_BRANCH), new TwoLayerFeature(1, 0, 1)).ignoreVines().decorators(ImmutableList.of(Placements.BEEHIVE_005)).build();
			GREEN_OAK_PARTY = buildMirkPartyTree(GREEN_OAK_LOG, GREEN_OAK_WOOD, GREEN_OAK_BRANCH, GREEN_OAK_LEAVES);
			RED_OAK_PARTY = buildMirkPartyTree(GREEN_OAK_LOG, GREEN_OAK_WOOD, GREEN_OAK_BRANCH, RED_OAK_LEAVES);
			GREEN_OAK_SHRUB = buildShrub(GREEN_OAK_LOG, GREEN_OAK_LEAVES);
			CYPRESS_LOG = ((Block) LOTRBlocks.CYPRESS_LOG.get()).defaultBlockState();
			CYPRESS_LEAVES = ((Block) LOTRBlocks.CYPRESS_LEAVES.get()).defaultBlockState();
			CYPRESS_SAPLING = (IPlantable) LOTRBlocks.CYPRESS_SAPLING.get();
			CYPRESS_TREE = new Builder(new SimpleBlockStateProvider(CYPRESS_LOG), new SimpleBlockStateProvider(CYPRESS_LEAVES), new CypressFoliagePlacer(FeatureSpread.fixed(1), FeatureSpread.fixed(1), FeatureSpread.of(3, 1)), new StraightTrunkPlacer(8, 5, 0), new TwoLayerFeature(1, 0, 1)).ignoreVines().build();
			CULUMALDA_LOG = ((Block) LOTRBlocks.CULUMALDA_LOG.get()).defaultBlockState();
			CULUMALDA_LEAVES = ((Block) LOTRBlocks.CULUMALDA_LEAVES.get()).defaultBlockState();
			CULUMALDA_SAPLING = (IPlantable) LOTRBlocks.CULUMALDA_SAPLING.get();
			CULUMALDA_TREE = buildClassicTreeWithSpecifiedFoliage(CULUMALDA_LOG, new SimpleBlockStateProvider(CULUMALDA_LEAVES), 5, 4, new CulumaldaFoliagePlacer(FeatureSpread.of(3, 0), FeatureSpread.fixed(0), 4), false, false);
			CULUMALDA_TREE_BEES = buildClassicTreeWithSpecifiedFoliage(CULUMALDA_LOG, new SimpleBlockStateProvider(CULUMALDA_LEAVES), 5, 4, new CulumaldaFoliagePlacer(FeatureSpread.of(3, 0), FeatureSpread.fixed(0), 4), true, false);
			SIMBELMYNE = ((Block) LOTRBlocks.SIMBELMYNE.get()).defaultBlockState();
			ATHELAS = ((Block) LOTRBlocks.ATHELAS.get()).defaultBlockState();
			WILD_PIPEWEED = ((Block) LOTRBlocks.WILD_PIPEWEED.get()).defaultBlockState();
			SIMBELMYNE_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(SIMBELMYNE), new SimpleBlockPlacer()).tries(64).build();
			ATHELAS_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ATHELAS), new SimpleBlockPlacer()).tries(64).build();
			WILD_PIPEWEED_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(WILD_PIPEWEED), new SimpleBlockPlacer()).tries(64).build();
			LILAC = Blocks.LILAC.defaultBlockState();
			ROSE_BUSH = Blocks.ROSE_BUSH.defaultBlockState();
			PEONY = Blocks.PEONY.defaultBlockState();
			SUNFLOWER = Blocks.SUNFLOWER.defaultBlockState();
			HIBISCUS = ((Block) LOTRBlocks.HIBISCUS.get()).defaultBlockState();
			FLAME_OF_HARAD = ((Block) LOTRBlocks.FLAME_OF_HARAD.get()).defaultBlockState();
			LILAC_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(LILAC), new DoublePlantBlockPlacer()).tries(64).noProjection().build();
			ROSE_BUSH_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ROSE_BUSH), new DoublePlantBlockPlacer()).tries(64).noProjection().build();
			PEONY_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(PEONY), new DoublePlantBlockPlacer()).tries(64).noProjection().build();
			SUNFLOWER_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(SUNFLOWER), new DoublePlantBlockPlacer()).tries(64).noProjection().build();
			HIBISCUS_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(HIBISCUS), new DoublePlantBlockPlacer()).tries(64).noProjection().build();
			FLAME_OF_HARAD_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(FLAME_OF_HARAD), new DoublePlantBlockPlacer()).tries(64).noProjection().build();
			DEAD_BUSH = Blocks.DEAD_BUSH.defaultBlockState();
			DEAD_BUSH_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(DEAD_BUSH), new SimpleBlockPlacer()).tries(4).build();
			CACTUS = Blocks.CACTUS.defaultBlockState();
			CACTUS_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(CACTUS), new ColumnBlockPlacer(1, 2)).tries(10).noProjection().build();
			SAND = Blocks.SAND.defaultBlockState();
			RED_SAND = Blocks.RED_SAND.defaultBlockState();
			WHITE_SAND = ((Block) LOTRBlocks.WHITE_SAND.get()).defaultBlockState();
			CLAY = Blocks.CLAY.defaultBlockState();
			QUAGMIRE = ((Block) LOTRBlocks.QUAGMIRE.get()).defaultBlockState();
			GRASS_BLOCK = Blocks.GRASS_BLOCK.defaultBlockState();
			BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.defaultBlockState();
			RED_MUSHROOM = Blocks.RED_MUSHROOM.defaultBlockState();
			MIRK_SHROOM = ((Block) LOTRBlocks.MIRK_SHROOM.get()).defaultBlockState();
			BROWN_MUSHROOM_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(BROWN_MUSHROOM), new SimpleBlockPlacer()).tries(64).noProjection().build();
			RED_MUSHROOM_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(RED_MUSHROOM), new SimpleBlockPlacer()).tries(64).noProjection().build();
			MIRK_SHROOM_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(MIRK_SHROOM), new SimpleBlockPlacer()).tries(64).noProjection().build();
			SUGAR_CANE = Blocks.SUGAR_CANE.defaultBlockState();
			SUGAR_CANE_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(SUGAR_CANE), new ColumnBlockPlacer(2, 2)).tries(20).xspread(4).yspread(0).zspread(4).noProjection().needWater().build();
			REEDS = ((Block) LOTRBlocks.REEDS.get()).defaultBlockState();
			DRIED_REEDS = ((Block) LOTRBlocks.DRIED_REEDS.get()).defaultBlockState();
			REEDS_CONFIG_FOR_DRIED_CHANCE = driedChance -> {
				float freshChance = 1.0F - (float) driedChance;
				int weight = 1000;
				WeightedBlockStateProvider blockProv = new WeightedBlockStateProvider().add(REEDS, (int) (freshChance * weight)).add(DRIED_REEDS, (int) ((float) driedChance * weight));
				return new ReedsFeatureConfig(blockProv, 32, 5, 2, 5, 0.75F);
			};
			PAPYRUS = ((Block) LOTRBlocks.PAPYRUS.get()).defaultBlockState();
			PAPYRUS_CONFIG = new ReedsFeatureConfig(new SimpleBlockStateProvider(PAPYRUS), 32, 5, 2, 5, 0.75F);
			RUSHES = ((Block) LOTRBlocks.RUSHES.get()).defaultBlockState();
			RUSHES_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(RUSHES), new DoublePlantBlockPlacer()).tries(64).noProjection().canReplace().build();
			PUMPKIN = Blocks.PUMPKIN.defaultBlockState();
			PUMPKIN_PATCH_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(PUMPKIN), new SimpleBlockPlacer()).tries(64).whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock())).noProjection().build();
			LILY_PAD = Blocks.LILY_PAD.defaultBlockState();
			LILY_PAD_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(LILY_PAD), new SimpleBlockPlacer()).tries(10).build();
			WHITE_WATER_LILY = ((Block) LOTRBlocks.WHITE_WATER_LILY.get()).defaultBlockState();
			YELLOW_WATER_LILY = ((Block) LOTRBlocks.YELLOW_WATER_LILY.get()).defaultBlockState();
			PURPLE_WATER_LILY = ((Block) LOTRBlocks.PURPLE_WATER_LILY.get()).defaultBlockState();
			PINK_WATER_LILY = ((Block) LOTRBlocks.PINK_WATER_LILY.get()).defaultBlockState();
			LILY_PAD_WITH_FLOWERS_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new WeightedBlockStateProvider().add(LILY_PAD, 5).add(WHITE_WATER_LILY, 1).add(YELLOW_WATER_LILY, 1).add(PURPLE_WATER_LILY, 1).add(PINK_WATER_LILY, 1), new SimpleBlockPlacer()).tries(10).build();
			LILY_PAD_WITH_RARE_FLOWERS_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new WeightedBlockStateProvider().add(LILY_PAD, 80).add(WHITE_WATER_LILY, 1).add(YELLOW_WATER_LILY, 1).add(PURPLE_WATER_LILY, 1).add(PINK_WATER_LILY, 1), new SimpleBlockPlacer()).tries(10).build();
			SPONGE = Blocks.WET_SPONGE.defaultBlockState();
			SPONGE_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(SPONGE), new SimpleBlockPlacer()).tries(64).whitelist(ImmutableSet.of(SAND.getBlock(), GRAVEL.getBlock())).canReplace().noProjection().build();
			SWEET_BERRY_BUSH = Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3);
			SWEET_BERRY_BUSH_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(SWEET_BERRY_BUSH), new SimpleBlockPlacer()).tries(64).whitelist(ImmutableSet.of(GRASS_BLOCK.getBlock())).noProjection().build();
			MORDOR_MOSS = ((Block) LOTRBlocks.MORDOR_MOSS.get()).defaultBlockState();
			MORDOR_MOSS_CONFIG = new MordorMossFeatureConfig(MORDOR_MOSS, 32, 80);
			MORDOR_GRASS = ((Block) LOTRBlocks.MORDOR_GRASS.get()).defaultBlockState();
			MORDOR_GRASS_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(MORDOR_GRASS), new SimpleBlockPlacer()).tries(32).build();
			MORDOR_THORN = ((Block) LOTRBlocks.MORDOR_THORN.get()).defaultBlockState();
			MORDOR_THORN_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(MORDOR_THORN), new SimpleBlockPlacer()).xspread(6).yspread(2).zspread(6).tries(160).build();
			MORGUL_SHROOM = ((Block) LOTRBlocks.MORGUL_SHROOM.get()).defaultBlockState();
			MORGUL_SHROOM_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(MORGUL_SHROOM), new SimpleBlockPlacer()).tries(64).noProjection().build();
			MORGUL_FLOWER = ((Block) LOTRBlocks.MORGUL_FLOWER.get()).defaultBlockState();
			MORGUL_FLOWER_CONFIG = new net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(MORGUL_FLOWER), new SimpleBlockPlacer()).tries(64).build();
			Set springStoneSet = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, LOTRBlocks.GONDOR_ROCK.get(), LOTRBlocks.MORDOR_ROCK.get(), (Block) LOTRBlocks.ROHAN_ROCK.get(), (Block) LOTRBlocks.BLUE_ROCK.get(), (Block) LOTRBlocks.RED_ROCK.get(), (Block) LOTRBlocks.CHALK.get(), (Block) LOTRBlocks.DIRTY_CHALK.get());
			WATER_SPRING_CONFIG = new LiquidsConfig(Fluids.WATER.defaultFluidState(), true, 4, 1, springStoneSet);
			LAVA_SPRING_CONFIG = new LiquidsConfig(Fluids.LAVA.defaultFluidState(), true, 4, 1, springStoneSet);
		}
	}

	public static ConfiguredFeature shirePine() {
		return Feature.TREE.configured(SHIRE_PINE_TREE);
	}

	public static ConfiguredFeature snowWrapTree(ConfiguredFeature tree) {
		IFeatureConfig config = tree.config();
		BaseTreeFeatureConfig treeConfig;
		if (config instanceof BaseTreeFeatureConfig) {
			treeConfig = (BaseTreeFeatureConfig) config;
		} else {
			if (!(config instanceof WrappedTreeFeatureConfig)) {
				throw new IllegalArgumentException("Cannot wrap the supplied ConfiguredFeature type (" + tree + ") in a snowy wrapped tree config");
			}

			treeConfig = ((WrappedTreeFeatureConfig) config).treeConfig;
		}

		return LOTRFeatures.WRAPPED_TREE.configured(new WrappedTreeFeatureConfig(treeConfig, WrappedTreeFeatureConfig.AlternativeTreeSoil.SNOWY));
	}

	public static ConfiguredFeature spruce() {
		return Features.SPRUCE;
	}

	public static ConfiguredFeature spruceDead() {
		return LOTRFeatures.WRAPPED_TREE.configured(SPRUCE_DEAD);
	}

	public static ConfiguredFeature spruceMega() {
		return Features.MEGA_SPRUCE;
	}

	public static ConfiguredFeature spruceShrub() {
		return Feature.TREE.configured(SPRUCE_SHRUB);
	}

	public static ConfiguredFeature spruceThin() {
		return Features.PINE;
	}

	public static ConfiguredFeature spruceThinMega() {
		return Features.MEGA_PINE;
	}
}
