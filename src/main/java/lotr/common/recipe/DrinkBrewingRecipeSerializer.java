package lotr.common.recipe;

import java.util.Iterator;

import com.google.gson.*;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class DrinkBrewingRecipeSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<DrinkBrewingRecipe> {
	private final int defaultBrewingTime;

	public DrinkBrewingRecipeSerializer(int time) {
		defaultBrewingTime = time;
	}

	@Override
	public DrinkBrewingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
		String group = JSONUtils.getAsString(json, "group", "");
		NonNullList ingList = readIngredients(JSONUtils.getAsJsonArray(json, "ingredients"));
		if (ingList.isEmpty()) {
			throw new JsonParseException("No ingredients for drink brewing recipe");
		}
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
		int time = JSONUtils.getAsInt(json, "brewtime", defaultBrewingTime);
		return new DrinkBrewingRecipe(recipeId, group, ingList, result, xp, time);
	}

	@Override
	public DrinkBrewingRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
		String group = buffer.readUtf(32767);
		int numIngs = buffer.readVarInt();
		NonNullList ingList = NonNullList.withSize(numIngs, Ingredient.EMPTY);

		for (int i = 0; i < ingList.size(); ++i) {
			ingList.set(i, Ingredient.fromNetwork(buffer));
		}

		ItemStack result = buffer.readItem();
		float xp = buffer.readFloat();
		int time = buffer.readVarInt();
		return new DrinkBrewingRecipe(recipeId, group, ingList, result, xp, time);
	}

	@Override
	public void toNetwork(PacketBuffer buffer, DrinkBrewingRecipe recipe) {
		buffer.writeUtf(recipe.group);
		buffer.writeVarInt(recipe.ingredients.size());
		Iterator var3 = recipe.ingredients.iterator();

		while (var3.hasNext()) {
			Ingredient ing = (Ingredient) var3.next();
			ing.toNetwork(buffer);
		}

		buffer.writeItem(recipe.result);
		buffer.writeFloat(recipe.experience);
		buffer.writeVarInt(recipe.brewTime);
	}

	private static NonNullList readIngredients(JsonArray array) {
		NonNullList list = NonNullList.create();

		for (int i = 0; i < array.size(); ++i) {
			Ingredient ing = Ingredient.fromJson(array.get(i));
			if (!ing.isEmpty()) {
				list.add(ing);
			}
		}

		return list;
	}
}
