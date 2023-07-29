package lotr.common.block;

import lotr.common.init.LOTRTags;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class LOTRPillarBlock extends RotatedPillarBlock {
	public static final BooleanProperty ABOVE;
	public static final BooleanProperty BELOW;

	static {
		ABOVE = LOTRBlockStates.PILLAR_ABOVE;
		BELOW = LOTRBlockStates.PILLAR_BELOW;
	}

	public LOTRPillarBlock(MaterialColor materialColor) {
		this(materialColor, 1.5F, 6.0F);
	}

	public LOTRPillarBlock(MaterialColor materialColor, float hard, float res) {
		this(Properties.of(Material.STONE, materialColor).requiresCorrectToolForDrops().strength(hard, res));
	}

	public LOTRPillarBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(ABOVE, false).setValue(BELOW, false));
	}

	private BlockState checkAdjacentPillars(BlockState state, Direction dir, BlockState facingState) {
		Axis pillarAxis = state.getValue(AXIS);
		if (dir.getAxis() == pillarAxis) {
			AxisDirection axisDir = dir.getAxisDirection();
			boolean matchDir = false;
			if (facingState.is(LOTRTags.Blocks.PILLARS)) {
				if (facingState.hasProperty(AXIS)) {
					matchDir = facingState.getValue(AXIS) == pillarAxis;
				} else {
					matchDir = true;
				}
			}

			if (axisDir == AxisDirection.POSITIVE) {
				return state.setValue(ABOVE, matchDir);
			}

			if (axisDir == AxisDirection.NEGATIVE) {
				return state.setValue(BELOW, matchDir);
			}
		}

		return state;
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ABOVE);
		builder.add(BELOW);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		BlockPos pos = context.getClickedPos();
		IWorld world = context.getLevel();
		Direction[] var5 = Direction.values();
		int var6 = var5.length;

		for (int var7 = 0; var7 < var6; ++var7) {
			Direction dir = var5[var7];
			BlockPos facingPos = pos.relative(dir);
			state = checkAdjacentPillars(state, dir, world.getBlockState(facingPos));
		}

		return state;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return checkAdjacentPillars(state, dir, facingState);
	}
}
