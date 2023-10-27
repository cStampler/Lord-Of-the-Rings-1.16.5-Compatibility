package lotr.common.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ThreeLeafCloverBlock extends CloverBlock implements IGrowable {
	public static final IntegerProperty NUM_CLOVERS;

	static {
		NUM_CLOVERS = LOTRBlockStates.CLOVERS_1_4;
	}

	public ThreeLeafCloverBlock() {
		registerDefaultState(defaultBlockState().setValue(NUM_CLOVERS, 1));
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
		if (useContext.getItemInHand().getItem() == asItem()) {
			int clovers = state.getValue(NUM_CLOVERS);
			if (clovers < 4) {
				BlockState oneMoreClover = state.setValue(NUM_CLOVERS, clovers + 1);
				if (oneMoreClover.canSurvive(useContext.getLevel(), useContext.getClickedPos())) {
					return true;
				}
			}
		}

		return super.canBeReplaced(state, useContext);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(NUM_CLOVERS);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		if (state.getBlock() == this) {
			int clovers = state.getValue(NUM_CLOVERS);
			return state.setValue(NUM_CLOVERS, Math.min(4, clovers + 1));
		}
		return super.getStateForPlacement(context);
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		int clovers = state.getValue(NUM_CLOVERS);
		return clovers < 4;
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		int clovers = state.getValue(NUM_CLOVERS);
		clovers += MathHelper.nextInt(rand, 1, 3);
		clovers = Math.min(clovers, 4);
		world.setBlock(pos, state.setValue(NUM_CLOVERS, clovers), 2);
	}
}
