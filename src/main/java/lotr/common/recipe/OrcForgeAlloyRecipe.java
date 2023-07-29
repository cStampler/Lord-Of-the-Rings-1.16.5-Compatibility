package lotr.common.recipe;

import lotr.common.init.LOTRBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;

public class OrcForgeAlloyRecipe extends AbstractAlloyForgeRecipe {
	public OrcForgeAlloyRecipe(ResourceLocation i, String grp, Ingredient ingr, Ingredient alloy, boolean swap, ItemStack res, float xp, int time) {
		super(LOTRRecipes.ORC_FORGE_ALLOY, i, grp, ingr, alloy, swap, res, xp, time);
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.ORC_FORGE_ALLOY_SERIALIZER.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return new ItemStack((IItemProvider) LOTRBlocks.ORC_FORGE.get());
	}
}
