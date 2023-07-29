package lotr.common.world.map;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.state.properties.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.blockstateprovider.*;
import net.minecraftforge.common.util.LazyOptional;

public class BridgeBlockProvider {
	public static final BridgeBlockProvider OAK = new BridgeBlockProvider(LazyOptional.of(() -> new WeightedBlockStateProvider().add(Blocks.OAK_PLANKS.defaultBlockState(), 1)), LazyOptional.of(() -> new WeightedBlockStateProvider().add(Blocks.OAK_SLAB.defaultBlockState(), 1)), LazyOptional.of(() -> new WeightedBlockStateProvider().add(Blocks.OAK_FENCE.defaultBlockState(), 1)), LazyOptional.of(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.OAK_BEAM.get()).defaultBlockState(), 1)), LazyOptional.of(() -> new WeightedBlockStateProvider().add(((Block) LOTRBlocks.OAK_BEAM_SLAB.get()).defaultBlockState(), 1)));
	private final LazyOptional mainProvider;
	private final LazyOptional mainSlabProvider;
	private final LazyOptional fenceProvider;
	private final LazyOptional beamProvider;
	private final LazyOptional beamSlabProvider;

	public BridgeBlockProvider(LazyOptional main, LazyOptional mainSlab, LazyOptional fence, LazyOptional beam, LazyOptional beamSlab) {
		mainProvider = main;
		mainSlabProvider = mainSlab;
		fenceProvider = fence;
		beamProvider = beam;
		beamSlabProvider = beamSlab;
	}

	public BlockState getBeamBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) beamProvider.orElse(RoadBlockProvider.FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getBeamSlabBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) beamSlabProvider.orElse(RoadBlockProvider.FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getBeamSlabBlockInverted(Random rand, BlockPos pos) {
		return safeGetInvertedSlab(getBeamSlabBlock(rand, pos));
	}

	public BlockState getFenceBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) fenceProvider.orElse(RoadBlockProvider.FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getMainBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) mainProvider.orElse(RoadBlockProvider.FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getMainSlabBlock(Random rand, BlockPos pos) {
		return ((BlockStateProvider) mainSlabProvider.orElse(RoadBlockProvider.FAILSAFE_PROVIDER)).getState(rand, pos);
	}

	public BlockState getMainSlabBlockInverted(Random rand, BlockPos pos) {
		return safeGetInvertedSlab(getMainSlabBlock(rand, pos));
	}

	private BlockState safeGetInvertedSlab(BlockState slab) {
		if (slab.hasProperty(BlockStateProperties.SLAB_TYPE)) {
			slab = slab.setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
		}

		return slab;
	}
}
