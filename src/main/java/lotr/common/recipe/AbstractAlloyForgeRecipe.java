package lotr.common.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;
import net.minecraft.world.World;

public abstract class AbstractAlloyForgeRecipe implements IRecipe<IInventory> {
	protected final IRecipeType recipeType;
	protected final ResourceLocation recipeId;
	protected final String group;
	protected final Ingredient ingredient;
	protected final Ingredient alloyIngredient;
	protected final boolean swappable;
	protected final ItemStack result;
	protected final float experience;
	protected final int cookTime;

	public AbstractAlloyForgeRecipe(IRecipeType ty, ResourceLocation i, String grp, Ingredient ingr, Ingredient alloy, boolean swap, ItemStack res, float xp, int time) {
		recipeType = ty;
		recipeId = i;
		group = grp;
		ingredient = ingr;
		alloyIngredient = alloy;
		swappable = swap;
		result = res;
		experience = xp;
		cookTime = time;
	}

	@Override
	public ItemStack assemble(IInventory inv) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public int getCookTime() {
		return cookTime;
	}

	public float getExperience() {
		return experience;
	}

	@Override
	public String getGroup() {
		return group;
	}

	@Override
	public ResourceLocation getId() {
		return recipeId;
	}

	@Override
	public NonNullList getIngredients() {
		NonNullList list = NonNullList.create();
		list.add(ingredient);
		list.add(alloyIngredient);
		return list;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public IRecipeType getType() {
		return recipeType;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		ItemStack invIngredient = inv.getItem(0);
		ItemStack invAlloy = inv.getItem(1);
		if (ingredient.test(invIngredient) && alloyIngredient.test(invAlloy)) {
			return true;
		}
		return swappable && ingredient.test(invAlloy) && alloyIngredient.test(invIngredient);
	}
}
