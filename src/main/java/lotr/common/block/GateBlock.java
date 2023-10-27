package lotr.common.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Maps;

import lotr.common.init.LOTRSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GateBlock extends Block implements IWaterLoggable {
	public static final DirectionProperty FACING;
	public static final BooleanProperty OPEN;
	public static final BooleanProperty POWERED;
	public static final BooleanProperty WATERLOGGED;
	public static final BooleanProperty UP;
	public static final BooleanProperty DOWN;
	public static final BooleanProperty NORTH;
	public static final BooleanProperty SOUTH;
	public static final BooleanProperty WEST;
	public static final BooleanProperty EAST;
	private static final Map CONNECTED_DIRECTION_TO_PROPERTY_MAP;
	protected static final int MAX_GATE_RANGE = 16;
	private static final VoxelShape TOP_SHAPE;
	private static final VoxelShape BOTTOM_SHAPE;
	private static final VoxelShape WEST_EAST_SHAPE;
	private static final VoxelShape NORTH_SOUTH_SHAPE;
	static {
		FACING = BlockStateProperties.FACING;
		OPEN = LOTRBlockStates.GATE_OPEN;
		POWERED = BlockStateProperties.POWERED;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		UP = BlockStateProperties.UP;
		DOWN = BlockStateProperties.DOWN;
		NORTH = BlockStateProperties.NORTH;
		SOUTH = BlockStateProperties.SOUTH;
		WEST = BlockStateProperties.WEST;
		EAST = BlockStateProperties.EAST;
		CONNECTED_DIRECTION_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), map -> {
			map.put(Direction.UP, UP);
			map.put(Direction.DOWN, DOWN);
			map.put(Direction.NORTH, NORTH);
			map.put(Direction.SOUTH, SOUTH);
			map.put(Direction.WEST, WEST);
			map.put(Direction.EAST, EAST);
		});
		TOP_SHAPE = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
		BOTTOM_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
		WEST_EAST_SHAPE = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
		NORTH_SOUTH_SHAPE = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	}
	public final boolean fullBlockGate = false;

	private final boolean isCutoutGate;

	public GateBlock(Properties properties, boolean cutout) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(POWERED, false).setValue(WATERLOGGED, false).setValue(UP, false).setValue(DOWN, false).setValue(NORTH, false).setValue(SOUTH, false).setValue(WEST, false).setValue(EAST, false));
		isCutoutGate = cutout;
	}

	private void activateGate(World world, BlockPos pos) {
		boolean wasOpen = isGateOpen(world, pos);
		this.activateGate(world, pos, !wasOpen);
	}

	private void activateGate(World world, BlockPos pos, boolean open) {
		getConnectedGatePositions(world, pos).forEach(p -> {
			setGateOpen(world, (BlockPos) p, open);
		});
		world.playSound((PlayerEntity) null, pos, getGateSound(open), SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FACING, OPEN, POWERED, WATERLOGGED, UP, DOWN, NORTH, SOUTH, WEST, EAST);
	}

	protected boolean directionsMatch(Direction facing1, Direction facing2) {
		if (facing1.getAxis() != Axis.Y && facing2.getAxis() != Axis.Y) {
			return facing1.getAxis() == facing2.getAxis();
		}
		return facing1 == facing2;
	}

	private void gatherAdjacentGate(World world, BlockPos pos, Direction originalFacing, boolean originalOpen, Set allCoords, Set currentDepthCoords) {
		if (!allCoords.contains(pos)) {
			BlockState stateHere = world.getBlockState(pos);
			Block blockHere = stateHere.getBlock();
			if (blockHere instanceof GateBlock) {
				GateBlock gateBlockHere = (GateBlock) blockHere;
				boolean openHere = isGateOpen(world, pos);
				Direction facingHere = getGateFacing(world, pos);
				if (openHere == originalOpen && directionsMatch(facingHere, originalFacing) && gateBlockHere.directionsMatch(facingHere, originalFacing)) {
					allCoords.add(pos);
					currentDepthCoords.add(pos);
				}
			}

		}
	}

	private void gatherAdjacentGates(World world, BlockPos pos, Direction originalFacing, boolean originalOpen, Set allCoords, Set currentDepthCoords) {
		Direction[] var7 = Direction.values();
		int var8 = var7.length;

		for (int var9 = 0; var9 < var8; ++var9) {
			Direction dir = var7[var9];
			if (dir.getAxis() != originalFacing.getAxis()) {
				gatherAdjacentGate(world, pos.relative(dir), originalFacing, originalOpen, allCoords, currentDepthCoords);
			}
		}

	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return state.getValue(OPEN) ? VoxelShapes.empty() : super.getCollisionShape(state, world, pos, context);
	}

	private List getConnectedGatePositions(World world, BlockPos pos) {
		boolean open = isGateOpen(world, pos);
		Direction facing = getGateFacing(world, pos);
		Set allCoords = new HashSet();
		Set lastDepthCoords = new HashSet();
		Set currentDepthCoords = new HashSet();

		for (int depth = 0; depth <= 16; ++depth) {
			if (depth == 0) {
				allCoords.add(pos);
				currentDepthCoords.add(pos);
			} else {
				Iterator var9 = lastDepthCoords.iterator();

				while (var9.hasNext()) {
					BlockPos coords = (BlockPos) var9.next();
					gatherAdjacentGates(world, coords, facing, open, allCoords, currentDepthCoords);
				}
			}

			lastDepthCoords.clear();
			lastDepthCoords.addAll(currentDepthCoords);
			currentDepthCoords.clear();
		}

		return new ArrayList(allCoords);
	}

	private Direction getDirectionForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Direction clickedSide = context.getClickedFace();
		BlockPos clickedOnPos = context.replacingClickedOnBlock() ? pos : pos.relative(clickedSide.getOpposite());
		BlockState clickedOnState = world.getBlockState(clickedOnPos);
		if (clickedOnState.getBlock() instanceof GateBlock && clickedOnState.getValue(FACING).getAxis() != clickedSide.getAxis()) {
			return clickedOnState.getValue(FACING);
		}
		Direction horizontalFacing = context.getHorizontalDirection();
		float pitch = Optional.ofNullable(context.getPlayer()).map(p -> p.xRot).orElse(0.0F);
		boolean lookingUp = pitch < -40.0F;
		boolean lookingDown = pitch > 40.0F;
		context.isSecondaryUseActive();
		if (clickedSide.getAxis().isVertical()) {
			return horizontalFacing;
		}
		if (!lookingUp && !lookingDown) {
			return clickedSide.getCounterClockWise();
		}
		return context.getClickLocation().y - pos.getY() < 0.5D ? Direction.DOWN : Direction.UP;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	private SoundEvent getGateSound(boolean open) {
		if (material == Material.STONE) {
			return open ? LOTRSoundEvents.STONE_GATE_OPEN : LOTRSoundEvents.STONE_GATE_CLOSE;
		}
		return open ? LOTRSoundEvents.GATE_OPEN : LOTRSoundEvents.GATE_CLOSE;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		if (state.getValue(FACING) == Direction.UP) {
			return TOP_SHAPE;
		}
		if (state.getValue(FACING) == Direction.DOWN) {
			return BOTTOM_SHAPE;
		}
		return state.getValue(FACING).getAxis() == Axis.X ? WEST_EAST_SHAPE : NORTH_SOUTH_SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		boolean powered = context.getLevel().hasNeighborSignal(context.getClickedPos());
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, getDirectionForPlacement(context)).setValue(POWERED, powered).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return type != PathType.LAND && type != PathType.AIR ? super.isPathfindable(state, world, pos, type) : (Boolean) state.getValue(OPEN);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block changedBlock, BlockPos changedPos, boolean isMoving) {
		if (!world.isClientSide && !(changedBlock instanceof GateBlock)) {
			boolean powered = world.hasNeighborSignal(pos);
			if (state.getValue(POWERED) != powered) {
				world.setBlockAndUpdate(pos, state.setValue(POWERED, powered));
				if (isGateOpen(world, pos) != powered) {
					this.activateGate(world, pos, powered);
				}
			}
		}

	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
		Block block = adjacentState.getBlock();
		if (block instanceof GateBlock) {
			GateBlock otherGateBlock = (GateBlock) block;
			if (isCutoutGate == otherGateBlock.isCutoutGate) {
				Direction thisFacing = state.getValue(FACING);
				Direction otherFacing = adjacentState.getValue(FACING);
				boolean thisOpen = state.getValue(OPEN);
				boolean otherOpen = adjacentState.getValue(OPEN);
				boolean connectToSide = !directionsMatch(thisFacing, side);
				if (connectToSide) {
					return thisOpen == otherOpen && directionsMatch(thisFacing, otherFacing) && otherGateBlock.directionsMatch(thisFacing, otherFacing);
				}
			}
		}

		return super.skipRendering(state, adjacentState, side);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		state = state.setValue((Property) CONNECTED_DIRECTION_TO_PROPERTY_MAP.get(facing), doBlocksConnectVisually(state, facingState, Collections.singletonList(facing)));
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult target) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (!heldItem.isEmpty()) {
			Item item = heldItem.getItem();
			if (Block.byItem(item) instanceof GateBlock) {
				return ActionResultType.PASS;
			}
		}

		if (!world.isClientSide) {
			this.activateGate(world, pos);
		}

		return ActionResultType.SUCCESS;
	}

	public static boolean doBlocksConnectVisually(BlockState state, BlockState otherState, List connectOffsets) {
		Block block = state.getBlock();
		Block otherBlock = otherState.getBlock();
		if (!(block instanceof GateBlock)) {
			return true;
		}
		GateBlock gateBlock = (GateBlock) block;
		Direction gateDir = state.getValue(FACING);
		boolean open = state.getValue(OPEN);
		if (connectOffsets.stream().anyMatch(dir -> (((Direction) dir).getAxis() == gateDir.getAxis()))) {
			return false;
		}
		if (!(otherBlock instanceof GateBlock)) {
			return gateDir.getAxis().isHorizontal() && open && connectOffsets.contains(Direction.DOWN);
		}
		GateBlock otherGateBlock = (GateBlock) otherBlock;
		Direction otherGateDir = otherState.getValue(FACING);
		boolean otherOpen = otherState.getValue(OPEN);
		boolean connectToOtherGate = open || otherGateBlock == gateBlock;
		return connectToOtherGate && open == otherOpen && gateBlock.directionsMatch(gateDir, otherGateDir) && otherGateBlock.directionsMatch(gateDir, otherGateDir);
	}

	private static Direction getGateFacing(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos).getValue(FACING);
	}

	private static boolean isGateOpen(IBlockReader world, BlockPos pos) {
		return world.getBlockState(pos).getValue(OPEN);
	}

	public static GateBlock makeMetal() {
		return makeMetal(false);
	}

	private static GateBlock makeMetal(boolean cutout) {
		return new GateBlock(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.METAL).noOcclusion(), cutout);
	}

	public static GateBlock makeMetalCutout() {
		return makeMetal(true);
	}

	public static GateBlock makeStone() {
		return new GateBlock(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(4.0F, 6.0F).sound(SoundType.STONE).noOcclusion(), false);
	}

	public static GateBlock makeWooden() {
		return makeWooden(false);
	}

	private static GateBlock makeWooden(boolean cutout) {
		return new GateBlock(Properties.of(Material.WOOD).strength(4.0F, 3.0F).sound(SoundType.WOOD).noOcclusion(), cutout);
	}

	public static GateBlock makeWoodenCutout() {
		return makeWooden(true);
	}

	private static void setGateOpen(IWorld world, BlockPos pos, boolean open) {
		BlockState state = world.getBlockState(pos).setValue(OPEN, open);
		state = Block.updateFromNeighbourShapes(state, world, pos);
		world.setBlock(pos, state, 3);
	}
}
