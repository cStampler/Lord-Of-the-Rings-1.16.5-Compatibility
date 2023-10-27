package lotr.common.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class DoubleableLOTRGrassBlock extends LOTRGrassBlock implements IGrowable {
	private final Supplier growableDoubleGrass;

	public DoubleableLOTRGrassBlock(Properties properties, Supplier doubleGrass) {
		super(properties);
		growableDoubleGrass = castDoublePlantSupplier(doubleGrass);
	}

	public DoubleableLOTRGrassBlock(Supplier doubleGrass) {
		growableDoubleGrass = castDoublePlantSupplier(doubleGrass);
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
		DoublePlantBlock doubleGrassBlock = (DoublePlantBlock) growableDoubleGrass.get();
		if (doubleGrassBlock.defaultBlockState().canSurvive(world, pos) && world.isEmptyBlock(pos.above())) {
			doubleGrassBlock.placeAt(world, pos, 2);
		}

	}

	private static Supplier castDoublePlantSupplier(Supplier doubleGrass) {
		return () -> doubleGrass.get();
	}
}
