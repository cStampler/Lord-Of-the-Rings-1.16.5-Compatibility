package lotr.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;

public class FourLeafCloverBlock extends CloverBlock {
	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
		return false;
	}
}
