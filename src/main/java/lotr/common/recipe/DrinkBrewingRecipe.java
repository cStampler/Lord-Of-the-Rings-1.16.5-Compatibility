package lotr.common.recipe;

import java.util.Iterator;

import lotr.common.init.LOTRTags;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DrinkBrewingRecipe implements IRecipe<IInventory> {
	protected final ResourceLocation recipeId;
	protected final String group;
	protected final NonNullList ingredients;
	protected final ItemStack result;
	protected final float experience;
	protected final int brewTime;

	public DrinkBrewingRecipe(ResourceLocation i, String grp, NonNullList ingr, ItemStack res, float xp, int time) {
		recipeId = i;
		group = grp;
		ingredients = ingr;
		result = res;
		experience = xp;
		brewTime = time;
	}

	@Override
	public ItemStack assemble(IInventory inv) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	public int getBrewTime() {
		return brewTime;
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
		return ingredients;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return LOTRRecipes.DRINK_BREWING_SERIALIZER.get();
	}

	@Override
	public IRecipeType getType() {
		return LOTRRecipes.DRINK_BREWING;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		int invSize = inv.getContainerSize();
		int invSizeToCheck = invSize - 3;
		if (invSizeToCheck > ingredients.size()) {
			return false;
		}
		boolean[] matchedSlots = new boolean[invSizeToCheck];
		Iterator var6 = ingredients.iterator();

		boolean matchedIngredient;
		do {
			if (!var6.hasNext()) {
				for (int i = 0; i < 3; ++i) {
					int waterSlot = invSizeToCheck + i;
					if (!isWaterSource(inv.getItem(waterSlot))) {
						return false;
					}
				}

				return true;
			}

			Ingredient ing = (Ingredient) var6.next();
			matchedIngredient = false;

			for (int i = 0; i < invSizeToCheck; ++i) {
				if (!matchedSlots[i]) {
					ItemStack stackInSlot = inv.getItem(i);
					if (ing.test(stackInSlot)) {
						matchedIngredient = true;
						matchedSlots[i] = true;
						break;
					}
				}
			}
		} while (matchedIngredient);

		return false;
	}

	public static boolean isWaterSource(ItemStack stack) {
		return stack.getItem().is(LOTRTags.Items.KEG_BREWING_WATER_SOURCES);
	}
}
