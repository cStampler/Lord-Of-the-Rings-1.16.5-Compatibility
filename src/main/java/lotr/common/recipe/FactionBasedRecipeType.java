package lotr.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

public interface FactionBasedRecipeType extends IRecipeType {
	ItemStack getFactionTableIcon();
}
