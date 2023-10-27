package lotr.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class DurnorItem extends Item {
	public DurnorItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getBurnTime(ItemStack itemstack) {
		return 600;
	}
}
