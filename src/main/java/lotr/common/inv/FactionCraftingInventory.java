package lotr.common.inv;

import java.util.Optional;

import net.minecraft.inventory.CraftingInventory;

public class FactionCraftingInventory extends CraftingInventory {
	private final FactionCraftingContainer factionCraftingContainer;

	public FactionCraftingInventory(FactionCraftingContainer container, int width, int height) {
		super(container, width, height);
		factionCraftingContainer = container;
	}

	public Optional getPouchColoringFaction() {
		return !factionCraftingContainer.isStandardCraftingActive() ? Optional.of(factionCraftingContainer.getFaction()) : Optional.empty();
	}
}
