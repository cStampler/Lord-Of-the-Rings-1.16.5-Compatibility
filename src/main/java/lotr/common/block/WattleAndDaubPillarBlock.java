package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class WattleAndDaubPillarBlock extends LOTRPillarBlock implements IForgeBlockState {
	public WattleAndDaubPillarBlock(Supplier blockSup) {
		super(Properties.copy((AbstractBlock) blockSup.get()));
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 40;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 40;
	}
}
