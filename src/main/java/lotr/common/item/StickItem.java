package lotr.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class StickItem extends Item {
	public StickItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getBurnTime(ItemStack itemstack) {
		return 100;
	}
}
