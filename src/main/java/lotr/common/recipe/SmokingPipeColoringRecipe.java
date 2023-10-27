package lotr.common.recipe;

import lotr.common.init.LOTRTags;
import lotr.common.item.SmokingPipeItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SmokingPipeColoringRecipe extends SpecialRecipe {
	public SmokingPipeColoringRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		ItemStack pipe = ItemStack.EMPTY;
		DyeColor pipeColor = null;
		boolean pipeMagic = false;
		DyeColor newColor = null;
		boolean newMagic = false;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack ingredient = inv.getItem(i);
			if (!ingredient.isEmpty()) {
				Item ingredientItem = ingredient.getItem();
				if (ingredientItem instanceof SmokingPipeItem && pipe.isEmpty()) {
					pipe = ingredient;
					pipeColor = SmokingPipeItem.getSmokeColor(ingredient);
					pipeMagic = SmokingPipeItem.isMagicSmoke(ingredient);
				} else if (ingredientItem instanceof DyeItem && newColor == null) {
					newColor = ((DyeItem) ingredientItem).getDyeColor();
				} else {
					if (!ingredientItem.is(LOTRTags.Items.PIPE_MAGIC_SMOKE_INGREDIENTS) || newMagic) {
						return ItemStack.EMPTY;
					}

					newMagic = true;
				}
			}
		}

		if (pipe.isEmpty() || (newColor == null || newColor == pipeColor) && (!newMagic || pipeMagic)) {
			return ItemStack.EMPTY;
		}
		ItemStack result = pipe.copy();
		if (newColor != null) {
			SmokingPipeItem.setSmokeColor(result, newColor);
		}

		if (newMagic) {
			SmokingPipeItem.setMagicSmoke(result, newMagic);
		}

		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.CRAFTING_SPECIAL_SMOKING_PIPE_COLORING.get();
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		return !assemble(inv).isEmpty();
	}
}
