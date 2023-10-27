package lotr.common.entity.npc.util;

import java.util.Random;

import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;

public class LeatherDyeUtil {
	public static ItemStack dyeLeather(Item item, int color) {
		return dyeLeather(new ItemStack(item), color);
	}

	public static ItemStack dyeLeather(Item item, int[] colors, Random rand) {
		return dyeLeather(new ItemStack(item), colors, rand);
	}

	public static ItemStack dyeLeather(ItemStack itemstack, int color) {
		Item item = itemstack.getItem();
		if (!(item instanceof DyeableArmorItem)) {
			throw new IllegalArgumentException("Cannot call dyeLeather on item " + item.getRegistryName() + " as it is not a DyeableArmorItem!");
		}
		((DyeableArmorItem) item).setColor(itemstack, color);
		return itemstack;
	}

	public static ItemStack dyeLeather(ItemStack itemstack, int[] colors, Random rand) {
		return dyeLeather(itemstack, Util.getRandom(colors, rand));
	}
}
