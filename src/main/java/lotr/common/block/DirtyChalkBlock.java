package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DirtyChalkBlock extends ChalkBlock {
	public static final BooleanProperty BELOW;

	static {
		BELOW = LOTRBlockStates.DIRTY_CHALK_BELOW;
	}

	public DirtyChalkBlock(Supplier blockSup) {
		super(blockSup);
		registerDefaultState(defaultBlockState().setValue(BELOW, false));
	}

	private BlockState checkDirtyChalkBelow(BlockState state, BlockState belowState) {
		boolean hasBelow = belowState.getBlock() == this;
		return state.setValue(BELOW, hasBelow);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(BELOW);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		IWorld world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		return checkDirtyChalkBelow(state, belowState);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return dir == Direction.DOWN ? checkDirtyChalkBelow(state, facingState) : super.updateShape(state, dir, facingState, world, currentPos, facingPos);
	}
}
