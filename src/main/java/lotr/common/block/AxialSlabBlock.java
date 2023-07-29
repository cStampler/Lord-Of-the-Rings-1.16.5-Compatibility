package lotr.common.block;

import java.util.Iterator;
import java.util.function.Supplier;

import lotr.common.LOTRLog;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.*;
import net.minecraft.util.Direction.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;

public class AxialSlabBlock extends LOTRSlabBlock {
	public static final VoxelShape NORTH_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 8.0D);
	public static final VoxelShape SOUTH_SHAPE = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
	public static final VoxelShape WEST_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 8.0D, 16.0D, 16.0D);
	public static final VoxelShape EAST_SHAPE = Block.box(8.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

	public AxialSlabBlock(Block block) {
		super(block);
		Axis defaultAxis = getSlabAxisProperty().getPossibleValues().contains(Axis.Y) ? Axis.Y : Axis.X;
		registerDefaultState(defaultBlockState().setValue(getSlabAxisProperty(), defaultAxis));
	}

	public AxialSlabBlock(Supplier block) {
		this((Block) block.get());
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext context) {
		return isSlabReplaceable(state, context);
	}

	protected boolean canDoubleSlabBeWaterlogged() {
		return false;
	}

	@Override
	public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid) {
		return canDoubleSlabBeWaterlogged() ? new AxialSlabBlock.DefaultImplWaterLoggable().canPlaceLiquid(world, pos, state, fluid) : super.canPlaceLiquid(world, pos, state, fluid);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(getSlabAxisProperty());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		SlabType slabType = state.getValue(TYPE);
		if (slabType == SlabType.DOUBLE) {
			return VoxelShapes.block();
		}
		boolean top = slabType == SlabType.TOP;
		Axis axis = (Axis) state.getValue(getSlabAxisProperty());
		if (axis == Axis.Y) {
			return top ? SlabBlock.TOP_AABB : SlabBlock.BOTTOM_AABB;
		}
		if (axis == Axis.X) {
			return top ? EAST_SHAPE : WEST_SHAPE;
		}
		if (axis == Axis.Z) {
			return top ? SOUTH_SHAPE : NORTH_SHAPE;
		}
		return VoxelShapes.block();
	}

	protected EnumProperty getSlabAxisProperty() {
		return LOTRBlockStates.SLAB_AXIS;
	}

	protected final AxialSlabBlock.AxialSlabPlacement getSlabPlacementState(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);
		Direction dir = context.getClickedFace();
		Axis axis = dir.getAxis();
		FluidState fluid = context.getLevel().getFluidState(pos);
		boolean waterlogged = fluid.getType() == Fluids.WATER;
		if (this.isSameSlab(state)) {
			waterlogged &= canDoubleSlabBeWaterlogged();
			return AxialSlabBlock.AxialSlabPlacement.of(getSlabAxis(state), SlabType.DOUBLE, waterlogged);
		}
		BlockPos clickedPos = pos.relative(dir.getOpposite());
		BlockState clickedState = world.getBlockState(clickedPos);
		boolean sneaking = context.isSecondaryUseActive();
		if (sneaking) {
			if (axis.isHorizontal()) {
				axis = Axis.Y;
			} else if (axis.isVertical() && (!isSingleSlab(clickedState) || !getSlabAxis(clickedState).isHorizontal())) {
				dir = context.getHorizontalDirection();
				axis = dir.getAxis();
			}
		} else if (isSingleSlab(clickedState)) {
			axis = getSlabAxis(clickedState);
		}

		Direction axisPosDir = Direction.get(AxisDirection.POSITIVE, axis);
		Direction axisNegDir = Direction.get(AxisDirection.NEGATIVE, axis);
		double relevantHitVecCoord = axis.choose(context.getClickLocation().x, context.getClickLocation().y, context.getClickLocation().z);
		double relevantPosCoord = axis.choose(pos.getX(), pos.getY(), pos.getZ());
		return dir == axisNegDir || dir != axisPosDir && relevantHitVecCoord - relevantPosCoord > 0.5D ? AxialSlabBlock.AxialSlabPlacement.of(axis, SlabType.TOP, waterlogged) : AxialSlabBlock.AxialSlabPlacement.of(axis, SlabType.BOTTOM, waterlogged);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		AxialSlabBlock.AxialSlabPlacement placement = getSlabPlacementState(context);
		return defaultBlockState().setValue(getSlabAxisProperty(), placement.axis).setValue(TYPE, placement.slabType).setValue(WATERLOGGED, placement.waterlogged);
	}

	protected final boolean isSameSlab(BlockState otherBlockState) {
		Block otherBlock = otherBlockState.getBlock();
		return otherBlock instanceof SlabBlock && this.isSameSlab((SlabBlock) otherBlock);
	}

	protected boolean isSameSlab(SlabBlock otherSlab) {
		return otherSlab == this;
	}

	protected final boolean isSlabReplaceable(BlockState state, BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		ItemStack itemstack = context.getItemInHand();
		boolean holdingSameSlab = false;
		if (itemstack.getItem() instanceof BlockItem) {
			Block itemBlock = ((BlockItem) itemstack.getItem()).getBlock();
			if (itemBlock instanceof SlabBlock) {
				holdingSameSlab = this.isSameSlab((SlabBlock) itemBlock);
			}
		}

		Direction dir = context.getClickedFace();
		boolean sneaking = context.isSecondaryUseActive();
		SlabType slabType = state.getValue(TYPE);
		Axis existingAxis = getSlabAxis(state);
		if (sneaking && existingAxis != Axis.Y) {
			BlockPos offsetPos = pos.relative(dir);
			if (world.getBlockState(offsetPos).canBeReplaced(AxialSlabBlock.AxialSlabUseContext.makeReplacementContext(context, offsetPos, dir))) {
				return false;
			}
		}

		if (slabType == SlabType.DOUBLE || !holdingSameSlab) {
			return false;
		}
		if (!context.replacingClickedOnBlock()) {
			return true;
		}
		double relevantHitVecCoord = existingAxis.choose(context.getClickLocation().x, context.getClickLocation().y, context.getClickLocation().z);
		double relevantPosCoord = existingAxis.choose(pos.getX(), pos.getY(), pos.getZ());
		boolean flag = relevantHitVecCoord - relevantPosCoord > 0.5D;
		if (slabType == SlabType.BOTTOM) {
			return dir == Direction.get(AxisDirection.POSITIVE, existingAxis) || flag && dir.getAxis() != existingAxis;
		}
		return dir == Direction.get(AxisDirection.NEGATIVE, existingAxis) || !flag && dir.getAxis() != existingAxis;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Axis axis = (Axis) state.getValue(getSlabAxisProperty());
		SlabType type = state.getValue(TYPE);
		if (mirror == Mirror.LEFT_RIGHT && axis == Axis.Z || mirror == Mirror.FRONT_BACK && axis == Axis.X) {
			if (type == SlabType.BOTTOM) {
				type = SlabType.TOP;
			} else if (type == SlabType.TOP) {
				type = SlabType.BOTTOM;
			}
		}

		return state.setValue(TYPE, type);
	}

	@Override
	public boolean placeLiquid(IWorld world, BlockPos pos, BlockState state, FluidState fluidState) {
		return canDoubleSlabBeWaterlogged() ? new AxialSlabBlock.DefaultImplWaterLoggable().placeLiquid(world, pos, state, fluidState) : super.placeLiquid(world, pos, state, fluidState);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		SlabType slabType = state.getValue(TYPE);
		Axis axis = (Axis) state.getValue(getSlabAxisProperty());
		AxisDirection axisDir = slabType == SlabType.BOTTOM ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;
		Direction dir = Direction.fromAxisAndDirection(axis, axisDir);
		Direction rotatedDir = rot.rotate(dir);
		Axis rotatedAxis = rotatedDir.getAxis();
		AxisDirection rotatedAxisDir = rotatedDir.getAxisDirection();
		if (getSlabAxisProperty().getPossibleValues().contains(rotatedAxis)) {
			SlabType rotatedSlabType = slabType == SlabType.DOUBLE ? slabType : rotatedAxisDir == AxisDirection.NEGATIVE ? SlabType.BOTTOM : rotatedAxisDir == AxisDirection.POSITIVE ? SlabType.TOP : slabType;
			return state.setValue(getSlabAxisProperty(), rotatedAxis).setValue(TYPE, rotatedSlabType);
		}
		return state;
	}

	private static Axis getSlabAxis(BlockState state) {
		Block block = state.getBlock();
		if (!(block instanceof SlabBlock)) {
			throw new IllegalArgumentException("This method should only get called on known instances of SlabBlock");
		}
		Iterator var2 = state.getProperties().iterator();

		Property prop;
		do {
			if (!var2.hasNext()) {
				if (block.getClass() == SlabBlock.class) {
					return Axis.Y;
				}

				LOTRLog.warn("Unknown SlabBlock subclass: %s with no axis-based property. Assuming axis = Y", block.getClass().toString());
				return Axis.Y;
			}

			prop = (Property) var2.next();
		} while (!(prop instanceof EnumProperty) || ((EnumProperty) prop).getValueClass() != Axis.class);

		return (Axis) state.getValue(prop);
	}

	private static boolean isSingleSlab(BlockState state) {
		return state.getBlock() instanceof SlabBlock && state.getValue(TYPE) != SlabType.DOUBLE;
	}

	public static class AxialSlabPlacement {
		public final Axis axis;
		public final SlabType slabType;
		public final boolean waterlogged;

		private AxialSlabPlacement(Axis ax, SlabType type, boolean water) {
			axis = ax;
			slabType = type;
			waterlogged = water;
		}

		public static AxialSlabBlock.AxialSlabPlacement of(Axis ax, SlabType type, boolean water) {
			return new AxialSlabBlock.AxialSlabPlacement(ax, type, water);
		}
	}

	protected static class AxialSlabUseContext extends BlockItemUseContext {
		public AxialSlabUseContext(ItemUseContext context) {
			super(context);
		}

		protected AxialSlabUseContext(World w, PlayerEntity pl, Hand h, ItemStack stack, BlockRayTraceResult rayTrace) {
			super(w, pl, h, stack, rayTrace);
			BlockState state = getLevel().getBlockState(rayTrace.getBlockPos());
			if (state.getBlock() instanceof SlabBlock) {
				SlabBlock slabBlock = (SlabBlock) state.getBlock();
				VerticalOnlySlabBlock verticalSlab = VerticalOnlySlabBlock.getVerticalSlabFor(slabBlock);
				if (verticalSlab != null) {
					replaceClicked = verticalSlab.canBeReplaced(state, this);
				}
			}

		}

		public static AxialSlabBlock.AxialSlabUseContext makeReplacementContext(BlockItemUseContext context, BlockPos pos, Direction dir) {
			Vector3d blockVec = new Vector3d(pos.getX() + 0.5D + dir.getStepX() * 0.5D, pos.getY() + 0.5D + dir.getStepY() * 0.5D, pos.getZ() + 0.5D + dir.getStepZ() * 0.5D);
			BlockRayTraceResult rayTrace = new BlockRayTraceResult(blockVec, dir, pos, false);
			return new AxialSlabBlock.AxialSlabUseContext(context.getLevel(), context.getPlayer(), context.getHand(), context.getItemInHand(), rayTrace);
		}
	}

	private static final class DefaultImplWaterLoggable implements IWaterLoggable {
		private DefaultImplWaterLoggable() {
		}
	}
}
