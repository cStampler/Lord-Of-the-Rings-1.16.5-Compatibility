package lotr.common.inv;

import org.apache.commons.lang3.StringUtils;

import lotr.common.init.LOTRContainers;
import lotr.common.item.PouchItem;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class PouchContainer extends Container implements OpenPouchContainer {
	private final PlayerInventory playerInv;
	private final int playerInvSlot;
	private final ItemStack pouchItem;
	private final PouchInventory pouchInventory;

	public PouchContainer(int windowID, PlayerInventory playerInv, PacketBuffer extraData) {
		super((ContainerType) LOTRContainers.POUCH.get(), windowID);
		this.playerInv = playerInv;
		World world = playerInv.player.level;
		playerInvSlot = extraData.readVarInt();
		pouchItem = playerInv.getItem(playerInvSlot);
		pouchInventory = PouchInventory.worldSidedInventory(pouchItem, world);
		if (!world.isClientSide) {
			PouchItem.setPickedUpNewItems(pouchItem, false);
		}
		int rows = getPouchCapacity() / 9;
		int y;
		for (y = 0; y < rows; y++) {
			for (int i = 0; i < 9; i++) {
				addSlot(new PouchSlot(pouchInventory, i + y * 9, 8 + i * 18, 30 + y * 18));
			}
		}
		for (y = 0; y < 3; y++) {
			for (int i = 0; i < 9; i++) {
				addSlot(new Slot(playerInv, i + y * 9 + 9, 8 + i * 18, 98 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(playerInv, x, 8 + x * 18, 156));
		}
	}

	@Override
	public ItemStack clicked(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
		if (isCurrentPouchSlot(this, slotId, player, playerInvSlot)) {
			return ItemStack.EMPTY;
		}
		return clickType == ClickType.SWAP && dragType == playerInvSlot ? ItemStack.EMPTY : super.clicked(slotId, dragType, clickType, player);
	}

	@Override
	public int getPouchCapacity() {
		return pouchInventory.getContainerSize();
	}

	public ITextComponent getPouchDefaultDisplayName() {
		return pouchItem.getItem().getName(pouchItem);
	}

	public ITextComponent getPouchDisplayName() {
		return pouchItem.getHoverName();
	}

	@Override
	public PouchInventory getPouchInventory() {
		return pouchInventory;
	}

	@Override
	public boolean isOpenPouch(ItemStack stack) {
		return playerInv.getItem(playerInvSlot) == stack;
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		int capacity = getPouchCapacity();
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < capacity) {
				if (!moveItemStackTo(itemstack1, capacity, capacity + 36, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (!moveItemStackTo(itemstack1, 0, capacity, false)) {
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
	public void reloadPouchFromPickup() {
		pouchInventory.reloadFromItemNBT();
		PouchItem.setPickedUpNewItems(pouchItem, false);
		broadcastChanges();
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		player.level.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), ((PouchItem) pouchItem.getItem()).getCloseSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	public void renamePouch(String name) {
		if (StringUtils.isBlank(name)) {
			pouchItem.resetHoverName();
		} else {
			pouchItem.setHoverName(new StringTextComponent(name));
		}

	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return player.inventory.getItem(playerInvSlot) == pouchItem;
	}

	public static boolean isCurrentPouchSlot(Container container, int slotId, PlayerEntity player, int playerInvSlot) {
		if (slotId >= 0 && slotId < container.slots.size()) {
			Slot slot = container.getSlot(slotId);
			if (slot.container == player.inventory && slot.getSlotIndex() == playerInvSlot) {
				return true;
			}
		}

		return false;
	}

	public static void writeContainerInitData(PacketBuffer extraData, int playerInvSlot) {
		extraData.writeVarInt(playerInvSlot);
	}
}
