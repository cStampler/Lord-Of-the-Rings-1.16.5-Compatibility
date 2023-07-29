package lotr.common.inv;

import java.util.*;

import lotr.common.fac.*;
import lotr.common.init.LOTRContainers;
import lotr.common.recipe.*;
import net.minecraft.block.Block;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class FactionCraftingContainer extends Container {
	private final FactionCraftingInventory craftMatrix = new FactionCraftingInventory(this, 3, 3);
	private final CraftResultInventory craftResult = new CraftResultInventory();
	private final PlayerEntity craftingPlayer;
	private IWorldPosCallable worldPos;
	private final FactionCraftingContainer.FactionCraftingContainerInitData initData;
	private boolean standardCraftingTableRecipes;

	public FactionCraftingContainer(int windowID, PlayerInventory inv, PacketBuffer extraData) {
		super((ContainerType) LOTRContainers.FACTION_CRAFTING.get(), windowID);
		worldPos = IWorldPosCallable.NULL;
		craftingPlayer = inv.player;
		if (extraData == null) {
			throw new IllegalArgumentException("FactionCraftingContainer REQUIRES extra packet buffer data to initialise!");
		}
		initData = FactionCraftingContainerInitData.read(extraData, craftingPlayer.level);
		addSlot(new FactionCraftingResultSlot(inv.player, this, craftMatrix, craftResult, 0, 124, 35));
		int y;
		for (y = 0; y < 3; y++) {
			for (int i = 0; i < 3; i++) {
				addSlot(new Slot(craftMatrix, i + y * 3, 30 + i * 18, 17 + y * 18));
			}
		}
		for (y = 0; y < 3; y++) {
			for (int i = 0; i < 9; i++) {
				addSlot(new Slot(inv, i + y * 9 + 9, 8 + i * 18, 84 + y * 18));
			}
		}
		for (int x = 0; x < 9; x++) {
			addSlot(new Slot(inv, x, 8 + x * 18, 142));
		}
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slotIn) {
		return slotIn.container != craftResult && super.canTakeItemForPickAll(stack, slotIn);
	}

	private Optional findMatchingFactionOrMulti(World world, CraftingInventory inv) {
		FactionTableType tableType = getRecipeType();
		Optional recipe = findMatchingRecipeOfType(world, inv, tableType);
		if (recipe.isPresent()) {
			return recipe;
		}
		Iterator var6 = tableType.getMultiTableTypes().iterator();

		Optional multiRecipe;
		do {
			if (!var6.hasNext()) {
				return Optional.empty();
			}

			MultiTableType multiType = (MultiTableType) var6.next();
			multiRecipe = findMatchingRecipeOfType(world, inv, multiType);
		} while (!multiRecipe.isPresent());

		return multiRecipe;
	}

	public Optional findMatchingRecipeOfAppropriateType(World world, PlayerEntity player, CraftingInventory inv) {
		return standardCraftingTableRecipes ? findMatchingRecipeOfType(world, inv, IRecipeType.CRAFTING) : findMatchingFactionOrMulti(world, inv);
	}

	private Optional findMatchingRecipeOfType(World world, CraftingInventory inv, IRecipeType type) {
		return world.getRecipeManager().getRecipeFor(type, inv, world);
	}

	public Block getCraftingBlock() {
		return initData.tableBlock;
	}

	protected Faction getFaction() {
		return initData.faction;
	}

	protected FactionTableType getRecipeType() {
		return initData.factionRecipeType;
	}

	public boolean isStandardCraftingActive() {
		return standardCraftingTableRecipes;
	}

	@Override
	public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				worldPos.execute((world, pos) -> {
					itemstack1.getItem().onCraftedBy(itemstack1, world, player);
				});
				if (!moveItemStackTo(itemstack1, 10, 46, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (index >= 10 && index < 46) {
				if (!moveItemStackTo(itemstack1, 1, 10, false)) {
					if (index < 37) {
						if (!moveItemStackTo(itemstack1, 37, 46, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!moveItemStackTo(itemstack1, 10, 37, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!moveItemStackTo(itemstack1, 10, 46, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			ItemStack itemstack2 = slot.onTake(player, itemstack1);
			if (index == 0) {
				player.drop(itemstack2, false);
			}
		}

		return itemstack;
	}

	@Override
	public void removed(PlayerEntity player) {
		super.removed(player);
		worldPos.execute((world, pos) -> {
			clearContainer(player, world, craftMatrix);
		});
	}

	public void setStandardCraftingActive(boolean flag) {
		boolean prev = standardCraftingTableRecipes;
		standardCraftingTableRecipes = flag;
		if (standardCraftingTableRecipes != prev) {
			slotsChanged(craftMatrix);
		}

	}

	public FactionCraftingContainer setWorldPosCallable(IWorldPosCallable pos) {
		worldPos = pos;
		return this;
	}

	@Override
	public void slotsChanged(IInventory inv) {
		worldPos.execute((world, pos) -> {
			tryMatchRecipe(containerId, world, craftingPlayer, craftMatrix, craftResult);
		});
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		return stillValid(worldPos, player, getCraftingBlock());
	}

	protected void tryMatchRecipe(int windowID, World world, PlayerEntity player, CraftingInventory inv, CraftResultInventory resultInv) {
		if (!world.isClientSide) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			Optional optRecipe = findMatchingRecipeOfAppropriateType(world, serverPlayer, inv);
			ItemStack result = ItemStack.EMPTY;
			if (optRecipe.isPresent()) {
				ICraftingRecipe recipe = (ICraftingRecipe) optRecipe.get();
				if (resultInv.setRecipeUsed(world, serverPlayer, recipe)) {
					result = recipe.assemble(inv);
				}
			}

			resultInv.setItem(0, result);
			serverPlayer.connection.send(new SSetSlotPacket(windowID, 0, result));
		}

	}

	public static class FactionCraftingContainerInitData {
		private final Block tableBlock;
		private final FactionTableType factionRecipeType;
		private final Faction faction;

		public FactionCraftingContainerInitData(Block tableBlock, FactionTableType factionRecipeType, Faction faction) {
			this.tableBlock = tableBlock;
			this.factionRecipeType = factionRecipeType;
			this.faction = faction;
		}

		public void write(PacketBuffer buf) {
			buf.writeResourceLocation(tableBlock.getRegistryName());
			buf.writeResourceLocation(factionRecipeType.recipeTypeName);
			buf.writeResourceLocation(faction.getName());
		}

		public static FactionCraftingContainer.FactionCraftingContainerInitData read(PacketBuffer buf, World world) {
			Block tableBlock = ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation());
			FactionTableType factionRecipeType = (FactionTableType) LOTRRecipes.findRecipeTypeByNameOrThrow(buf.readResourceLocation(), FactionTableType.class);
			Faction faction = FactionSettingsManager.sidedInstance(world).getCurrentLoadedFactions().getFactionByName(buf.readResourceLocation());
			return new FactionCraftingContainer.FactionCraftingContainerInitData(tableBlock, factionRecipeType, faction);
		}
	}
}
