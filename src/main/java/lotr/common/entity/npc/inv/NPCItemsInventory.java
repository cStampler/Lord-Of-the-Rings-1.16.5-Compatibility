package lotr.common.entity.npc.inv;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketNPCState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;

public class NPCItemsInventory extends NPCInventory {
	private boolean isEating = false;

	public NPCItemsInventory(NPCEntity entity) {
		super(entity, 12, "NPCItemsInv");
	}

	public void backupHeldAndStartEating(ItemStack eatingItem) {
		setEatingBackup(((NPCEntity) theEntity).getItemInHand(Hand.MAIN_HAND));
		setIsEating(true);
		((NPCEntity) theEntity).setItemInHand(Hand.MAIN_HAND, eatingItem);
	}

	public void clearIdleItem() {
		setIdleItem(ItemStack.EMPTY);
	}

	public void clearMeleeWeapon() {
		setMeleeWeapon(ItemStack.EMPTY);
	}

	public void clearRangedWeapon() {
		setRangedWeapon(ItemStack.EMPTY);
	}

	private SPacketNPCState createIsEatingPacket() {
		return new SPacketNPCState((NPCEntity) theEntity, SPacketNPCState.Type.IS_EATING, getIsEating());
	}

	public ItemStack getBomb() {
		return getItem(11).copy();
	}

	public ItemStack getBombingItem() {
		return getItem(10).copy();
	}

	private ItemStack getEatingBackup() {
		return getItem(4).copy();
	}

	public ItemStack getIdleItem() {
		return getItem(0).copy();
	}

	public ItemStack getIdleItemMounted() {
		return getItem(5).copy();
	}

	public boolean getIsEating() {
		return isEating;
	}

	public ItemStack getMeleeWeapon() {
		return getItem(1).copy();
	}

	public ItemStack getMeleeWeaponMounted() {
		return getItem(6).copy();
	}

	public ItemStack getRangedWeapon() {
		return getItem(2).copy();
	}

	public ItemStack getReplacedIdleItem() {
		return getItem(7).copy();
	}

	public ItemStack getReplacedIdleItemMounted() {
		return getItem(9).copy();
	}

	public ItemStack getReplacedMeleeWeaponMounted() {
		return getItem(8).copy();
	}

	public ItemStack getSpearBackup() {
		return getItem(3).copy();
	}

	@Override
	public void readFromEntityNBT(CompoundNBT nbt) {
		super.readFromEntityNBT(nbt);
		isEating = nbt.getBoolean("NPCEating");
		if (isEating) {
			stopEatingAndRestoreHeld();
		}

	}

	public void receiveClientIsEating(boolean state) {
		if (!((NPCEntity) theEntity).level.isClientSide) {
			throw new IllegalStateException("This method should only be called on the clientside");
		}
		isEating = state;
	}

	public void sendIsEating(ServerPlayerEntity player) {
		LOTRPacketHandler.sendTo(createIsEatingPacket(), player);
	}

	public void sendIsEatingToWatchers() {
		LOTRPacketHandler.sendToAllTrackingEntity(createIsEatingPacket(), theEntity);
	}

	public void setBomb(ItemStack item) {
		setItem(11, item);
	}

	public void setBombingItem(ItemStack item) {
		setItem(10, item);
	}

	private void setEatingBackup(ItemStack item) {
		setItem(4, item);
	}

	public void setIdleItem(ItemStack item) {
		setItem(0, item);
	}

	public void setIdleItemMounted(ItemStack item) {
		setItem(5, item);
	}

	public void setIdleItemsFromMeleeWeapons() {
		setIdleItem(getMeleeWeapon());
		setIdleItemMounted(getMeleeWeaponMounted());
	}

	public void setIdleItemsFromRangedWeapons() {
		setIdleItem(getRangedWeapon());
		setIdleItemMounted(getRangedWeapon());
	}

	private void setIsEating(boolean flag) {
		if (isEating != flag) {
			isEating = flag;
			sendIsEatingToWatchers();
		}

	}

	public void setMeleeWeapon(ItemStack item) {
		setItem(1, item);
	}

	public void setMeleeWeaponMounted(ItemStack item) {
		setItem(6, item);
	}

	public void setRangedWeapon(ItemStack item) {
		setItem(2, item);
	}

	public void setReplacedIdleItem(ItemStack item) {
		setItem(7, item);
	}

	public void setReplacedIdleItemMounted(ItemStack item) {
		setItem(9, item);
	}

	public void setReplacedMeleeWeaponMounted(ItemStack item) {
		setItem(8, item);
	}

	public void setSpearBackup(ItemStack item) {
		setItem(3, item);
	}

	public void stopEatingAndRestoreHeld() {
		((NPCEntity) theEntity).setItemInHand(Hand.MAIN_HAND, getEatingBackup());
		setEatingBackup(ItemStack.EMPTY);
		setIsEating(false);
	}

	@Override
	public void writeToEntityNBT(CompoundNBT nbt) {
		super.writeToEntityNBT(nbt);
		nbt.putBoolean("NPCEating", isEating);
	}
}
