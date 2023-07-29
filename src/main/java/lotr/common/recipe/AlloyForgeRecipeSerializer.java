package lotr.common.recipe;

import com.google.gson.*;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class AlloyForgeRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AbstractAlloyForgeRecipe> {
	private final AlloyForgeRecipeSerializer.IFactory recipeFactory;
	private final int defaultCookingTime;

	public AlloyForgeRecipeSerializer(AlloyForgeRecipeSerializer.IFactory factory, int time) {
		recipeFactory = factory;
		defaultCookingTime = time;
	}

	@Override
	public AbstractAlloyForgeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		String group = JSONUtils.getAsString(json, "group", "");
		JsonElement jsonIngredient = JSONUtils.isArrayNode(json, "ingredient") ? JSONUtils.getAsJsonArray(json, "ingredient") : JSONUtils.getAsJsonObject(json, "ingredient");
		JsonElement jsonAlloy = JSONUtils.isArrayNode(json, "alloy") ? JSONUtils.getAsJsonArray(json, "alloy") : JSONUtils.getAsJsonObject(json, "alloy");
		Ingredient ingredient = Ingredient.fromJson(jsonIngredient);
		Ingredient alloy = Ingredient.fromJson(jsonAlloy);
		boolean swappable = json.get("swappable").getAsBoolean();
		if (!json.has("result")) {
			throw new JsonSyntaxException("Missing result, expected to find a string or object");
		}
		ItemStack result;
		if (json.get("result").isJsonObject()) {
			result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
		} else {
			String resultString = JSONUtils.getAsString(json, "result");
			ResourceLocation resultId = new ResourceLocation(resultString);
			result = new ItemStack(Registry.ITEM.getOptional(resultId).orElseThrow(() -> new IllegalStateException("Item: " + resultString + " does not exist")));
		}

		float xp = JSONUtils.getAsFloat(json, "experience", 0.0F);
		int time = JSONUtils.getAsInt(json, "cookingtime", defaultCookingTime);
		return recipeFactory.create(recipeId, group, ingredient, alloy, swappable, result, xp, time);
	}

	@Override
	public AbstractAlloyForgeRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		String group = buffer.readUtf(32767);
		Ingredient ingredient = Ingredient.fromNetwork(buffer);
		Ingredient alloy = Ingredient.fromNetwork(buffer);
		boolean swappable = buffer.readBoolean();
		ItemStack result = buffer.readItem();
		float xp = buffer.readFloat();
		int time = buffer.readVarInt();
		return recipeFactory.create(recipeId, group, ingredient, alloy, swappable, result, xp, time);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, AbstractAlloyForgeRecipe recipe) {
		buffer.writeUtf(recipe.group);
		recipe.ingredient.toNetwork(buffer);
		recipe.alloyIngredient.toNetwork(buffer);
		buffer.writeBoolean(recipe.swappable);
		buffer.writeItem(recipe.result);
		buffer.writeFloat(recipe.experience);
		buffer.writeVarInt(recipe.cookTime);
	}

	public interface IFactory {
		AbstractAlloyForgeRecipe create(ResourceLocation var1, String var2, Ingredient var3, Ingredient var4, boolean var5, ItemStack var6, float var7, int var8);
	}
}
