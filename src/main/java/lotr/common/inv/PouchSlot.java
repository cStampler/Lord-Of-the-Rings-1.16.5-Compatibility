package lotr.common.inv;

import lotr.common.item.PouchItem;
import net.minecraft.block.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class PouchSlot extends Slot {
	public PouchSlot(IInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return isItemValidForPouchSlot(stack);
	}

	public static boolean isItemValidForPouchSlot(ItemStack stack) {
		return isItemValidForPouchSlotExcludingShulkerBoxes(stack) && !(Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock);
	}

	public static boolean isItemValidForPouchSlotExcludingShulkerBoxes(ItemStack stack) {
		return !(stack.getItem() instanceof PouchItem);
	}
}
