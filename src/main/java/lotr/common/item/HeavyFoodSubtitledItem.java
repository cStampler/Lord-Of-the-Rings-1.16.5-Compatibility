package lotr.common.item;

import net.minecraft.item.ItemStack;

public class HeavyFoodSubtitledItem extends SubtitledItem {
	public HeavyFoodSubtitledItem(Properties properties) {
		super(properties);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return super.getUseDuration(stack) * 2;
	}
}
