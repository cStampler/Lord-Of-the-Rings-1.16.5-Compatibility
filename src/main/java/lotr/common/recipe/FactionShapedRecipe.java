package lotr.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FactionShapedRecipe extends ShapedRecipe {
	private final FactionBasedRecipeType tableType;

	public FactionShapedRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList recipeItemsIn, ItemStack recipeOutputIn, FactionBasedRecipeType tableType) {
		super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
		this.tableType = tableType;
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.FACTION_SHAPED.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return tableType.getFactionTableIcon();
	}

	@Override
	public IRecipeType getType() {
		return tableType;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FactionShapedRecipe> {
		private final net.minecraft.item.crafting.ShapedRecipe.Serializer internalSerializer = new net.minecraft.item.crafting.ShapedRecipe.Serializer();

		@Override
		public FactionShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ShapedRecipe recipe = internalSerializer.fromJson(recipeId, json);
			String tableTypeName = JSONUtils.getAsString(json, "table", "");
			FactionBasedRecipeType tableType = (FactionBasedRecipeType) LOTRRecipes.findRecipeTypeByNameOrThrow(tableTypeName, FactionBasedRecipeType.class);
			return new FactionShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(), tableType);
		}

		@Override
		public FactionShapedRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			ShapedRecipe recipe = internalSerializer.fromNetwork(recipeId, buffer);
			String tableTypeName = buffer.readUtf(32767);
			FactionBasedRecipeType tableType = (FactionBasedRecipeType) LOTRRecipes.findRecipeTypeByNameOrThrow(tableTypeName, FactionBasedRecipeType.class);
			return new FactionShapedRecipe(recipe.getId(), recipe.getGroup(), recipe.getWidth(), recipe.getHeight(), recipe.getIngredients(), recipe.getResultItem(), tableType);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, FactionShapedRecipe recipe) {
			internalSerializer.toNetwork(buffer, recipe);
			String tableTypeName = LOTRRecipes.findRecipeTypeName(recipe.tableType);
			buffer.writeUtf(tableTypeName, 32767);
		}
	}
}
