package lotr.common.block;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import lotr.common.init.LOTRTileEntities;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class CustomWaypointMarkerBlock extends Block {
	public static final DirectionProperty FACING;
	private static final Map SHAPES;

	static {
		FACING = BlockStateProperties.HORIZONTAL_FACING;
		SHAPES = new EnumMap(ImmutableMap.of(Direction.NORTH, Block.box(0.0D, 1.5D, 14.5D, 16.0D, 14.5D, 16.0D), Direction.SOUTH, Block.box(0.0D, 1.5D, 0.0D, 16.0D, 14.5D, 1.5D), Direction.EAST, Block.box(0.0D, 1.5D, 0.0D, 1.5D, 14.5D, 16.0D), Direction.WEST, Block.box(14.5D, 1.5D, 0.0D, 16.0D, 14.5D, 16.0D)));
	}

	public CustomWaypointMarkerBlock() {
		super(Properties.of(Material.WOOD).noCollission().strength(-1.0F, 3600000.0F).noDrops().sound(SoundType.WOOD));
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Direction facing = state.getValue(FACING);
		BlockPos attachedPos = pos.relative(facing.getOpposite());
		return world.getBlockState(attachedPos).isFaceSturdy(world, attachedPos, facing);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FACING);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.CUSTOM_WAYPOINT_MARKER.get()).create();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return (VoxelShape) SHAPES.get(state.getValue(FACING));
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof CustomWaypointMarkerTileEntity) {
				CustomWaypointMarkerTileEntity marker = (CustomWaypointMarkerTileEntity) te;
				marker.recreateAndDropItemFrame(state);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}

	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return facing.getOpposite() == state.getValue(FACING) && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}
