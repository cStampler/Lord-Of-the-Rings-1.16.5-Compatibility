package lotr.common.block;

import java.util.Random;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class WateryTallFlowerBlock extends WaterloggableDoublePlantBlock implements IGrowable, IForgeBlockState {
	public WateryTallFlowerBlock() {
		CompostingHelper.prepareCompostable(this, 0.65F);
	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
		return false;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER && state.getValue(WATERLOGGED)) {
			return false;
		}
		return state.getValue(HALF) == DoubleBlockHalf.LOWER && world.getFluidState(pos.above()).getType() == Fluids.WATER ? false : super.canSurvive(state, world, pos);
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
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		return true;
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		popResource(world, pos, new ItemStack(this));
	}
}
