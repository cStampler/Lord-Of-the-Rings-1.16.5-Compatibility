package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.fluid.*;
import net.minecraft.item.*;
import net.minecraft.state.*;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;

public class CrystalBlock extends Block implements IWaterLoggable, IBeaconBeamColorProvider {
	public static final EnumProperty CRYSTAL_FACING;
	public static final BooleanProperty WATERLOGGED;
	private static final VoxelShape SHAPE_UP;
	private static final VoxelShape SHAPE_DOWN;
	private static final VoxelShape SHAPE_WEST;
	private static final VoxelShape SHAPE_EAST;
	private static final VoxelShape SHAPE_NORTH;
	private static final VoxelShape SHAPE_SOUTH;
	static {
		CRYSTAL_FACING = BlockStateProperties.FACING;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		SHAPE_UP = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);
		SHAPE_DOWN = Block.box(2.0D, 1.0D, 2.0D, 14.0D, 16.0D, 14.0D);
		SHAPE_WEST = Block.box(1.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);
		SHAPE_EAST = Block.box(0.0D, 2.0D, 2.0D, 15.0D, 14.0D, 14.0D);
		SHAPE_NORTH = Block.box(2.0D, 2.0D, 1.0D, 14.0D, 14.0D, 16.0D);
		SHAPE_SOUTH = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 15.0D);
	}

	private final DyeColor beaconBeamColor;

	public CrystalBlock(int light, int harvestLvl, DyeColor color) {
		this(Properties.of(LOTRBlockMaterial.CRYSTAL).requiresCorrectToolForDrops().strength(3.0F, 3.0F).noOcclusion().lightLevel(LOTRBlocks.constantLight(light)).sound(SoundType.GLASS).harvestTool(ToolType.PICKAXE).harvestLevel(harvestLvl), color);
	}

	public CrystalBlock(Properties properties, DyeColor color) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(CRYSTAL_FACING, Direction.UP).setValue(WATERLOGGED, false));
		beaconBeamColor = color;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Direction crystalFacing = (Direction) state.getValue(CRYSTAL_FACING);
		BlockPos supportPos = pos.relative(crystalFacing.getOpposite());
		return LOTRUtil.hasSolidSide(world, supportPos, crystalFacing);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(CRYSTAL_FACING, WATERLOGGED);
	}

	@Override
	public DyeColor getColor() {
		return beaconBeamColor;
	}

	@Override
	public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? getExperience(RANDOM) : 0;
	}

	protected int getExperience(Random rand) {
		if (this == LOTRBlocks.GLOWSTONE_CRYSTAL.get()) {
			return MathHelper.nextInt(rand, 2, 4);
		}
		if (this == LOTRBlocks.EDHELVIR_CRYSTAL.get()) {
			return MathHelper.nextInt(rand, 2, 5);
		}
		return this == LOTRBlocks.GULDURIL_CRYSTAL.get() ? MathHelper.nextInt(rand, 2, 5) : 0;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Direction crystalFacing = (Direction) state.getValue(CRYSTAL_FACING);
		switch (crystalFacing) {
		case UP:
		default:
			return SHAPE_UP;
		case DOWN:
			return SHAPE_DOWN;
		case WEST:
			return SHAPE_WEST;
		case EAST:
			return SHAPE_EAST;
		case NORTH:
			return SHAPE_NORTH;
		case SOUTH:
			return SHAPE_SOUTH;
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(CRYSTAL_FACING, context.getClickedFace()).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation((Direction) state.getValue(CRYSTAL_FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(CRYSTAL_FACING, rot.rotate((Direction) state.getValue(CRYSTAL_FACING)));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		Direction crystalFacing = (Direction) state.getValue(CRYSTAL_FACING);
		return facing == crystalFacing.getOpposite() && !canSurvive(state, world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}
