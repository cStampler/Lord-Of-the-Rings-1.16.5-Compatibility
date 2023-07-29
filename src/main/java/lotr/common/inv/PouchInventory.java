package lotr.common.inv;

import java.util.List;

import lotr.common.item.PouchItem;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class PouchInventory extends Inventory {
	private final ItemStack pouch;
	private final boolean isWritable;
	private boolean enableWriting = true;

	private PouchInventory(ItemStack pouch, boolean isWritable) {
		super(getCapacity(pouch));
		this.pouch = pouch;
		this.isWritable = isWritable;
		enableWriting = false;
		loadPouchContents();
		enableWriting = true;
	}

	public void fillPouchFromList(List contents) {
		if (contents.size() > getContainerSize()) {
			throw new IllegalArgumentException("Too many items (" + contents.size() + ") for a pouch of size " + getContainerSize());
		}
		enableWriting = false;

		for (int i = 0; i < contents.size(); ++i) {
			setItem(i, (ItemStack) contents.get(i));
		}

		enableWriting = true;
		setChanged();
	}

	public int getNumSlotsFull() {
		int slotsFull = 0;

		for (int i = 0; i < getContainerSize(); ++i) {
			ItemStack stack = getItem(i);
			if (!stack.isEmpty()) {
				++slotsFull;
			}
		}

		return slotsFull;
	}

	private void loadPouchContents() {
		CompoundNBT pouchNBT = PouchItem.getOrCreatePouchRootNBT(pouch);
		NonNullList temp = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(pouchNBT, temp);

		for (int i = 0; i < getContainerSize(); ++i) {
			setItem(i, (ItemStack) temp.get(i));
		}

	}

	public void reloadFromItemNBT() {
		loadPouchContents();
	}

	private void savePouchContents() {
		NonNullList temp = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < getContainerSize(); ++i) {
			temp.set(i, getItem(i));
		}

		CompoundNBT pouchNBT = PouchItem.getOrCreatePouchRootNBT(pouch);
		ItemStackHelper.saveAllItems(pouchNBT, temp, true);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (isWritable && enableWriting) {
			savePouchContents();
		}

	}

	private static int getCapacity(ItemStack pouch) {
		if (pouch.getItem() instanceof PouchItem) {
			return ((PouchItem) pouch.getItem()).getCapacity();
		}
		throw new IllegalArgumentException("Item " + pouch.getItem().getRegistryName() + " does not contain a pouch inventory!");
	}

	public static PouchInventory temporaryReadOnly(ItemStack pouch) {
		return new PouchInventory(pouch, false);
	}

	public static PouchInventory temporaryWritable(ItemStack pouch) {
		return new PouchInventory(pouch, true);
	}

	public static PouchInventory worldSidedInventory(ItemStack pouch, World world) {
		return new PouchInventory(pouch, !world.isClientSide);
	}
}
