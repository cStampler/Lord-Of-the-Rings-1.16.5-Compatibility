package lotr.common.inv;

import lotr.common.init.LOTRContainers;
import lotr.common.item.VesselDrinkItem;
import lotr.common.recipe.DrinkBrewingRecipe;
import lotr.common.tileentity.KegTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class KegContainer extends Container {
	public final IInventory theKeg;
	private final IIntArray kegData;
	private final Slot brewResultSlot;

	public KegContainer(int id, PlayerInventory playerInv, IInventory keg, IIntArray data) {
		super(LOTRContainers.KEG.get(), id);
		checkContainerSize(keg, 10);
		checkContainerDataCount(data, 3);
		theKeg = keg;
		kegData = data;
		theKeg.startOpen(playerInv.player);
		int y;
		for (y = 0; y < 3; y++) {
			for (int i = 0; i < 3; i++) {
				KegSlot slot = new KegSlot(this, keg, i + y * 3, 14 + i * 18, 34 + y * 18);
				if (y == 2) {
					slot.setWaterSource();
				}
				addSlot(slot);
			}
		}
		addSlot(brewResultSlot = new KegResultSlot(keg, 9, 108, 52));
		for (y = 0; y < 3; y++) {
			for (int i = 0; i < 9; i++) {
				addSlot(new Slot(playerInv, i + y * 9 + 9, 25 + i * 18, 139 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInv, x, 25 + x * 18, 197));
		}
		addDataSlots(data);
	}

	public KegContainer(int id, PlayerInventory playerInv, PacketBuffer extraData) {
		this(id, playerInv, new Inventory(10), new IntArray(3));
	}

	public boolean canFinishBrewingNow() {
		ItemStack stack = getBrewingResult();
		if (stack.isEmpty()) {
			return false;
		}
		Item item = stack.getItem();
		if (item instanceof VesselDrinkItem) {
			return !VesselDrinkItem.getPotency(stack).isMin();
		}
		return true;
	}

	public int getBarrelFullAmountScaled(int i) {
		return getBrewingResult().getCount() * i / 16;
	}

	public ItemStack getBrewingResult() {
		return brewResultSlot.getItem();
	}

	public int getBrewProgressScaled(int i) {
		int fullTime = kegData.get(2);
		return fullTime == 0 ? 0 : kegData.get(1) * i / fullTime;
	}

	public VesselDrinkItem.Potency getInterruptBrewingPotency() {
		return VesselDrinkItem.getPotency(getBrewingResult()).getPrev();
	}

	public KegTileEntity.KegMode getKegMode() {
		return KegTileEntity.KegMode.forId(kegData.get(0));
	}

	public ITextComponent getKegSubtitle() {
		ItemStack brewingItem = getBrewingResult();
		KegTileEntity.KegMode mode = getKegMode();
		return (mode == KegTileEntity.KegMode.BREWING || mode == KegTileEntity.KegMode.FULL) && !brewingItem.isEmpty() ? new TranslationTextComponent("container.lotr.keg.item_subtitle", brewingItem.getHoverName(), VesselDrinkItem.getPotency(brewingItem).getDisplayName()) : StringTextComponent.EMPTY;
	}

	public ITextComponent getKegTitle() {
		KegTileEntity.KegMode mode = getKegMode();
		if (mode == KegTileEntity.KegMode.EMPTY) {
			return new TranslationTextComponent("container.lotr.keg.empty");
		}
		if (mode == KegTileEntity.KegMode.BREWING) {
			return new TranslationTextComponent("container.lotr.keg.brewing");
		}
		return mode == KegTileEntity.KegMode.FULL ? new TranslationTextComponent("container.lotr.keg.full") : StringTextComponent.EMPTY;
	}

	public VesselDrinkItem.Potency getMinimumPotency() {
		return VesselDrinkItem.Potency.getMin();
	}

	public void handleBrewButtonPress(ServerPlayerEntity player) {
		if (theKeg instanceof KegTileEntity) {
			((KegTileEntity) theKeg).handleBrewButtonPress(player);
		}

	}

	public boolean hasBrewingResult() {
		return brewResultSlot.hasItem();
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 9) {
				if (!moveItemStackTo(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index != 9) {
				boolean mergedIntoKeg = false;
				if (getKegMode() == KegTileEntity.KegMode.EMPTY) {
					if (DrinkBrewingRecipe.isWaterSource(itemstack1)) {
						mergedIntoKeg = moveItemStackTo(itemstack1, 6, 9, false);
					} else {
						mergedIntoKeg = moveItemStackTo(itemstack1, 0, 6, false);
					}
				}

				if (!mergedIntoKeg) {
					if (index >= 10 && index < 37) {
						if (!moveItemStackTo(itemstack1, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!moveItemStackTo(itemstack1, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		theKeg.stopOpen(player);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return theKeg.stillValid(player);
	}
}
