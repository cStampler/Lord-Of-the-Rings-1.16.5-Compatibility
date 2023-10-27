package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class ChalkPathBlock extends LOTRPathBlock {
	public ChalkPathBlock(MaterialColor color, float hard, float res) {
		super(Properties.of(Material.STONE, color).strength(hard, res).requiresCorrectToolForDrops());
	}

	@Override
	protected BlockState getUnpathedBlockState() {
		return ((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState();
	}
}
