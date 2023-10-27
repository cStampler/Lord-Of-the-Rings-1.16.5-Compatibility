package lotr.common.inv;

import java.util.Optional;

import lotr.common.LOTRLog;
import lotr.common.util.UsernameHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class FactionCraftingResultSlot extends CraftingResultSlot {
	private final PlayerEntity slotPlayer;
	private final CraftingInventory craftMatrix;
	private final CraftResultInventory resultInventory;
	private final FactionCraftingContainer tableContainer;

	public FactionCraftingResultSlot(PlayerEntity player, FactionCraftingContainer table, CraftingInventory tableInv, CraftResultInventory resultInv, int slot, int x, int y) {
		super(player, tableInv, resultInv, slot, x, y);
		slotPlayer = player;
		craftMatrix = tableInv;
		resultInventory = resultInv;
		tableContainer = table;
	}

	private IRecipeType determineRecipeType(PlayerEntity player) {
		Optional optRecipe = tableContainer.findMatchingRecipeOfAppropriateType(slotPlayer.level, slotPlayer, craftMatrix);
		if (optRecipe.isPresent()) {
			ICraftingRecipe recipe = (ICraftingRecipe) optRecipe.get();
			World world = player.level;
			boolean canUseRecipe = world.isClientSide ? true : resultInventory.setRecipeUsed(world, (ServerPlayerEntity) player, recipe);
			if (canUseRecipe) {
				return ((ICraftingRecipe) optRecipe.get()).getType();
			}
		}

		LOTRLog.error("Faction crafting table (%s) failed to determine the type of the crafted recipe (crafter = %s)", tableContainer.getType().getRegistryName(), UsernameHelper.getRawUsername(slotPlayer));
		return IRecipeType.CRAFTING;
	}

	@Override
	public ItemStack onTake(PlayerEntity player, ItemStack stack) {
		checkTakeAchievements(stack);
		ForgeHooks.setCraftingPlayer(player);
		IRecipeType recipeType = determineRecipeType(player);
		NonNullList nonnulllist = player.level.getRecipeManager().getRemainingItemsFor(recipeType, craftMatrix, player.level);
		ForgeHooks.setCraftingPlayer((PlayerEntity) null);

		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack inMatrix = craftMatrix.getItem(i);
			ItemStack inList = (ItemStack) nonnulllist.get(i);
			if (!inMatrix.isEmpty()) {
				craftMatrix.removeItem(i, 1);
				inMatrix = craftMatrix.getItem(i);
			}

			if (!inList.isEmpty()) {
				if (inMatrix.isEmpty()) {
					craftMatrix.setItem(i, inList);
				} else if (ItemStack.isSame(inMatrix, inList) && ItemStack.tagMatches(inMatrix, inList)) {
					inList.grow(inMatrix.getCount());
					craftMatrix.setItem(i, inList);
				} else if (!slotPlayer.inventory.add(inList)) {
					slotPlayer.drop(inList, false);
				}
			}
		}

		return stack;
	}
}
