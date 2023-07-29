package lotr.common.block;

import java.util.Random;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class RushesBlock extends WaterloggableDoublePlantBlock implements IGrowable, IForgeBlockState {
	private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

	public RushesBlock() {
		CompostingHelper.prepareCompostable(this, 0.5F);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		if (state.getValue(HALF) != DoubleBlockHalf.UPPER || !(Boolean) state.getValue(WATERLOGGED) && world.getFluidState(pos.below()).getType() == Fluids.WATER) {
			return state.getValue(HALF) != DoubleBlockHalf.LOWER || world.getFluidState(pos).getType() == Fluids.WATER && world.getFluidState(pos.above()).getType() != Fluids.WATER ? super.canSurvive(state, world, pos) : false;
		}
		return false;
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
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
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
	protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
		return super.mayPlaceOn(state, world, pos) || state.getBlock().is(BlockTags.SAND) || state.getBlock() instanceof QuagmireBlock;
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		int xzRange = 2;
		int yRange = 1;
		Mutable movingPos = new Mutable();
		int tries = 10;

		for (int l = 0; l < tries; ++l) {
			int x = MathHelper.nextInt(rand, -xzRange, xzRange);
			int y = MathHelper.nextInt(rand, -yRange, yRange);
			int z = MathHelper.nextInt(rand, -xzRange, xzRange);
			movingPos.set(pos).move(x, y, z);
			BlockState placeState = defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER);
			if (placeState.canSurvive(world, movingPos) && world.isEmptyBlock(movingPos.above())) {
				placeAt(world, movingPos, 3);
			}
		}

	}
}
