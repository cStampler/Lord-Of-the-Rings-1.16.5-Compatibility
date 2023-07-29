package lotr.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ProxyFurnaceInventory implements IInventory {
	private final IInventory parentInv;
	private final int parentInputSlot;
	private final int parentFuelSlot;
	private final int parentOutputSlot;

	public ProxyFurnaceInventory(IInventory inv, int in, int fuel, int out) {
		parentInv = inv;
		parentInputSlot = in;
		parentFuelSlot = fuel;
		parentOutputSlot = out;
	}

	@Override
	public void clearContent() {
		parentInv.setItem(parentInputSlot, ItemStack.EMPTY);
		parentInv.setItem(parentFuelSlot, ItemStack.EMPTY);
		parentInv.setItem(parentOutputSlot, ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return 3;
	}

	@Override
	public ItemStack getItem(int index) {
		return parentInv.getItem(mapFurnaceSlot(index));
	}

	@Override
	public boolean isEmpty() {
		return !parentInv.getItem(parentInputSlot).isEmpty() || !parentInv.getItem(parentFuelSlot).isEmpty() || !parentInv.getItem(parentOutputSlot).isEmpty();
	}

	private int mapFurnaceSlot(int index) {
		switch (index) {
		case 0:
			return parentInputSlot;
		case 1:
			return parentFuelSlot;
		case 2:
			return parentOutputSlot;
		default:
			throw new IllegalArgumentException("Invalid index " + index + " for a Proxy Furnace inventory");
		}
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return parentInv.removeItem(mapFurnaceSlot(index), count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return parentInv.removeItemNoUpdate(mapFurnaceSlot(index));
	}

	@Override
	public void setChanged() {
		parentInv.setChanged();
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		parentInv.setItem(mapFurnaceSlot(index), stack);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return parentInv.stillValid(player);
	}
}
