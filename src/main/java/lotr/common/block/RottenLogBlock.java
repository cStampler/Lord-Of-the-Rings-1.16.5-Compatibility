package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.fluid.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;

public class RottenLogBlock extends LOTRLogBlock implements IWaterLoggable {
	public static final BooleanProperty WATERLOGGED;
	public static final VoxelShape Y_SHAPE;
	public static final VoxelShape X_SHAPE;
	public static final VoxelShape Z_SHAPE;

	static {
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		Y_SHAPE = VoxelShapes.join(VoxelShapes.block(), Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
		X_SHAPE = VoxelShapes.join(VoxelShapes.block(), Block.box(0.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
		Z_SHAPE = VoxelShapes.join(VoxelShapes.block(), Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 16.0D), IBooleanFunction.ONLY_FIRST);
	}

	public RottenLogBlock(MaterialColor wood, MaterialColor bark) {
		super(Properties.of(Material.WOOD, LOTRLogBlock.logStateToMaterialColor(wood, bark)).strength(2.0F).noOcclusion().sound(SoundType.WOOD), bark, wood);
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
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Axis axis = state.getValue(AXIS);
		switch (axis) {
		case Y:
		default:
			return Y_SHAPE;
		case X:
			return X_SHAPE;
		case Z:
			return Z_SHAPE;
		}
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
	public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidState) {
		return true;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}
