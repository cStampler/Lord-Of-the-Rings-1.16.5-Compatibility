package lotr.common.recipe;

import lotr.common.init.LOTRBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ElvenForgeRecipe extends LOTRAbstractCookingRecipe {
	public ElvenForgeRecipe(ResourceLocation rl, String grp, Ingredient ingr, ItemStack res, float xp, int time) {
		super(LOTRRecipes.ELVEN_FORGE, rl, grp, ingr, res, xp, time);
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.ELVEN_FORGE_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack((IItemProvider) LOTRBlocks.ELVEN_FORGE.get());
	}
}
