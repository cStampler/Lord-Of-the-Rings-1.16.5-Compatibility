package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

public class WaterloggableDoublePlantBlock extends DoublePlantBlock implements IWaterLoggable {
	public static final BooleanProperty WATERLOGGED;

	static {
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
	}

	public WaterloggableDoublePlantBlock() {
		this(Properties.of(Material.REPLACEABLE_PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
	}

	public WaterloggableDoublePlantBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState superState = super.getStateForPlacement(context);
		if (superState != null) {
			FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
			return superState.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
		}
		return null;
	}

	@Override
	public void placeAt(IWorld world, BlockPos pos, int flags) {
		BlockPos abovePos = pos.above();
		boolean waterlogged = world.getFluidState(pos).getType() == Fluids.WATER;
		boolean waterloggedAbove = world.getFluidState(abovePos).getType() == Fluids.WATER;
		world.setBlock(pos, defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(WATERLOGGED, waterlogged), flags);
		world.setBlock(abovePos, defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER).setValue(WATERLOGGED, waterloggedAbove), flags);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf half = state.getValue(HALF);
		BlockPos otherHalfPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
		BlockState otherHalfState = world.getBlockState(otherHalfPos);
		boolean wasOtherHalfWaterlogged = otherHalfState.hasProperty(WATERLOGGED) && otherHalfState.getValue(WATERLOGGED);
		super.playerWillDestroy(world, pos, state, player);
		if (wasOtherHalfWaterlogged) {
			FluidState otherHalfFluidState = otherHalfState.getFluidState();
			FluidState otherHalfReplacedFluidState = world.getFluidState(otherHalfPos);
			if (otherHalfReplacedFluidState.getType() != otherHalfFluidState.getType()) {
				world.setBlock(otherHalfPos, otherHalfFluidState.createLegacyBlock(), 3);
			}
		}

	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}
