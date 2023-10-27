package lotr.common.recipe;

import com.google.gson.JsonObject;

import lotr.common.item.IEmptyVesselItem;
import lotr.common.item.VesselDrinkItem;
import lotr.common.item.VesselType;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class VesselDrinkShapelessRecipe extends ShapelessRecipe {
	private final boolean leaveDrinkIngredientContainers;

	public VesselDrinkShapelessRecipe(ResourceLocation rId, String grp, ItemStack output, NonNullList ings, boolean leaveDrinkIngredientContainers) {
		super(rId, grp, output, ings);
		this.leaveDrinkIngredientContainers = leaveDrinkIngredientContainers;
	}

	@Override
	public ItemStack assemble(CraftingInventory inv) {
		ItemStack result = super.assemble(inv);
		if (result.getItem() instanceof VesselDrinkItem) {
			VesselType ves = determineSingleVessel(inv);
			if (ves == null) {
				ves = VesselType.WOODEN_MUG;
			}

			VesselDrinkItem.setVessel(result, ves);
		}

		return result;
	}

	private VesselType determineSingleVessel(CraftingInventory inv) {
		VesselType singleType = null;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack invStack = inv.getItem(i);
			if (!invStack.isEmpty()) {
				Item invItem = invStack.getItem();
				VesselType invVessel = null;
				if (invItem instanceof VesselDrinkItem) {
					invVessel = VesselDrinkItem.getVessel(invStack);
				} else if (invItem instanceof IEmptyVesselItem) {
					invVessel = ((IEmptyVesselItem) invItem).getVesselType();
				}

				if (invVessel != null) {
					if (singleType == null) {
						singleType = invVessel;
					} else if (invVessel != singleType) {
						singleType = null;
						break;
					}
				}
			}
		}

		return singleType;
	}

	@Override
	public NonNullList getRemainingItems(CraftingInventory inv) {
		NonNullList list = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		for (int i = 0; i < list.size(); ++i) {
			ItemStack item = inv.getItem(i);
			if (item.hasContainerItem() && (leaveDrinkIngredientContainers || !(item.getItem() instanceof VesselDrinkItem))) {
				list.set(i, item.getContainerItem());
			}
		}

		return list;
	}

	@Override
	public IRecipeSerializer getSerializer() {
		return LOTRRecipes.VESSEL_DRINK_SHAPELESS.get();
	}

	@Override
	public boolean matches(CraftingInventory inv, World world) {
		return super.matches(inv, world) && determineSingleVessel(inv) != null;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<VesselDrinkShapelessRecipe> {
		private final net.minecraft.item.crafting.ShapelessRecipe.Serializer internalSerializer = new net.minecraft.item.crafting.ShapelessRecipe.Serializer();

		@Override
		public VesselDrinkShapelessRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			ShapelessRecipe recipe = internalSerializer.fromJson(recipeId, json);
			boolean leaveDrinkIngredientContainers = json.has("leave_drink_ingredient_containers") ? json.get("leave_drink_ingredient_containers").getAsBoolean() : true;
			return new VesselDrinkShapelessRecipe(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients(), leaveDrinkIngredientContainers);
		}

		@Override
		public VesselDrinkShapelessRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			ShapelessRecipe recipe = internalSerializer.fromNetwork(recipeId, buffer);
			boolean leaveDrinkIngredientContainers = buffer.readBoolean();
			return new VesselDrinkShapelessRecipe(recipe.getId(), recipe.getGroup(), recipe.getResultItem(), recipe.getIngredients(), leaveDrinkIngredientContainers);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, VesselDrinkShapelessRecipe recipe) {
			internalSerializer.toNetwork(buffer, recipe);
			buffer.writeBoolean(recipe.leaveDrinkIngredientContainers);
		}
	}
}
