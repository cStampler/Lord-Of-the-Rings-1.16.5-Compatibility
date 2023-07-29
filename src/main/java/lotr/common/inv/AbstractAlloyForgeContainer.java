package lotr.common.inv;

import lotr.common.tileentity.AbstractAlloyForgeTileEntity;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;

public abstract class AbstractAlloyForgeContainer extends Container {
	private final AbstractAlloyForgeTileEntity theForge;
	private final IIntArray forgeData;
	protected final World world;
	private final int forgeSlots;

	public AbstractAlloyForgeContainer(ContainerType type, int id, PlayerInventory playerInv, AbstractAlloyForgeTileEntity forge) {
		this(type, id, playerInv, forge, new IntArray(4));
	}

	public AbstractAlloyForgeContainer(ContainerType type, int id, PlayerInventory playerInv, AbstractAlloyForgeTileEntity forge, IIntArray data) {
		super(type, id);

		checkContainerSize(forge, 13);
		checkContainerDataCount(data, 4);
		theForge = forge;
		forgeData = data;
		world = playerInv.player.level;
		forgeSlots = 4;
		int i;
		for (i = 0; i < forgeSlots; i++) {
			addSlot(new Slot(theForge, i, 53 + i * 18, 39));
		}
		for (i = 0; i < forgeSlots; i++) {
			addSlot(new Slot(theForge, i + forgeSlots, 53 + i * 18, 21));
		}
		for (i = 0; i < forgeSlots; i++) {
			addSlot(new ForgeResultSlot(playerInv.player, theForge, i + forgeSlots * 2, 53 + i * 18, 85));
		}
		addSlot(new ForgeFuelSlot(this, theForge, forgeSlots * 3, 80, 129));
		for (int y = 0; y < 3; y++) {
			for (int j = 0; j < 9; j++) {
				addSlot(new Slot(playerInv, j + y * 9 + 9, 8 + j * 18, 151 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInv, x, 8 + x * 18, 209));
		}
		addDataSlots(data);
	}

	public int getBurnLeftScaled() {
		int i = forgeData.get(1);
		if (i == 0) {
			i = 200;
		}

		return forgeData.get(0) * 13 / i;
	}

	public int getCookProgressionScaled() {
		int i = forgeData.get(2);
		int j = forgeData.get(3);
		return j != 0 && i != 0 ? i * 24 / j : 0;
	}

	public boolean isBurning() {
		return forgeData.get(0) > 0;
	}

	public boolean isFuel(ItemStack stack) {
		return AbstractFurnaceTileEntity.isFuel(stack);
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			int forgeSize = theForge.getContainerSize();
			if (index >= forgeSlots * 2 && index < forgeSlots * 3) {
				if (!moveItemStackTo(itemstack1, forgeSize, forgeSize + 36, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (index >= forgeSlots * 2 && index != forgeSlots * 3) {
				boolean couldForgeAcceptItem = false;
				if (testSmeltable(itemstack1)) {
					couldForgeAcceptItem = true;
					if (moveItemStackTo(itemstack1, 0, forgeSlots, false)) {
					}
				}

				if (isFuel(itemstack1)) {
					couldForgeAcceptItem = true;
					if (moveItemStackTo(itemstack1, forgeSlots * 3, forgeSlots * 3 + 1, false)) {
					}
				}

				for (int i = 0; i < forgeSlots; ++i) {
					int alloySlot = i + forgeSlots;
					ItemStack ingredientInForge = theForge.getItem(i);
					ItemStack alloyInForge = theForge.getItem(alloySlot);
					if (testAlloySmeltable(ingredientInForge, itemstack1)) {
						couldForgeAcceptItem = true;
						if (moveItemStackTo(itemstack1, alloySlot, alloySlot + 1, false)) {
						}
					} else if (testAlloySmeltable(itemstack1, alloyInForge)) {
						couldForgeAcceptItem = true;
						if (moveItemStackTo(itemstack1, i, i + 1, false)) {
						}
					}
				}

				if (!couldForgeAcceptItem) {
					if (index >= forgeSize && index < forgeSize + 27) {
						if (!moveItemStackTo(itemstack1, forgeSize + 27, forgeSize + 36, false)) {
							return ItemStack.EMPTY;
						}
					} else if (index >= forgeSize + 27 && index < forgeSize + 36 && !moveItemStackTo(itemstack1, forgeSize, forgeSize + 27, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!moveItemStackTo(itemstack1, forgeSize, forgeSize + 36, false)) {
				return ItemStack.EMPTY;
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
	public boolean stillValid(PlayerEntity player) {
		return theForge.stillValid(player);
	}

	private boolean testAlloySmeltable(ItemStack stack, ItemStack alloy) {
		return !theForge.getSmeltingResult(stack, alloy).isEmpty();
	}

	private boolean testSmeltable(ItemStack stack) {
		return testAlloySmeltable(stack, ItemStack.EMPTY);
	}
}
