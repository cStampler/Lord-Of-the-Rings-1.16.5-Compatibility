package lotr.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class VesselOperations {
	public static ItemStack getEquivalentDrink(ItemStack itemstack) {
		return itemstack;
	}

	public static ItemStack getRealDrink(ItemStack itemstack) {
		return itemstack;
	}

	public static VesselType getVessel(ItemStack itemstack) {
		Item item = itemstack.getItem();
		if (item instanceof IEmptyVesselItem) {
			return ((IEmptyVesselItem) item).getVesselType();
		}
		return item instanceof VesselDrinkItem ? VesselDrinkItem.getVessel(itemstack) : VesselType.WOODEN_MUG;
	}

	public static ItemStack getWithVesselSet(ItemStack itemstack, VesselType vesselType, boolean correctItem) {
		if (isItemEmptyVessel(itemstack)) {
			return getVessel(itemstack).createEmpty();
		}
		if (isItemFullVessel(itemstack)) {
			ItemStack copy = itemstack.copy();
			if (correctItem) {
			}

			VesselDrinkItem.setVessel(copy, vesselType);
			return copy;
		}
		return itemstack;
	}

	public static boolean isItemEmptyVessel(ItemStack itemstack) {
		return itemstack.getItem() instanceof IEmptyVesselItem;
	}

	public static boolean isItemFullVessel(ItemStack itemstack) {
		return itemstack.getItem() instanceof VesselDrinkItem;
	}
}
