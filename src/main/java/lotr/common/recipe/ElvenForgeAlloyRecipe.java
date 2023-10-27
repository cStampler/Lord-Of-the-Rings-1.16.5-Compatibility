package lotr.common.recipe;

import lotr.common.init.LOTRBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ElvenForgeAlloyRecipe extends AbstractAlloyForgeRecipe {
	public ElvenForgeAlloyRecipe(ResourceLocation i, String grp, Ingredient ingr, Ingredient alloy, boolean swap, ItemStack res, float xp, int time) {
		super(LOTRRecipes.ELVEN_FORGE_ALLOY, i, grp, ingr, alloy, swap, res, xp, time);
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.ELVEN_FORGE_ALLOY_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack((IItemProvider) LOTRBlocks.ELVEN_FORGE.get());
	}
}
