package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BrickWithAboveBlock extends LOTRStoneBlock {
	public static final BooleanProperty ABOVE;
	static {
		ABOVE = LOTRBlockStates.BRICK_ABOVE;
	}

	private final ITag alikeBlocks;

	public BrickWithAboveBlock(MaterialColor materialColor, ITag blocks) {
		super(materialColor);
		alikeBlocks = blocks;
		initBrickWithAbove();
	}

	public BrickWithAboveBlock(Supplier blockSup, ITag blocks) {
		super(blockSup);
		alikeBlocks = blocks;
		initBrickWithAbove();
	}

	private BlockState checkAboveBlock(IWorld world, BlockState state, BlockPos abovePos, BlockState aboveState) {
		boolean hasAbove = aboveState.is(alikeBlocks) && aboveState.isFaceSturdy(world, abovePos, Direction.DOWN);
		return state.setValue(ABOVE, hasAbove);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ABOVE);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		IWorld world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockPos abovePos = pos.above();
		BlockState aboveState = world.getBlockState(abovePos);
		return checkAboveBlock(world, state, abovePos, aboveState);
	}

	private void initBrickWithAbove() {
		registerDefaultState(defaultBlockState().setValue(ABOVE, false));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return dir == Direction.UP ? checkAboveBlock(world, state, facingPos, facingState) : super.updateShape(state, dir, facingState, world, currentPos, facingPos);
	}
}
