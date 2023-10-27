package lotr.common.world.map;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.RegistryObject;

public class RoadBlockProvider {
	public static final WeightedBlockStateProvider FAILSAFE_PROVIDER;
	public static final RoadBlockProvider PATH;
	public static final RoadBlockProvider PAVED_PATH;
	public static final RoadBlockProvider CHALK_PATH;
	public static final RoadBlockProvider DIRT;
	public static final RoadBlockProvider COBBLESTONE;
	public static final RoadBlockProvider SMOOTH_STONE;
	public static final RoadBlockProvider STONE_BRICK;
	public static final RoadBlockProvider DRYSTONE;
	public static final RoadBlockProvider GONDOR;
	public static final RoadBlockProvider DOL_AMROTH;
	public static final RoadBlockProvider ROHAN;
	public static final RoadBlockProvider ARNOR;
	public static final RoadBlockProvider HIGH_ELVEN;
	public static final RoadBlockProvider WOOD_ELVEN;
	public static final RoadBlockProvider MIRKWOOD_PATH;
	public static final RoadBlockProvider MORDOR_BRICK;
	public static final RoadBlockProvider MORDOR_PATH;
	public static final RoadBlockProvider NURN_PATH;
	public static final RoadBlockProvider DWARVEN;
	public static final RoadBlockProvider DALE;
	public static final RoadBlockProvider DORWINION;
	public static final RoadBlockProvider DORWINION_PATH;
	public static final RoadBlockProvider HARAD;
	public static final RoadBlockProvider HARAD_PATH;
	public static final RoadBlockProvider UMBAR;
	public static final LazyOptional STANDARD_HEDGE;
	static {
		FAILSAFE_PROVIDER = new WeightedBlockStateProvider().add(Blocks.COBBLESTONE.defaultBlockState(), 1);
		PATH = makeFromProviders(() -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 12).add(Blocks.COARSE_DIRT.defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 12).add(Blocks.COARSE_DIRT.defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.DIRT.defaultBlockState(), 1)).withRepair(0.95F);
		PAVED_PATH = makeFromProviders(() -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 6).add(Blocks.COBBLESTONE.defaultBlockState(), 3).add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 1).add(Blocks.GRAVEL.defaultBlockState(), 2), () -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 6).add(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 3).add(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 1).add(Blocks.GRAVEL.defaultBlockState(), 2), () -> new WeightedBlockStateProvider().add(Blocks.DIRT.defaultBlockState(), 1));
		CHALK_PATH = makeFromProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.CHALK_PATH.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.CHALK_PATH.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), 1)).withRepair(0.95F);
		DIRT = makeFromBlockAndSlab(Blocks.COARSE_DIRT, Blocks.COARSE_DIRT);
		COBBLESTONE = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(Blocks.COBBLESTONE.defaultBlockState(), 6).add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 6).add(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 1));
		SMOOTH_STONE = makeFromBlockAndSlab(Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB);
		STONE_BRICK = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(Blocks.STONE_BRICKS.defaultBlockState(), 4).add(Blocks.MOSSY_STONE_BRICKS.defaultBlockState(), 1).add(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.STONE_BRICK_SLAB.defaultBlockState(), 4).add(Blocks.MOSSY_STONE_BRICK_SLAB.defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_STONE_BRICK_SLAB.get()).defaultBlockState(), 1));
		DRYSTONE = makeFromBlockAndSlab(LOTRBlocks.DRYSTONE, LOTRBlocks.DRYSTONE);
		GONDOR = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GONDOR_BRICK.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.MOSSY_GONDOR_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_GONDOR_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.SMOOTH_GONDOR_ROCK.get()).defaultBlockState(), 2), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GONDOR_BRICK_SLAB.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.MOSSY_GONDOR_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_GONDOR_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.SMOOTH_GONDOR_ROCK_SLAB.get()).defaultBlockState(), 2)).withEdgeProvider(SMOOTH_STONE);
		DOL_AMROTH = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DOL_AMROTH_BRICK.get()).defaultBlockState(), 5).add(((Block) LOTRBlocks.CRACKED_DOL_AMROTH_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DOL_AMROTH_BRICK_SLAB.get()).defaultBlockState(), 5).add(((Block) LOTRBlocks.CRACKED_DOL_AMROTH_BRICK_SLAB.get()).defaultBlockState(), 1)).withPostProcessing().withEdgeProvider(makeFromBlockAndSlab(LOTRBlocks.SMOOTH_GONDOR_ROCK, LOTRBlocks.SMOOTH_GONDOR_ROCK_SLAB));
		ROHAN = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ROHAN_BRICK.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.CRACKED_ROHAN_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.ROHAN_ROCK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.SMOOTH_ROHAN_ROCK.get()).defaultBlockState(), 1).add(Blocks.COARSE_DIRT.defaultBlockState(), 2), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ROHAN_BRICK_SLAB.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.CRACKED_ROHAN_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.ROHAN_ROCK_SLAB.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.ROHAN_ROCK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.SMOOTH_ROHAN_ROCK_SLAB.get()).defaultBlockState(), 1).add(Blocks.COARSE_DIRT.defaultBlockState(), 2)).withRepair(0.94F).withEdgeProvider(COBBLESTONE);
		ARNOR = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ARNOR_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_ARNOR_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_ARNOR_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ARNOR_BRICK_SLAB.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_ARNOR_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_ARNOR_BRICK_SLAB.get()).defaultBlockState(), 1)).withEdgeProvider(STONE_BRICK);
		HIGH_ELVEN = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HIGH_ELVEN_BRICK.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.MOSSY_HIGH_ELVEN_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_HIGH_ELVEN_BRICK.get()).defaultBlockState(), 3), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HIGH_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.MOSSY_HIGH_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_HIGH_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 3)).withEdgeProvider(SMOOTH_STONE);
		WOOD_ELVEN = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.WOOD_ELVEN_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_WOOD_ELVEN_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_WOOD_ELVEN_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.WOOD_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_WOOD_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_WOOD_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 1)).withEdgeProvider(STONE_BRICK);
		MIRKWOOD_PATH = makeFromProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.WOOD_ELVEN_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_WOOD_ELVEN_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_WOOD_ELVEN_BRICK.get()).defaultBlockState(), 1).add(Blocks.GRASS_PATH.defaultBlockState(), 8), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.WOOD_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_WOOD_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_WOOD_ELVEN_BRICK_SLAB.get()).defaultBlockState(), 1).add(Blocks.GRASS_PATH.defaultBlockState(), 8), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.WOOD_ELVEN_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MOSSY_WOOD_ELVEN_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_WOOD_ELVEN_BRICK.get()).defaultBlockState(), 1).add(Blocks.DIRT.defaultBlockState(), 8)).withEdgeProvider(STONE_BRICK);
		MORDOR_BRICK = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_BRICK.get()).defaultBlockState(), 20).add(((Block) LOTRBlocks.CRACKED_MORDOR_BRICK.get()).defaultBlockState(), 8).add(((Block) LOTRBlocks.MOSSY_MORDOR_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_BRICK_SLAB.get()).defaultBlockState(), 20).add(((Block) LOTRBlocks.CRACKED_MORDOR_BRICK_SLAB.get()).defaultBlockState(), 8).add(((Block) LOTRBlocks.MOSSY_MORDOR_BRICK_SLAB.get()).defaultBlockState(), 1));
		MORDOR_PATH = makeFromProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_DIRT_PATH.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.MORDOR_GRAVEL.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_DIRT_PATH.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.MORDOR_GRAVEL.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState(), 1)).withEdgeProvider(MORDOR_BRICK);
		NURN_PATH = PATH.withEdgeProvider(MORDOR_BRICK);
		DWARVEN = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DWARVEN_BRICK.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.CRACKED_DWARVEN_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DWARVEN_BRICK_SLAB.get()).defaultBlockState(), 4).add(((Block) LOTRBlocks.CRACKED_DWARVEN_BRICK_SLAB.get()).defaultBlockState(), 1)).withEdgeProvider(STONE_BRICK);
		DALE = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DALE_BRICK.get()).defaultBlockState(), 8).add(((Block) LOTRBlocks.MOSSY_DALE_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_DALE_BRICK.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.DALE_PAVING.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.MOSSY_DALE_PAVING.get()).defaultBlockState(), 4), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DALE_BRICK_SLAB.get()).defaultBlockState(), 8).add(((Block) LOTRBlocks.MOSSY_DALE_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_DALE_BRICK_SLAB.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.DALE_PAVING_SLAB.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.MOSSY_DALE_PAVING_SLAB.get()).defaultBlockState(), 4)).withEdgeProvider(STONE_BRICK);
		DORWINION = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DORWINION_BRICK.get()).defaultBlockState(), 6).add(((Block) LOTRBlocks.MOSSY_DORWINION_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_DORWINION_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.DORWINION_BRICK_SLAB.get()).defaultBlockState(), 6).add(((Block) LOTRBlocks.MOSSY_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 1)).withEdgeProvider(makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.RED_DORWINION_BRICK.get()).defaultBlockState(), 6).add(((Block) LOTRBlocks.MOSSY_RED_DORWINION_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_RED_DORWINION_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.RED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 6).add(((Block) LOTRBlocks.MOSSY_RED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_RED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 1)));
		DORWINION_PATH = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 12).add(Blocks.COARSE_DIRT.defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 12).add(Blocks.COARSE_DIRT.defaultBlockState(), 1)).withEdgeProvider(makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.RED_DORWINION_BRICK.get()).defaultBlockState(), 6).add(((Block) LOTRBlocks.MOSSY_RED_DORWINION_BRICK.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_RED_DORWINION_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.RED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 6).add(((Block) LOTRBlocks.MOSSY_RED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 1).add(((Block) LOTRBlocks.CRACKED_RED_DORWINION_BRICK_SLAB.get()).defaultBlockState(), 1)));
		HARAD = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_BRICK.get()).defaultBlockState(), 6), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_BRICK_SLAB.get()).defaultBlockState(), 6)).withEdgeProvider(makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_PILLAR.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_PILLAR_SLAB.get()).defaultBlockState(), 1)));
		HARAD_PATH = makeFromProviders(() -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 12).add(Blocks.COARSE_DIRT.defaultBlockState(), 4).add(Blocks.SAND.defaultBlockState(), 1).add(((Block) LOTRBlocks.HARAD_BRICK.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.GRASS_PATH.defaultBlockState(), 12).add(Blocks.COARSE_DIRT.defaultBlockState(), 4).add(Blocks.SAND.defaultBlockState(), 1).add(((Block) LOTRBlocks.HARAD_BRICK_SLAB.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(Blocks.DIRT.defaultBlockState(), 16).add(Blocks.SANDSTONE.defaultBlockState(), 1).add(((Block) LOTRBlocks.HARAD_BRICK.get()).defaultBlockState(), 1)).withEdgeProvider(makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_PILLAR.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.HARAD_PILLAR_SLAB.get()).defaultBlockState(), 1)));
		UMBAR = makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.UMBAR_BRICK.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.HARAD_BRICK.get()).defaultBlockState(), 4), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.UMBAR_BRICK_SLAB.get()).defaultBlockState(), 12).add(((Block) LOTRBlocks.HARAD_BRICK_SLAB.get()).defaultBlockState(), 4)).withEdgeProvider(makeFromBlockAndSlabProviders(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.NUMENOREAN_BRICK.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.NUMENOREAN_PILLAR.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.NUMENOREAN_BRICK_SLAB.get()).defaultBlockState(), 2).add(((Block) LOTRBlocks.NUMENOREAN_PILLAR_SLAB.get()).defaultBlockState(), 1)));
		STANDARD_HEDGE = LazyOptional.of(() -> new WeightedBlockStateProvider().add(Blocks.COBBLESTONE.defaultBlockState(), 3).add(Blocks.COBBLESTONE_SLAB.defaultBlockState(), 2).add(Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3).add(Blocks.MOSSY_COBBLESTONE_SLAB.defaultBlockState(), 2).add(Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 24));
	}
	private final LazyOptional topProvider;
	private final LazyOptional topSlabProvider;
	private final LazyOptional fillerProvider;
	private final float repair;
	private final boolean requiresPostProcessing;
	private final RoadBlockProvider edgeRoadBlockProvider;
	private final LazyOptional hedgeProvider;

	private final float hedgeDensity;

	public RoadBlockProvider(LazyOptional top, LazyOptional topSlab, LazyOptional filler) {
		this(top, topSlab, filler, 1.0F);
	}

	public RoadBlockProvider(LazyOptional top, LazyOptional topSlab, LazyOptional filler, float rep) {
		this(top, topSlab, filler, rep, false, (RoadBlockProvider) null, (LazyOptional) null, 0.0F);
	}

	public RoadBlockProvider(LazyOptional top, LazyOptional topSlab, LazyOptional filler, float rep, boolean postProcess, RoadBlockProvider edgeRoad, LazyOptional hedge, float hedgeDens) {
		topProvider = top;
		topSlabProvider = topSlab;
		fillerProvider = filler;
		repair = rep;
		requiresPostProcessing = postProcess;
		edgeRoadBlockProvider = edgeRoad;
		hedgeProvider = hedge;
		hedgeDensity = hedgeDens;
	}

	public RoadBlockProvider getEdgeProvider() {
		return edgeRoadBlockProvider != null ? edgeRoadBlockProvider : this;
	}

	public BlockState getFillerBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) fillerProvider.orElse(FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getHedgeBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) hedgeProvider.orElse(FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public float getHedgeDensity() {
		return hedgeDensity;
	}

	public float getRepair() {
		return repair;
	}

	public BlockState getTopBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) topProvider.orElse(FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getTopSlabBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) topSlabProvider.orElse(FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public boolean hasDistinctEdge() {
		return edgeRoadBlockProvider != null;
	}

	public boolean hasHedge() {
		return hedgeProvider != null;
	}

	public boolean requiresPostProcessing() {
		return requiresPostProcessing;
	}

	public RoadBlockProvider withEdgeProvider(RoadBlockProvider newEdge) {
		return new RoadBlockProvider(topProvider, topSlabProvider, fillerProvider, repair, requiresPostProcessing, newEdge, hedgeProvider, hedgeDensity);
	}

	public RoadBlockProvider withHedge(LazyOptional hedge, float hedgeDens) {
		return new RoadBlockProvider(topProvider, topSlabProvider, fillerProvider, repair, requiresPostProcessing, edgeRoadBlockProvider, hedge, hedgeDens);
	}

	public RoadBlockProvider withPostProcessing() {
		return new RoadBlockProvider(topProvider, topSlabProvider, fillerProvider, repair, true, edgeRoadBlockProvider, hedgeProvider, hedgeDensity);
	}

	public RoadBlockProvider withRepair(float newRepair) {
		return new RoadBlockProvider(topProvider, topSlabProvider, fillerProvider, newRepair, requiresPostProcessing, edgeRoadBlockProvider, hedgeProvider, hedgeDensity);
	}

	public RoadBlockProvider withStandardHedge() {
		return withHedge(STANDARD_HEDGE, 0.82F);
	}

	public static RoadBlockProvider makeFromBlockAndSlab(Block full, Block slab) {
		return makeFromBlockAndSlab(() -> full, () -> slab);
	}

	public static RoadBlockProvider makeFromBlockAndSlab(NonNullSupplier full, NonNullSupplier slab) {
		return makeFromSingleBlocks(full, slab, full);
	}

	public static RoadBlockProvider makeFromBlockAndSlab(RegistryObject full, RegistryObject slab) {
		return makeFromBlockAndSlab(() -> full.get(), () -> slab.get());
	}

	public static RoadBlockProvider makeFromBlockAndSlabProviders(NonNullSupplier full, NonNullSupplier slab) {
		return makeFromProviders(full, slab, full);
	}

	public static RoadBlockProvider makeFromProviders(NonNullSupplier top, NonNullSupplier topSlab, NonNullSupplier filler) {
		return new RoadBlockProvider(LazyOptional.of(top), LazyOptional.of(topSlab), LazyOptional.of(filler));
	}

	public static RoadBlockProvider makeFromSingleBlocks(NonNullSupplier top, NonNullSupplier topSlab, NonNullSupplier filler) {
		return makeFromProviders(() -> new WeightedBlockStateProvider().add(((Block) top.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) topSlab.get()).defaultBlockState(), 1), () -> new WeightedBlockStateProvider().add(((Block) filler.get()).defaultBlockState(), 1));
	}
}
