package lotr.common.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lotr.common.LOTRLog;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class FactionTableType implements FactionBasedRecipeType<ICraftingRecipe> {
	public final ResourceLocation recipeTypeName;
	private final Supplier<Block> blockIconSupplier;
	private final List<MultiTableType> associatedMultiTableTypes = new ArrayList<>();
	public final String recipeID;

	public FactionTableType(ResourceLocation name, Supplier<Block> blockSup) {
		recipeTypeName = name;
		recipeID = recipeTypeName.toString();
		blockIconSupplier = blockSup;
	}

	@Override
	public ItemStack getFactionTableIcon() {
		return new ItemStack((IItemProvider) blockIconSupplier.get());
	}

	public ItemStack getIcon() {
		return getFactionTableIcon();
	}

	public List<MultiTableType> getMultiTableTypes() {
		return new ArrayList<MultiTableType>(associatedMultiTableTypes);
	}

	protected void registerMultiTableType(MultiTableType t) {
		if (!t.includesFactionType(this)) {
			throw new IllegalArgumentException("Invalid - multi table type " + t.toString() + " does not include faction table " + toString() + "!");
		}
		if (!associatedMultiTableTypes.contains(t)) {
			associatedMultiTableTypes.add(t);
		} else {
			LOTRLog.warn("Faction table type %s already includes multi table type %s", toString(), t.toString());
		}
	}

	@Override
	public String toString() {
		return recipeTypeName.toString();
	}
}
