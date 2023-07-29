package lotr.common.recipe;

import java.util.*;

import com.google.gson.JsonObject;

import lotr.common.fac.Faction;
import lotr.common.inv.*;
import lotr.common.item.PouchItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class PouchRecipe extends SpecialRecipe {
	private final IRecipeType recipeType;

	public PouchRecipe(ResourceLocation id, IRecipeType recipeType) {
		super(id);
		this.recipeType = recipeType;
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		Optional tableColoringFaction = inv instanceof FactionCraftingInventory ? ((FactionCraftingInventory) inv).getPouchColoringFaction() : Optional.empty();
		List pouches = new ArrayList();
		int[] rgb = new int[3];
		int brightestIngredientColor = 0;
		int coloredItems = 0;
		boolean anyDye = false;
		boolean shouldApplyColorToResult = false;
		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack ingredient = inv.getItem(i);
			if (!ingredient.isEmpty()) {
				Item ingredientItem = ingredient.getItem();
				if (ingredientItem instanceof PouchItem) {
					pouches.add(ingredient);
					int pouchColor = PouchItem.getPouchColor(ingredient);
					float r = (pouchColor >> 16 & 0xFF) / 255.0f;
					float g = (pouchColor >> 8 & 0xFF) / 255.0f;
					float b = (pouchColor & 0xFF) / 255.0f;
					brightestIngredientColor = (int) (brightestIngredientColor + Math.max(r, Math.max(g, b)) * 255.0f);
					rgb[0] = (int) (rgb[0] + r * 255.0f);
					rgb[1] = (int) (rgb[1] + g * 255.0f);
					rgb[2] = (int) (rgb[2] + b * 255.0f);
					++coloredItems;
					if (PouchItem.isPouchDyed(ingredient)) {
						shouldApplyColorToResult = true;
					}
				} else {
					if (!(ingredientItem instanceof DyeItem)) {
						return ItemStack.EMPTY;
					}

					float[] dyeColors = ((DyeItem) ingredientItem).getDyeColor().getTextureDiffuseColors();
					int r = (int) (dyeColors[0] * 255.0f);
					int g = (int) (dyeColors[1] * 255.0f);
					int b = (int) (dyeColors[2] * 255.0f);
					brightestIngredientColor += Math.max(r, Math.max(g, b));
					rgb[0] += r;
					rgb[1] += g;
					rgb[2] += b;
					++coloredItems;
					shouldApplyColorToResult = true;
					anyDye = true;
				}
			}
		}

		if (pouches.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack pouch;
		if (pouches.size() == 1) {
			if (!anyDye && !tableColoringFaction.isPresent()) {
				return ItemStack.EMPTY;
			}

			pouch = ((ItemStack) pouches.get(0)).copy();
		} else {
			Item combinedPouchItem = getCombinedPouchItem(pouches);
			if (combinedPouchItem == null) {
				return ItemStack.EMPTY;
			}

			pouch = new ItemStack(combinedPouchItem, 1);
			List combinedContents = new ArrayList();
			Iterator var23 = pouches.iterator();

			while (var23.hasNext()) {
				ItemStack craftingPouch = (ItemStack) var23.next();
				PouchInventory craftingPouchInv = PouchInventory.temporaryReadOnly(craftingPouch);

				for (int color = 0; color < craftingPouchInv.getContainerSize(); ++color) {
					ItemStack slotItem = craftingPouchInv.getItem(color);
					if (!slotItem.isEmpty()) {
						combinedContents.add(slotItem.copy());
					}
				}
			}

			PouchInventory combinedPouchInv = PouchInventory.temporaryWritable(pouch);
			combinedPouchInv.fillPouchFromList(combinedContents);
			boolean pickedUpNewItems = pouches.stream().anyMatch(hummel -> PouchItem.getPickedUpNewItems((ItemStack) hummel));
			PouchItem.setPickedUpNewItems(pouch, pickedUpNewItems);
		}

		if (tableColoringFaction.isPresent() && !anyDye) {
			PouchItem.setPouchDyedByFaction(pouch, (Faction) tableColoringFaction.get());
		} else if (shouldApplyColorToResult && coloredItems > 0) {
			int r = rgb[0] / coloredItems;
			int g = rgb[1] / coloredItems;
			int b = rgb[2] / coloredItems;
			float brightestIngredientColorPerItem = (float) brightestIngredientColor / (float) coloredItems;
			float brightestAvgIngredientRgb = Math.max(r, Math.max(g, b));
			r = (int) (r * brightestIngredientColorPerItem / brightestAvgIngredientRgb);
			g = (int) (g * brightestIngredientColorPerItem / brightestAvgIngredientRgb);
			b = (int) (b * brightestIngredientColorPerItem / brightestAvgIngredientRgb);
			int color = (r << 16) + (g << 8) + b;
			PouchItem.setPouchDyedByColor(pouch, color);
		}

		return pouch;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	private PouchItem getCombinedPouchItem(List pouches) {
		int totalCapacity = 0;

		Item item;
		for (Iterator var3 = pouches.iterator(); var3.hasNext(); totalCapacity += ((PouchItem) item).getCapacity()) {
			ItemStack pouch = (ItemStack) var3.next();
			item = pouch.getItem();
			if (!(item instanceof PouchItem)) {
				return null;
			}
		}

		return (PouchItem) PouchItem.POUCHES_BY_CAPACITY.getOrDefault(totalCapacity, (Object) null);
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.CRAFTING_SPECIAL_POUCH.get();
	}

	@Override
	public IRecipeType getType() {
		return recipeType;
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		return !assemble(inv).isEmpty();
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PouchRecipe> {
		@Override
		public PouchRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			String recipeTypeName = JSONUtils.getAsString(json, "table_type", "");
			IRecipeType recipeType = LOTRRecipes.findRecipeTypeByNameOrThrow(recipeTypeName, IRecipeType.class);
			return new PouchRecipe(recipeId, recipeType);
		}

		@Override
		public PouchRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			String recipeTypeName = buffer.readUtf(32767);
			IRecipeType recipeType = LOTRRecipes.findRecipeTypeByNameOrThrow(recipeTypeName, IRecipeType.class);
			return new PouchRecipe(recipeId, recipeType);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, PouchRecipe recipe) {
			String recipeTypeName = LOTRRecipes.findRecipeTypeName(recipe.recipeType);
			buffer.writeUtf(recipeTypeName, 32767);
		}
	}
}
