package lotr.common.inv;

import net.minecraft.item.ItemStack;

public interface OpenPouchContainer {
	int getPouchCapacity();

	PouchInventory getPouchInventory();

	boolean isOpenPouch(ItemStack var1);

	void reloadPouchFromPickup();
}
