package lotr.common.block;

import lotr.common.init.LOTRTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class MorgulShroomBlock extends LOTRMushroomBlock {
	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockState belowState = world.getBlockState(pos.below());
		return belowState.is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES) ? true : super.canSurvive(state, world, pos);
	}
}
