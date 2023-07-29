package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.*;

public class ChalkPathBlock extends LOTRPathBlock {
	public ChalkPathBlock(MaterialColor color, float hard, float res) {
		super(Properties.of(Material.STONE, color).strength(hard, res).requiresCorrectToolForDrops());
	}

	@Override
	protected BlockState getUnpathedBlockState() {
		return ((Block) LOTRBlocks.DIRTY_CHALK.get()).defaultBlockState();
	}
}
