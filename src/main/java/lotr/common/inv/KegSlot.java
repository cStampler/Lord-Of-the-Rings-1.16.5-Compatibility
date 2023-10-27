package lotr.common.inv;

import lotr.common.recipe.DrinkBrewingRecipe;
import lotr.common.tileentity.KegTileEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class KegSlot extends Slot {
	public static final ResourceLocation EMPTY_BUCKET_TEXTURE = new ResourceLocation("lotr", "item/empty_keg_slot_bucket");
	private KegContainer theKegContainer;
	private boolean isWater;

	public KegSlot(KegContainer keg, IInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		theKegContainer = keg;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		if (theKegContainer.getKegMode() == KegTileEntity.KegMode.EMPTY) {
			return isWater ? DrinkBrewingRecipe.isWaterSource(stack) : true;
		}
		return false;
	}

	public KegSlot setWaterSource() {
		isWater = true;
		setBackground(PlayerContainer.BLOCK_ATLAS, EMPTY_BUCKET_TEXTURE);
		return this;
	}
}
