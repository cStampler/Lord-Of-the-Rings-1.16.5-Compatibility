package lotr.common.inv;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;

public class ForgeFuelSlot extends Slot {
	private final AbstractAlloyForgeContainer container;

	public ForgeFuelSlot(AbstractAlloyForgeContainer forge, IInventory inv, int i, int x, int y) {
		super(inv, i, x, y);
		container = forge;
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return FurnaceFuelSlot.isBucket(stack) ? 1 : super.getMaxStackSize(stack);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return container.isFuel(stack) || FurnaceFuelSlot.isBucket(stack);
	}
}
