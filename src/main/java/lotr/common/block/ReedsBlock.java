package lotr.common.block;

import java.util.Random;

import lotr.common.event.CompostingHelper;
import lotr.common.init.LOTRTags;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.*;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class ReedsBlock extends Block implements IWaterLoggable, IGrowable, IForgeBlockState {
	public static final EnumProperty REEDS_TYPE;
	public static final IntegerProperty AGE;
	public static final BooleanProperty WATERLOGGED;
	private static final VoxelShape SHAPE;
	static {
		REEDS_TYPE = LOTRBlockStates.REEDS_TYPE;
		AGE = BlockStateProperties.AGE_15;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
	}
	private final boolean canReedGrow;

	private final boolean canPlaceByIce;

	public ReedsBlock() {
		this(true);
	}

	protected ReedsBlock(boolean canGrow) {
		this(Properties.of(Material.PLANT).noCollission().randomTicks().strength(0.0F).sound(SoundType.GRASS), canGrow);
	}

	protected ReedsBlock(Properties properties, boolean canGrow) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(REEDS_TYPE, ReedsBlock.Type.ONE).setValue(AGE, 0).setValue(WATERLOGGED, false));
		canReedGrow = canGrow;
		canPlaceByIce = !canGrow;
		CompostingHelper.prepareCompostable(this, 0.5F);
	}

	private boolean canReedGrowUpwards(IBlockReader world, BlockPos pos, BlockState state) {
		ReedsBlock.Type reedType = (ReedsBlock.Type) state.getValue(REEDS_TYPE);
		if (reedType == ReedsBlock.Type.ONE || reedType == ReedsBlock.Type.TWO_TOP) {
			BlockPos abovePos = pos.above();
			if (world.getBlockState(abovePos).isAir(world, abovePos)) {
				return true;
			}

			BlockPos twoAbovePos = abovePos.above();
			if (world.getFluidState(abovePos).getType() == Fluids.WATER && world.getBlockState(twoAbovePos).isAir(world, twoAbovePos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		ReedsBlock.Type reedType = (ReedsBlock.Type) state.getValue(REEDS_TYPE);
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		if (reedType != ReedsBlock.Type.ONE && reedType != ReedsBlock.Type.TWO_BOTTOM && reedType != ReedsBlock.Type.THREE_BOTTOM) {
			if (reedType == ReedsBlock.Type.THREE_TOP && state.getValue(WATERLOGGED)) {
				return false;
			}
			return belowState.getBlock() == this;
		}
		if (belowState.is(LOTRTags.Blocks.REEDS_PLACEABLE_ON)) {
			if (world.getFluidState(pos).getType() == Fluids.WATER) {
				boolean canPotentiallyReachAir = false;
				if (isAirOrReedsInAir(world, pos.above()) || isWaterOrReedsInWater(world, pos.above()) && isAirOrReedsInAir(world, pos.above(2))) {
					canPotentiallyReachAir = true;
				}

				return canPotentiallyReachAir;
			}

			for (Direction horizontalDir : Plane.HORIZONTAL) {
				BlockState adjacentBelowState = world.getBlockState(belowPos.relative(horizontalDir));
				FluidState fluid = adjacentBelowState.getFluidState();
				if (fluid.is(FluidTags.WATER) || adjacentBelowState.getBlock() == Blocks.FROSTED_ICE || canPlaceByIce && adjacentBelowState.getMaterial() == Material.ICE) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(REEDS_TYPE, AGE, WATERLOGGED);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState placeState = defaultBlockState();
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState belowState = world.getBlockState(pos.below());
		if (belowState.getBlock() == this) {
			ReedsBlock.Type reedType = (ReedsBlock.Type) belowState.getValue(REEDS_TYPE);
			if (reedType == ReedsBlock.Type.ONE) {
				placeState = placeState.setValue(REEDS_TYPE, ReedsBlock.Type.TWO_TOP);
			} else if (reedType == ReedsBlock.Type.TWO_TOP) {
				placeState = placeState.setValue(REEDS_TYPE, ReedsBlock.Type.THREE_TOP);
			}
		}

		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return placeState.setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}

	private void growReedAbove(World world, BlockPos pos, BlockState state) {
		BlockPos abovePos = pos.above();
		BlockState growAboveState = defaultBlockState().setValue(WATERLOGGED, world.getFluidState(abovePos).getType() == Fluids.WATER);
		ReedsBlock.Type reedType = (ReedsBlock.Type) state.getValue(REEDS_TYPE);
		if (reedType == ReedsBlock.Type.ONE) {
			world.setBlockAndUpdate(pos.above(), growAboveState.setValue(REEDS_TYPE, ReedsBlock.Type.TWO_TOP));
		} else if (reedType == ReedsBlock.Type.TWO_TOP) {
			world.setBlockAndUpdate(pos.above(), growAboveState.setValue(REEDS_TYPE, ReedsBlock.Type.THREE_TOP));
		}

		BlockState updatedStateHere = world.getBlockState(pos);
		world.setBlock(pos, updatedStateHere.setValue(AGE, 0), 4);
	}

	private boolean isAirOrReedsInAir(IWorldReader world, BlockPos pos) {
		if (world.isEmptyBlock(pos)) {
			return true;
		}
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this && !(Boolean) state.getValue(WATERLOGGED);
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		return canReedGrow && canReedGrowUpwards(world, pos, state);
	}

	private boolean isWaterOrReedsInWater(IWorldReader world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getFluidState().getType() == Fluids.WATER) {
			return true;
		}
		return state.getBlock() == this && state.getValue(WATERLOGGED);
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		int age = state.getValue(AGE);
		age += MathHelper.nextInt(rand, 7, 15);
		if (age >= 15) {
			growReedAbove(world, pos, state);
			int ageRemaining = age - 15;
			if (ageRemaining > 0) {
				BlockPos abovePos = pos.above();
				BlockState aboveState = world.getBlockState(abovePos);
				if (canReedGrowUpwards(world, abovePos, aboveState)) {
					int aboveAge = aboveState.getValue(AGE);
					aboveAge += ageRemaining;
					world.setBlock(abovePos, aboveState.setValue(AGE, aboveAge), 4);
				}
			}
		} else {
			world.setBlock(pos, state.setValue(AGE, age), 4);
		}

	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		if (!state.canSurvive(world, pos)) {
			world.destroyBlock(pos, true);
		} else if (canReedGrow && canReedGrowUpwards(world, pos, state)) {
			int age = state.getValue(AGE);
			if (ForgeHooks.onCropsGrowPre(world, pos, state, true)) {
				if (age == 15) {
					growReedAbove(world, pos, state);
				} else {
					world.setBlock(pos, state.setValue(AGE, age + 1), 4);
				}

				ForgeHooks.onCropsGrowPost(world, pos, state);
			}
		}

	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (!state.canSurvive(world, currentPos)) {
			world.getBlockTicks().scheduleTick(currentPos, this, 1);
		} else if (facing == Direction.UP) {
			ReedsBlock.Type thisReedType = (ReedsBlock.Type) state.getValue(REEDS_TYPE);
			if (facingState.getBlock() == this) {
				ReedsBlock.Type aboveReedType = (ReedsBlock.Type) facingState.getValue(REEDS_TYPE);
				if (thisReedType == ReedsBlock.Type.ONE && aboveReedType == ReedsBlock.Type.TWO_TOP) {
					return state.setValue(REEDS_TYPE, ReedsBlock.Type.TWO_BOTTOM);
				}

				if (thisReedType == ReedsBlock.Type.TWO_TOP && aboveReedType == ReedsBlock.Type.THREE_TOP) {
					return state.setValue(REEDS_TYPE, ReedsBlock.Type.THREE_MIDDLE);
				}

				if (thisReedType == ReedsBlock.Type.TWO_BOTTOM && aboveReedType == ReedsBlock.Type.THREE_MIDDLE) {
					return state.setValue(REEDS_TYPE, ReedsBlock.Type.THREE_BOTTOM);
				}

				if (thisReedType == ReedsBlock.Type.THREE_BOTTOM && aboveReedType == ReedsBlock.Type.TWO_TOP) {
					return state.setValue(REEDS_TYPE, ReedsBlock.Type.TWO_BOTTOM);
				}
			} else {
				if (thisReedType == ReedsBlock.Type.TWO_BOTTOM || thisReedType == ReedsBlock.Type.THREE_BOTTOM) {
					return state.setValue(REEDS_TYPE, ReedsBlock.Type.ONE);
				}

				if (thisReedType == ReedsBlock.Type.THREE_MIDDLE) {
					return state.setValue(REEDS_TYPE, ReedsBlock.Type.TWO_TOP);
				}
			}
		}

		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	public enum Type implements IStringSerializable {
		ONE("1"), TWO_BOTTOM("2_bottom"), TWO_TOP("2_top"), THREE_BOTTOM("3_bottom"), THREE_MIDDLE("3_middle"), THREE_TOP("3_top");

		private final String typeName;

		Type(String s) {
			typeName = s;
		}

		@Override
		public String getSerializedName() {
			return typeName;
		}
	}
}
