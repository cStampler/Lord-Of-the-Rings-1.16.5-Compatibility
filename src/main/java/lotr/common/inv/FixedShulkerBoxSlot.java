package lotr.common.inv;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ShulkerBoxSlot;
import net.minecraft.item.ItemStack;

public class FixedShulkerBoxSlot extends ShulkerBoxSlot {
	public FixedShulkerBoxSlot(IInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return super.mayPlace(stack) && PouchSlot.isItemValidForPouchSlotExcludingShulkerBoxes(stack);
	}
}
