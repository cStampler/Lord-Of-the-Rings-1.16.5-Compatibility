package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class RottenWoodBeamSlabBlock extends LogSlabBlock {
	public static final VoxelShape HOLLOW_BOTTOM_SHAPE;
	public static final VoxelShape HOLLOW_TOP_SHAPE;
	public static final VoxelShape HOLLOW_NORTH_SHAPE;
	public static final VoxelShape HOLLOW_SOUTH_SHAPE;
	public static final VoxelShape HOLLOW_WEST_SHAPE;
	public static final VoxelShape HOLLOW_EAST_SHAPE;

	static {
		HOLLOW_BOTTOM_SHAPE = VoxelShapes.join(SlabBlock.BOTTOM_AABB, VoxelShapes.or(Block.box(2.0D, 0.0D, 2.0D, 6.0D, 8.0D, 6.0D), Block.box(10.0D, 0.0D, 2.0D, 14.0D, 8.0D, 6.0D), Block.box(2.0D, 0.0D, 10.0D, 6.0D, 8.0D, 14.0D), Block.box(10.0D, 0.0D, 10.0D, 14.0D, 8.0D, 14.0D)), IBooleanFunction.ONLY_FIRST);
		HOLLOW_TOP_SHAPE = VoxelShapes.join(SlabBlock.TOP_AABB, VoxelShapes.or(Block.box(2.0D, 8.0D, 2.0D, 6.0D, 16.0D, 6.0D), Block.box(10.0D, 8.0D, 2.0D, 14.0D, 16.0D, 6.0D), Block.box(2.0D, 8.0D, 10.0D, 6.0D, 16.0D, 14.0D), Block.box(10.0D, 8.0D, 10.0D, 14.0D, 16.0D, 14.0D)), IBooleanFunction.ONLY_FIRST);
		HOLLOW_NORTH_SHAPE = VoxelShapes.join(AxialSlabBlock.NORTH_SHAPE, VoxelShapes.or(Block.box(2.0D, 2.0D, 0.0D, 6.0D, 6.0D, 8.0D), Block.box(10.0D, 2.0D, 0.0D, 14.0D, 6.0D, 8.0D), Block.box(2.0D, 10.0D, 0.0D, 6.0D, 14.0D, 8.0D), Block.box(10.0D, 10.0D, 0.0D, 14.0D, 14.0D, 8.0D)), IBooleanFunction.ONLY_FIRST);
		HOLLOW_SOUTH_SHAPE = VoxelShapes.join(AxialSlabBlock.SOUTH_SHAPE, VoxelShapes.or(Block.box(2.0D, 2.0D, 8.0D, 6.0D, 6.0D, 16.0D), Block.box(10.0D, 2.0D, 8.0D, 14.0D, 6.0D, 16.0D), Block.box(2.0D, 10.0D, 8.0D, 6.0D, 14.0D, 16.0D), Block.box(10.0D, 10.0D, 8.0D, 14.0D, 14.0D, 16.0D)), IBooleanFunction.ONLY_FIRST);
		HOLLOW_WEST_SHAPE = VoxelShapes.join(AxialSlabBlock.WEST_SHAPE, VoxelShapes.or(Block.box(0.0D, 2.0D, 2.0D, 8.0D, 6.0D, 6.0D), Block.box(0.0D, 10.0D, 2.0D, 8.0D, 14.0D, 6.0D), Block.box(0.0D, 2.0D, 10.0D, 8.0D, 6.0D, 14.0D), Block.box(0.0D, 10.0D, 10.0D, 8.0D, 14.0D, 14.0D)), IBooleanFunction.ONLY_FIRST);
		HOLLOW_EAST_SHAPE = VoxelShapes.join(AxialSlabBlock.EAST_SHAPE, VoxelShapes.or(Block.box(8.0D, 2.0D, 2.0D, 16.0D, 6.0D, 6.0D), Block.box(8.0D, 10.0D, 2.0D, 16.0D, 14.0D, 6.0D), Block.box(8.0D, 2.0D, 10.0D, 16.0D, 6.0D, 14.0D), Block.box(8.0D, 10.0D, 10.0D, 16.0D, 14.0D, 14.0D)), IBooleanFunction.ONLY_FIRST);
	}

	public RottenWoodBeamSlabBlock(Supplier block) {
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
				return RottenWoodBeamBlock.Y_SHAPE;
			}

			if (axis == Axis.X) {
				return RottenWoodBeamBlock.X_SHAPE;
			}

			if (axis == Axis.Z) {
				return RottenWoodBeamBlock.Z_SHAPE;
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
