package lotr.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class DoubleAridGrassBlock extends LOTRDoubleGrassBlock {
	@Override
	protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
		return super.mayPlaceOn(state, world, pos) || state.is(BlockTags.SAND);
	}
}
