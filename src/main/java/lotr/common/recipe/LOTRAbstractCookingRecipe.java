package lotr.common.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class LOTRAbstractCookingRecipe implements IRecipe<IInventory> {
	protected final IRecipeType<?> type;
	protected final ResourceLocation id;
	protected final String group;
	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final float experience;
	protected final int cookingTime;

	public LOTRAbstractCookingRecipe(IRecipeType<?> p_i50032_1_, ResourceLocation p_i50032_2_, String p_i50032_3_, Ingredient p_i50032_4_, ItemStack p_i50032_5_, float p_i50032_6_, int p_i50032_7_) {
		type = p_i50032_1_;
		id = p_i50032_2_;
		group = p_i50032_3_;
		ingredient = p_i50032_4_;
		result = p_i50032_5_;
		experience = p_i50032_6_;
		cookingTime = p_i50032_7_;
	}

	@Override
	public ItemStack assemble(IInventory p_77572_1_) {
		return result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int p_194133_1_, int p_194133_2_) {
		return true;
	}

	public int getCookingTime() {
		return cookingTime;
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
		return id;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(ingredient);
		return nonnulllist;
	}

	@Override
	public ItemStack getResultItem() {
		return result;
	}

	@Override
	public IRecipeType<?> getType() {
		return type;
	}

	@Override
	public boolean matches(IInventory p_77569_1_, World p_77569_2_) {
		return ingredient.test(p_77569_1_.getItem(0));
	}
}