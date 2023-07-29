package lotr.common.tileentity;

import lotr.common.init.*;
import lotr.common.inv.AlloyForgeContainer;
import lotr.common.recipe.LOTRRecipes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.*;

public class HobbitOvenTileEntity extends AbstractAlloyForgeTileEntity {
	public HobbitOvenTileEntity() {
		super((TileEntityType) LOTRTileEntities.HOBBIT_OVEN.get(), new IRecipeType[] { LOTRRecipes.HOBBIT_OVEN, IRecipeType.SMELTING }, new IRecipeType[] { LOTRRecipes.HOBBIT_OVEN_ALLOY });
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new AlloyForgeContainer(id, player, this, forgeData);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container.lotr.hobbit_oven");
	}

	@Override
	protected boolean isDefaultFurnaceRecipeAcceptable(ItemStack ingredientStack, ItemStack resultStack) {
		Item resultItem = resultStack.getItem();
		if (resultItem.isEdible()) {
			return true;
		}
		Item ingredientItem = ingredientStack.getItem();
		return ingredientItem.is(LOTRTags.Items.HOBBIT_OVEN_EXTRA_COOKABLES);
	}
}
