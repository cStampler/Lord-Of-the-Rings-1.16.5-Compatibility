package lotr.common.tileentity;

import lotr.common.init.LOTRTileEntities;
import lotr.common.inv.AlloyForgeContainer;
import lotr.common.recipe.LOTRRecipes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class OrcForgeTileEntity extends AbstractAlloyForgeTileEntity {
	public OrcForgeTileEntity() {
		super((TileEntityType) LOTRTileEntities.ORC_FORGE.get(), new IRecipeType[] { LOTRRecipes.ORC_FORGE, IRecipeType.SMELTING }, new IRecipeType[] { LOTRRecipes.ORC_FORGE_ALLOY, LOTRRecipes.ALLOY_FORGE });
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new AlloyForgeContainer(id, player, this, forgeData);
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container.lotr.orc_forge");
	}
}
