package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

public abstract class NonWaterloggableLanternBlock extends Block {
	public static final BooleanProperty HANGING;

	static {
		HANGING = BlockStateProperties.HANGING;
	}

	public NonWaterloggableLanternBlock(int light) {
		this(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(LOTRBlocks.constantLight(light)).noOcclusion());
	}

	public NonWaterloggableLanternBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(HANGING, false));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		Direction dir = getBlockConnected(state).getOpposite();
		return Block.canSupportCenter(world, pos.relative(dir), dir.getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(HANGING);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		context.getLevel().getFluidState(context.getClickedPos());
		Direction[] var3 = context.getNearestLookingDirections();
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			Direction dir = var3[var5];
			if (dir.getAxis() == Axis.Y) {
				BlockState state = defaultBlockState().setValue(HANGING, dir == Direction.UP);
				if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
					return state;
				}
			}
		}

		return null;
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return getBlockConnected(state).getOpposite() == facing && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	protected static Direction getBlockConnected(BlockState state) {
		return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
	}
}
