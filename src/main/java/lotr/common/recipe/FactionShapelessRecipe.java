package lotr.common.recipe;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FactionShapelessRecipe extends ShapelessRecipe {
	private final FactionBasedRecipeType tableType;

	public FactionShapelessRecipe(ResourceLocation idIn, String groupIn, ItemStack recipeOutputIn, NonNullList recipeItemsIn, FactionBasedRecipeType tableType) {
		super(idIn, groupIn, recipeOutputIn, recipeItemsIn);
		this.tableType = tableType;
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.FACTION_SHAPELESS.get();
	}

	@Override
	public ItemStack getToastSymbol() {
		return tableType.getFactionTableIcon();
	}

	@Override
	public IRecipeType getType() {
		return tableType;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FactionShapelessRecipe> {
		private final net.minecraft.item.crafting.ShapelessRecipe.Serializer internalSerializer = new net.minecraft.item.crafting.ShapelessRecipe.Serializer();

		@Override
		public FactionShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ShapelessRecipe recipe = internalSerializer.fromJson(recipeId, json);
			String tableTypeName = JSONUtils.getAsString(json, "table", "");
			FactionBasedRecipeType tableType = (FactionBasedRecipeType) LOTRRecipes.findRecipeTypeByNameOrThrow(tableTypeName, FactionBasedRecipeType.class);
			return new FactionShapelessRecipe(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients(), tableType);
		}

		@Override
		public FactionShapelessRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			ShapelessRecipe recipe = internalSerializer.fromNetwork(recipeId, buffer);
			String tableTypeName = buffer.readUtf(32767);
			FactionBasedRecipeType tableType = (FactionBasedRecipeType) LOTRRecipes.findRecipeTypeByNameOrThrow(tableTypeName, FactionBasedRecipeType.class);
			return new FactionShapelessRecipe(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients(), tableType);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, FactionShapelessRecipe recipe) {
			internalSerializer.toNetwork(buffer, recipe);
			String tableTypeName = LOTRRecipes.findRecipeTypeName(recipe.tableType);
			buffer.writeUtf(tableTypeName, 32767);
		}
	}
}
