package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class SoulFireHearthBlock extends HearthBlock {
	@Override
	protected Block getFireBlock() {
		return Blocks.SOUL_FIRE;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		return new ItemStack((IItemProvider) LOTRBlocks.HEARTH_BLOCK.get());
	}

	@Override
	public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		state = super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		return facing == Direction.UP && !(Boolean) state.getValue(LIT) ? ((Block) LOTRBlocks.HEARTH_BLOCK.get()).defaultBlockState() : state;
	}
}
