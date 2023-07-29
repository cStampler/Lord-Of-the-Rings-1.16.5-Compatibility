package lotr.common.item;

import net.minecraft.item.*;

public class DurnorItem extends Item {
	public DurnorItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getBurnTime(ItemStack itemstack) {
		return 600;
	}
}
