package lotr.common.item;

import net.minecraft.item.SoupItem;
import net.minecraft.util.*;

public class SyrupItem extends SoupItem {
	public SyrupItem(Properties properties) {
		super(properties);
	}

	@Override
	public SoundEvent getDrinkingSound() {
		return SoundEvents.HONEY_DRINK;
	}

	@Override
	public SoundEvent getEatingSound() {
		return SoundEvents.HONEY_DRINK;
	}
}
