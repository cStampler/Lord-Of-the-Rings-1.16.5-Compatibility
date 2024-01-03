package lotr.common.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;

public interface FactionBasedRecipeType<T extends net.minecraft.item.crafting.IRecipe<?>> extends IRecipeType<T> {
	ItemStack getFactionTableIcon();
}
