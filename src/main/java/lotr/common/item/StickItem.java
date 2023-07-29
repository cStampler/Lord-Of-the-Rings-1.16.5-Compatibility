package lotr.common.item;

import net.minecraft.item.*;

public class StickItem extends Item {
	public StickItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getBurnTime(ItemStack itemstack) {
		return 100;
	}
}
