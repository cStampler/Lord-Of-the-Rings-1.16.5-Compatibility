package lotr.common.block;

import lotr.common.event.CompostingHelper;
import lotr.common.init.LOTRTags;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;

public class MordorMossBlock extends Block {
	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

	public MordorMossBlock() {
		super(Properties.of(Material.PLANT).noCollission().strength(0.2F).sound(SoundType.GRASS));
		CompostingHelper.prepareCompostable(this, 0.5F);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos below = pos.below();
		return isValidGround(world.getBlockState(below));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	private boolean isValidGround(BlockState state) {
		Block block = state.getBlock();
		return block.is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
	}
}
