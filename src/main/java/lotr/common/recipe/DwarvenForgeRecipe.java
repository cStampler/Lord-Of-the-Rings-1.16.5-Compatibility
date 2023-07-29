package lotr.common.recipe;

import lotr.common.init.LOTRBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;

public class DwarvenForgeRecipe extends LOTRAbstractCookingRecipe {
	public DwarvenForgeRecipe(ResourceLocation rl, String grp, Ingredient ingr, ItemStack res, float xp, int time) {
		super(LOTRRecipes.DWARVEN_FORGE, rl, grp, ingr, res, xp, time);
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.DWARVEN_FORGE_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack((IItemProvider) LOTRBlocks.DWARVEN_FORGE.get());
	}
}
