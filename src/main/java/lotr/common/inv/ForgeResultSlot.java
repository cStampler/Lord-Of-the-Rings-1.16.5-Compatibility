package lotr.common.inv;

import lotr.common.tileentity.AbstractAlloyForgeTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.hooks.BasicEventHooks;

public class ForgeResultSlot extends Slot {
	private final PlayerEntity player;
	private int removeCount;

	public ForgeResultSlot(PlayerEntity p, IInventory inv, int i, int x, int y) {
		super(inv, i, x, y);
		player = p;
	}

	@Override
	protected void checkTakeAchievements(ItemStack stack) {
		stack.onCraftedBy(player.level, player, removeCount);
		if (!player.level.isClientSide && container instanceof AbstractAlloyForgeTileEntity) {
			((AbstractAlloyForgeTileEntity) container).onResultTaken(player);
		}

		removeCount = 0;
		BasicEventHooks.firePlayerSmeltedEvent(player, stack);
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	@Override
	protected void onQuickCraft(ItemStack stack, int amount) {
		removeCount += amount;
		checkTakeAchievements(stack);
	}

	@Override
	public ItemStack onTake(PlayerEntity p, ItemStack stack) {
		checkTakeAchievements(stack);
		super.onTake(p, stack);
		return stack;
	}

	@Override
	public ItemStack remove(int amount) {
		if (hasItem()) {
			removeCount += Math.min(amount, getItem().getCount());
		}

		return super.remove(amount);
	}
}
