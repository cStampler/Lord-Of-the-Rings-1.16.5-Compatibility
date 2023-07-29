package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class LogSlabBlock extends AxialSlabBlock {
	public LogSlabBlock(Block block) {
		super(block);
	}

	public LogSlabBlock(Supplier block) {
		this((Block) block.get());
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}
}
