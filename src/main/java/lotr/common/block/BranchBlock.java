package lotr.common.block;

import java.util.function.Supplier;

import lotr.common.init.LOTRTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class BranchBlock extends FourWayBlock implements IForgeBlockState {
	public BranchBlock(Block block) {
		this(Properties.copy(block));
	}

	public BranchBlock(Properties properties) {
		super(4.0F, 3.0F, 16.0F, 16.0F, 16.0F, properties);
		registerDefaultState(stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
	}

	public BranchBlock(Supplier block) {
		this((Block) block.get());
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	private boolean doesBranchConnect(BlockState state, boolean isSolidSide, Direction oppositeFace) {
		Block block = state.getBlock();
		boolean isOtherBranch = block.is(LOTRTags.Blocks.BRANCHES);
		boolean isLeaves = block.is(BlockTags.LEAVES);
		boolean isWoodenFence = block.is(BlockTags.FENCES) && block.is(BlockTags.WOODEN_FENCES);
		boolean isParallelFenceGate = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, oppositeFace);
		return !isExceptionForConnection(block) && isSolidSide || isOtherBranch || isLeaves || isWoodenFence || isParallelFenceGate;
	}

	private boolean doesBranchConnectDirectional(IWorldReader world, BlockPos pos, Direction dir) {
		BlockPos offsetPos = pos.relative(dir);
		BlockState adjacentState = world.getBlockState(offsetPos);
		Direction oppositeFace = dir.getOpposite();
		return doesBranchConnect(adjacentState, adjacentState.isFaceSturdy(world, offsetPos, oppositeFace), oppositeFace);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IWorldReader world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		FluidState fluid = world.getFluidState(pos);
		boolean waterlogged = fluid.getType() == Fluids.WATER;
		boolean connectNorth = doesBranchConnectDirectional(world, pos, Direction.NORTH);
		boolean connectSouth = doesBranchConnectDirectional(world, pos, Direction.SOUTH);
		boolean connectWest = doesBranchConnectDirectional(world, pos, Direction.WEST);
		boolean connectEast = doesBranchConnectDirectional(world, pos, Direction.EAST);
		return defaultBlockState().setValue(NORTH, connectNorth).setValue(SOUTH, connectSouth).setValue(WEST, connectWest).setValue(EAST, connectEast).setValue(WATERLOGGED, waterlogged);
	}

	private boolean getUpdatedConnectionOrStateProperty(Direction facing, Direction relevantDirection, BlockState facingState, IWorld world, BlockPos facingPos, boolean stateProperty) {
		Direction oppositeFace = facing.getOpposite();
		return facing == relevantDirection ? doesBranchConnect(facingState, facingState.isFaceSturdy(world, facingPos, oppositeFace), oppositeFace) : stateProperty;
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		if (facing != Direction.DOWN && facing != Direction.UP) {
			boolean connectNorth = getUpdatedConnectionOrStateProperty(facing, Direction.NORTH, facingState, world, facingPos, state.getValue(NORTH));
			boolean connectEast = getUpdatedConnectionOrStateProperty(facing, Direction.EAST, facingState, world, facingPos, state.getValue(EAST));
			boolean connectSouth = getUpdatedConnectionOrStateProperty(facing, Direction.SOUTH, facingState, world, facingPos, state.getValue(SOUTH));
			boolean connectWest = getUpdatedConnectionOrStateProperty(facing, Direction.WEST, facingState, world, facingPos, state.getValue(WEST));
			return state.setValue(NORTH, connectNorth).setValue(EAST, connectEast).setValue(SOUTH, connectSouth).setValue(WEST, connectWest);
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}
