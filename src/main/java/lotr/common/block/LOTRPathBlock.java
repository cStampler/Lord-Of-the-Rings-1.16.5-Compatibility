package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;

public abstract class LOTRPathBlock extends Block {
	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

	public LOTRPathBlock(Properties basicProperties) {
		super(basicProperties.isViewBlocking(LOTRBlocks::posPredicateTrue).isSuffocating(LOTRBlocks::posPredicateTrue));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockState aboveState = world.getBlockState(pos.above());
		return !aboveState.getMaterial().isSolid() || aboveState.getBlock() instanceof FenceGateBlock;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState defState = defaultBlockState();
		if (!defState.canSurvive(world, pos)) {
			Block.pushEntitiesUp(defState, getUnpathedBlockState(), world, pos);
		}

		return super.getStateForPlacement(context);
	}

	protected abstract BlockState getUnpathedBlockState();

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		world.setBlockAndUpdate(pos, Block.pushEntitiesUp(state, getUnpathedBlockState(), world, pos));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.UP && !state.canSurvive(world, currentPos)) {
			world.getBlockTicks().scheduleTick(currentPos, this, 1);
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
}
