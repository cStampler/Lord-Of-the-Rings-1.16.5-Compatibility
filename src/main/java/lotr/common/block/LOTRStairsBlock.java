package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRStairsBlock extends StairsBlock implements IForgeBlockState {
	public LOTRStairsBlock(Block block) {
		super(() -> block.defaultBlockState(), Properties.copy(block));
	}

	public LOTRStairsBlock(Supplier block) {
		this((Block) block.get());
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}
}
