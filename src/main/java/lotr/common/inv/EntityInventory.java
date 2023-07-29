package lotr.common.inv;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

public class EntityInventory extends Inventory {
	protected final LivingEntity theEntity;
	private final String nbtName;

	public EntityInventory(LivingEntity entity, int size, String name) {
		super(size);
		theEntity = entity;
		nbtName = name;
	}

	@Override
	public final ListNBT createTag() {
		ListNBT items = new ListNBT();

		for (int i = 0; i < getContainerSize(); ++i) {
			ItemStack itemstack = getItem(i);
			if (!itemstack.isEmpty()) {
				CompoundNBT itemData = new CompoundNBT();
				itemData.putByte("Slot", (byte) i);
				itemstack.save(itemData);
				items.add(itemData);
			}
		}

		return items;
	}

	protected void doDropItem(ItemStack itemstack) {
		theEntity.spawnAtLocation(itemstack, 0.0F);
	}

	public final void dropAllItems() {
		for (int i = 0; i < getContainerSize(); ++i) {
			ItemStack itemstack = getItem(i);
			if (!itemstack.isEmpty()) {
				doDropItem(itemstack);
				setItem(i, ItemStack.EMPTY);
			}
		}

	}

	@Override
	public final void fromTag(ListNBT items) {
		for (int i = 0; i < items.size(); ++i) {
			CompoundNBT itemData = items.getCompound(i);
			int slot = itemData.getByte("Slot");
			if (slot >= 0 && slot < getContainerSize()) {
				setItem(slot, ItemStack.of(itemData));
			}
		}

	}

	public boolean isFull() {
		return !isEmpty();
	}

	public void readFromEntityNBT(CompoundNBT nbt) {
		fromTag(nbt.getList(nbtName, 10));
	}

	public void writeToEntityNBT(CompoundNBT nbt) {
		nbt.put(nbtName, createTag());
	}
}
