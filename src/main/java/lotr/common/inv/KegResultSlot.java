package lotr.common.inv;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class KegResultSlot extends Slot {
	public static final ResourceLocation EMPTY_MUG_TEXTURE = new ResourceLocation("lotr", "item/empty_keg_slot_mug");

	public KegResultSlot(IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
		setBackground(PlayerContainer.BLOCK_ATLAS, EMPTY_MUG_TEXTURE);
	}

	@Override
	public boolean mayPickup(PlayerEntity entityplayer) {
		return false;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
}
