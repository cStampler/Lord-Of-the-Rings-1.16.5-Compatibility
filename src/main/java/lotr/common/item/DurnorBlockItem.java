package lotr.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class DurnorBlockItem extends BlockItem {
	public DurnorBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public int getBurnTime(ItemStack itemstack) {
		return 6000;
	}
}
