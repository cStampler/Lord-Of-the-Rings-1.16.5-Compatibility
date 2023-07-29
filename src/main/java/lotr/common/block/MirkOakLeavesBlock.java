package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

public class MirkOakLeavesBlock extends LOTRLeavesBlock {
	public static final BooleanProperty DOWN;

	static {
		DOWN = BlockStateProperties.DOWN;
	}

	public MirkOakLeavesBlock() {
		registerDefaultState(defaultBlockState().setValue(DOWN, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(DOWN);
	}

	@Override
	public int getLightBlock(BlockState state, IBlockReader world, BlockPos pos) {
		return state.getValue(DOWN) ? 15 : super.getLightBlock(state, world, pos);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(DOWN, hasDownState(context.getLevel(), context.getClickedPos()));
	}

	private boolean hasDownState(IWorld world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		return Block.isFaceFull(belowState.getBlockSupportShape(world, belowPos), Direction.UP) || belowState.getBlock() instanceof HangingWebBlock;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		state = super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		if (facing == Direction.DOWN) {
			state = state.setValue(DOWN, hasDownState(world, currentPos));
		}

		return state;
	}
}
