package lotr.common.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MordorGravelBlock extends LOTRGravelBlock implements IGrowable {
	public MordorGravelBlock(MaterialColor materialColor, int dust) {
		super(materialColor, dust);
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		BlockPos abovePos = pos.above();
		return world.getBlockState(abovePos).isAir(world, abovePos);
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		MordorRockBlock.growMordorPlants(world, rand, pos, state);
	}
}
