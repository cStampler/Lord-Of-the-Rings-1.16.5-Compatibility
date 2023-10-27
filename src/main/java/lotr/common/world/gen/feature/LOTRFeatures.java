package lotr.common.world.gen.feature;

import lotr.common.init.LOTRTags;
import lotr.common.init.RegistryOrderHelper;
import lotr.common.world.gen.placement.AtSurfaceLayerLimitedWithExtra;
import lotr.common.world.gen.placement.AtSurfaceLayerLimitedWithExtraConfig;
import lotr.common.world.gen.placement.ByWater;
import lotr.common.world.gen.placement.ByWaterConfig;
import lotr.common.world.gen.placement.TreeClusters;
import lotr.common.world.gen.placement.TreeClustersConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockStateProvidingFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRFeatures {
	public static final DeferredRegister FEATURES;
	public static final DeferredRegister PLACEMENTS;
	public static final WeightedRandomFeature WEIGHTED_RANDOM;
	public static final MordorMossFeature MORDOR_MOSS;
	public static final BoulderFeature BOULDER;
	public static final DripstoneFeature DRIPSTONE;
	public static final CobwebFeature COBWEBS;
	public static final TerrainSharpenFeature TERRAIN_SHARPEN;
	public static final GrassPatchFeature GRASS_PATCH;
	public static final TreeTorchesFeature TREE_TORCHES;
	public static final CraftingMonumentFeature CRAFTING_MONUMENT;
	public static final CrystalFeature CRYSTALS;
	public static final LatitudeBasedFeature LATITUDE_BASED;
	public static final LeafBushesFeature LEAF_BUSHES;
	public static final FallenLogFeature FALLEN_LOG;
	public static final ReedsFeature REEDS;
	public static final UnderwaterSpongeFeature UNDERWATER_SPONGE;
	public static final FallenLeavesFeature FALLEN_LEAVES;
	public static final WrappedTreeFeature WRAPPED_TREE;
	public static final MordorBasaltFeature MORDOR_BASALT;
	public static final AtSurfaceLayerLimitedWithExtra COUNT_EXTRA_HEIGHTMAP_LIMITED;
	public static final TreeClusters TREE_CLUSTERS;
	public static final ByWater BY_WATER;

	static {
		FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, "lotr");
		PLACEMENTS = DeferredRegister.create(ForgeRegistries.DECORATORS, "lotr");
		WEIGHTED_RANDOM = (WeightedRandomFeature) preRegFeature("weighted_random", new WeightedRandomFeature(WeightedRandomFeatureConfig.CODEC));
		MORDOR_MOSS = (MordorMossFeature) preRegFeature("mordor_moss", new MordorMossFeature(MordorMossFeatureConfig.CODEC));
		BOULDER = (BoulderFeature) preRegFeature("boulder", new BoulderFeature(BoulderFeatureConfig.CODEC));
		DRIPSTONE = (DripstoneFeature) preRegFeature("dripstone", new DripstoneFeature(DripstoneFeatureConfig.CODEC));
		COBWEBS = (CobwebFeature) preRegFeature("cobwebs", new CobwebFeature(CobwebFeatureConfig.CODEC));
		TERRAIN_SHARPEN = (TerrainSharpenFeature) preRegFeature("terrain_sharpen", new TerrainSharpenFeature(TerrainSharpenFeatureConfig.CODEC));
		GRASS_PATCH = (GrassPatchFeature) preRegFeature("grass_patch", new GrassPatchFeature(GrassPatchFeatureConfig.CODEC));
		TREE_TORCHES = (TreeTorchesFeature) preRegFeature("tree_torches", new TreeTorchesFeature(BlockStateProvidingFeatureConfig.CODEC));
		CRAFTING_MONUMENT = (CraftingMonumentFeature) preRegFeature("crafting_monument", new CraftingMonumentFeature(CraftingMonumentFeatureConfig.CODEC));
		CRYSTALS = (CrystalFeature) preRegFeature("crystals", new CrystalFeature(CrystalFeatureConfig.CODEC));
		LATITUDE_BASED = (LatitudeBasedFeature) preRegFeature("latitude_based", new LatitudeBasedFeature(LatitudeBasedFeatureConfig.CODEC));
		LEAF_BUSHES = (LeafBushesFeature) preRegFeature("leaf_bushes", new LeafBushesFeature(NoFeatureConfig.CODEC));
		FALLEN_LOG = (FallenLogFeature) preRegFeature("fallen_log", new FallenLogFeature(FallenLogFeatureConfig.CODEC));
		REEDS = (ReedsFeature) preRegFeature("reeds", new ReedsFeature(ReedsFeatureConfig.CODEC));
		UNDERWATER_SPONGE = (UnderwaterSpongeFeature) preRegFeature("underwater_sponge", new UnderwaterSpongeFeature(BlockClusterFeatureConfig.CODEC));
		FALLEN_LEAVES = (FallenLeavesFeature) preRegFeature("fallen_leaves", new FallenLeavesFeature(NoFeatureConfig.CODEC));
		WRAPPED_TREE = (WrappedTreeFeature) preRegFeature("wrapped_tree", new WrappedTreeFeature(WrappedTreeFeatureConfig.CODEC));
		MORDOR_BASALT = (MordorBasaltFeature) preRegFeature("mordor_basalt", new MordorBasaltFeature(MordorBasaltFeatureConfig.CODEC));
		COUNT_EXTRA_HEIGHTMAP_LIMITED = (AtSurfaceLayerLimitedWithExtra) preRegPlacement("count_extra_heightmap_limited", new AtSurfaceLayerLimitedWithExtra(AtSurfaceLayerLimitedWithExtraConfig.CODEC));
		TREE_CLUSTERS = (TreeClusters) preRegPlacement("tree_clusters", new TreeClusters(TreeClustersConfig.CODEC));
		BY_WATER = (ByWater) preRegPlacement("by_water", new ByWater(ByWaterConfig.CODEC));
	}

	public static BlockState getBlockStateInContext(BlockState state, IWorld world, BlockPos pos) {
		return Block.updateFromNeighbourShapes(state, world, pos);
	}

	public static final WeightedRandomFeature getWeightedRandom() {
		return WEIGHTED_RANDOM;
	}

	public static boolean isSurfaceBlock(IWorld world, BlockPos pos) {
		return isSurfaceBlock(world, pos, 0);
	}

	private static boolean isSurfaceBlock(IWorld world, BlockPos pos, int recursion) {
		if (world.getBlockState(pos.above()).getMaterial().isLiquid()) {
			return false;
		}
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		Biome biome = world.getBiome(pos);
		ConfiguredSurfaceBuilder surface = biome.getGenerationSettings().getSurfaceBuilder().get();
		if (block == surface.config.getTopMaterial().getBlock() || block == surface.config.getUnderMaterial().getBlock()) {
			return true;
		}
		if (block.is(BlockTags.SAND) || block.is(BlockTags.VALID_SPAWN) || block.is(Blocks.GRAVEL) || block.is(Blocks.DIRT)) {
			return true;
		}
		if (block.is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES)) {
			return true;
		}
		return block == net.minecraft.block.Blocks.STONE && recursion <= 1 && isSurfaceBlock(world, pos.below(), recursion + 1);
	}

	private static Feature preRegFeature(String name, Feature feature) {
		return (Feature) RegistryOrderHelper.preRegObject(FEATURES, name, feature);
	}

	private static Placement preRegPlacement(String name, Placement placement) {
		return (Placement) RegistryOrderHelper.preRegObject(PLACEMENTS, name, placement);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		FEATURES.register(bus);
		PLACEMENTS.register(bus);
	}

	public static void setGrassToDirtBelow(IWorld world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		belowState.getBlock().onPlantGrow(belowState, world, belowPos, pos);
	}

	public static void setGrassToDirtBelowDuringChunkGen(IChunk chunk, BlockPos pos) {
		BlockPos belowPos = pos.below();
		if (chunk.getBlockState(belowPos).is(Blocks.DIRT)) {
			chunk.setBlockState(belowPos, net.minecraft.block.Blocks.DIRT.defaultBlockState(), false);
		}

	}
}
