package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;

public class RottenLogSlabBlock extends LogSlabBlock {
	private static final VoxelShape HOLLOW_BOTTOM_SHAPE;
	private static final VoxelShape HOLLOW_TOP_SHAPE;
	private static final VoxelShape HOLLOW_NORTH_SHAPE;
	private static final VoxelShape HOLLOW_SOUTH_SHAPE;
	private static final VoxelShape HOLLOW_WEST_SHAPE;
	private static final VoxelShape HOLLOW_EAST_SHAPE;

	static {
		HOLLOW_BOTTOM_SHAPE = VoxelShapes.join(SlabBlock.BOTTOM_AABB, Block.box(2.0D, 0.0D, 2.0D, 14.0D, 8.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
		HOLLOW_TOP_SHAPE = VoxelShapes.join(SlabBlock.TOP_AABB, Block.box(2.0D, 8.0D, 2.0D, 14.0D, 16.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
		HOLLOW_NORTH_SHAPE = VoxelShapes.join(AxialSlabBlock.NORTH_SHAPE, Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 8.0D), IBooleanFunction.ONLY_FIRST);
		HOLLOW_SOUTH_SHAPE = VoxelShapes.join(AxialSlabBlock.SOUTH_SHAPE, Block.box(2.0D, 2.0D, 8.0D, 14.0D, 14.0D, 16.0D), IBooleanFunction.ONLY_FIRST);
		HOLLOW_WEST_SHAPE = VoxelShapes.join(AxialSlabBlock.WEST_SHAPE, Block.box(0.0D, 2.0D, 2.0D, 8.0D, 14.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
		HOLLOW_EAST_SHAPE = VoxelShapes.join(AxialSlabBlock.EAST_SHAPE, Block.box(8.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D), IBooleanFunction.ONLY_FIRST);
	}

	public RottenLogSlabBlock(Supplier block) {
		super(block);
	}

	@Override
	protected boolean canDoubleSlabBeWaterlogged() {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		SlabType slabType = state.getValue(TYPE);
		Axis axis = (Axis) state.getValue(getSlabAxisProperty());
		if (slabType == SlabType.DOUBLE) {
			if (axis == Axis.Y) {
				return RottenLogBlock.Y_SHAPE;
			}

			if (axis == Axis.X) {
				return RottenLogBlock.X_SHAPE;
			}

			if (axis == Axis.Z) {
				return RottenLogBlock.Z_SHAPE;
			}
		} else {
			boolean top = slabType == SlabType.TOP;
			if (axis == Axis.Y) {
				return top ? HOLLOW_TOP_SHAPE : HOLLOW_BOTTOM_SHAPE;
			}

			if (axis == Axis.X) {
				return top ? HOLLOW_EAST_SHAPE : HOLLOW_WEST_SHAPE;
			}

			if (axis == Axis.Z) {
				return top ? HOLLOW_SOUTH_SHAPE : HOLLOW_NORTH_SHAPE;
			}
		}

		return VoxelShapes.block();
	}
}
