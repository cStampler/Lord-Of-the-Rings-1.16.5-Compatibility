package lotr.common.block;

import lotr.common.init.LOTRTags;
import net.minecraft.block.*;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.IPlantable;

public abstract class MordorPlantBlock extends Block {
	protected MordorPlantBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos below = pos.below();
		return isValidGround(world.getBlockState(below), world, below);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return type == PathType.AIR && !hasCollision ? true : super.isPathfindable(state, world, pos, type);
	}

	protected boolean isValidGround(BlockState state, IBlockReader world, BlockPos pos) {
		return state.is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES) || state.canSustainPlant(world, pos, Direction.UP, (IPlantable) Blocks.GRASS);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
		return !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, pos, facingPos);
	}
}
