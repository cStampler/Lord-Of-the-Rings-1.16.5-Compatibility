package lotr.common.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lotr.common.init.LOTRTags;
import lotr.common.inv.SlotAndCount;
import lotr.common.recipe.AbstractAlloyForgeRecipe;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public abstract class AbstractAlloyForgeTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
	public static final int RECIPE_FUNCTIONALITY_VERSION_FOR_JEI = 3;
	private int[] SLOTS_INGREDIENT = { 0, 1, 2, 3 };
	private int[] SLOTS_ALLOY = { 4, 5, 6, 7 };
	private int[] SLOTS_OUTPUT = { 8, 9, 10, 11 };
	private int SLOT_FUEL = 12;
	protected NonNullList inventory;
	private int burnTime;
	private int burnTimeTotal;
	private int cookTime;
	private int cookTimeTotal;
	protected final IIntArray forgeData;
	private final Map usedRecipes;
	protected final IRecipeType[] recipeTypes;
	protected final IRecipeType[] alloyRecipeTypes;
	private LazyOptional[] handlers;

	protected AbstractAlloyForgeTileEntity(TileEntityType type, IRecipeType[] recipes, IRecipeType[] alloyRecipes) {
		super(type);
		inventory = NonNullList.withSize(SLOTS_INGREDIENT.length + SLOTS_ALLOY.length + SLOTS_OUTPUT.length + 1, ItemStack.EMPTY);
		forgeData = new IIntArray() {
			@Override
			public int get(int index) {
				switch (index) {
				case 0:
					return burnTime;
				case 1:
					return burnTimeTotal;
				case 2:
					return cookTime;
				case 3:
					return cookTimeTotal;
				default:
					return 0;
				}
			}

			@Override
			public int getCount() {
				return 4;
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
				case 0:
					burnTime = value;
					break;
				case 1:
					burnTimeTotal = value;
					break;
				case 2:
					cookTime = value;
					break;
				case 3:
					cookTimeTotal = value;
				}

			}
		};
		usedRecipes = Maps.newHashMap();
		handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
		recipeTypes = recipes;
		alloyRecipeTypes = alloyRecipes;
	}

	@Override
	public void awardUsedRecipes(PlayerEntity player) {
	}

	protected boolean canDoSmelting() {
		for (int i = 0; i < SLOTS_INGREDIENT.length; ++i) {
			int slotI = SLOTS_INGREDIENT[i];
			int slotA = SLOTS_ALLOY[i];
			int slotO = SLOTS_OUTPUT[i];
			if (canSmelt(slotI, slotA, slotO)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if (ArrayUtils.contains(SLOTS_INGREDIENT, index)) {
			return true;
		}
		if (index != SLOT_FUEL) {
			return false;
		}
		ItemStack currentFuel = (ItemStack) inventory.get(1);
		return AbstractFurnaceTileEntity.isFuel(stack) || stack.getItem() == Items.BUCKET && currentFuel.getItem() != Items.BUCKET;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		return canPlaceItem(index, stack);
	}

	protected boolean canSmelt(int slotIngredient, int slotAlloy, int slotOutput) {
		ItemStack output = getSmeltingResult((ItemStack) inventory.get(slotIngredient), (ItemStack) inventory.get(slotAlloy));
		if (output.isEmpty()) {
			return false;
		}
		ItemStack existingOutput = (ItemStack) inventory.get(slotOutput);
		if (existingOutput.isEmpty()) {
			return true;
		}
		if (!existingOutput.sameItem(output)) {
			return false;
		}
		if (existingOutput.getCount() + output.getCount() <= getMaxStackSize() && existingOutput.getCount() + output.getCount() <= existingOutput.getMaxStackSize()) {
			return true;
		}
		return existingOutput.getCount() + output.getCount() <= output.getMaxStackSize();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (direction == Direction.DOWN && index == SLOT_FUEL) {
			Item item = stack.getItem();
			if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void clearContent() {
		inventory.clear();
	}

	protected void doSmelt() {
		for (int i = 0; i < SLOTS_INGREDIENT.length; ++i) {
			int slotI = SLOTS_INGREDIENT[i];
			int slotA = SLOTS_ALLOY[i];
			int slotO = SLOTS_OUTPUT[i];
			smeltItemInSlot(slotI, slotA, slotO);
		}

	}

	@Override
	public void fillStackedContents(RecipeItemHelper helper) {
		Iterator var2 = inventory.iterator();

		while (var2.hasNext()) {
			ItemStack itemstack = (ItemStack) var2.next();
			helper.accountStack(itemstack);
		}

	}

	protected int getBurnTime(ItemStack itemstack) {
		if (itemstack.isEmpty()) {
			return 0;
		}
		itemstack.getItem();
		return ForgeHooks.getBurnTime(itemstack);
	}

	@Override
	public LazyOptional getCapability(Capability capability, Direction facing) {
		if (remove || facing == null || capability != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return super.getCapability(capability, facing);
		}
		if (facing == Direction.UP) {
			return handlers[0].cast();
		}
		return facing == Direction.DOWN ? handlers[1].cast() : handlers[2].cast();
	}

	@Override
	public int getContainerSize() {
		return inventory.size();
	}

	protected int getCookTime() {
		return 400;
	}

	@Override
	public ItemStack getItem(int index) {
		return (ItemStack) inventory.get(index);
	}

	@Override
	public IRecipe getRecipeUsed() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.DOWN) {
			return LOTRUtil.combineArrays(SLOTS_OUTPUT, new int[] { SLOT_FUEL });
		}
		return side == Direction.UP ? SlotAndCount.sortSlotsByCount(this, SLOTS_INGREDIENT) : new int[] { SLOT_FUEL };
	}

	public final ItemStack getSmeltingResult(ItemStack ingredientStack, ItemStack alloyStack) {
		if (ingredientStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		Inventory furnaceTestInv;
		IRecipeType[] var4;
		int var5;
		int var6;
		IRecipeType recipeType;
		IRecipe furnaceRecipe;
		ItemStack output;
		if (!alloyStack.isEmpty()) {
			furnaceTestInv = new Inventory(2);
			furnaceTestInv.setItem(0, ingredientStack);
			furnaceTestInv.setItem(1, alloyStack);
			var4 = alloyRecipeTypes;
			var5 = var4.length;

			for (var6 = 0; var6 < var5; ++var6) {
				recipeType = var4[var6];
				furnaceRecipe = (IRecipe) level.getRecipeManager().getRecipeFor(recipeType, furnaceTestInv, level).orElse((Object) null);
				if (furnaceRecipe != null) {
					return furnaceRecipe.getResultItem();
				}
			}
		} else {
			furnaceTestInv = new Inventory(1);
			furnaceTestInv.setItem(0, ingredientStack);
			var4 = recipeTypes;
			var5 = var4.length;

			for (var6 = 0; var6 < var5; ++var6) {
				recipeType = var4[var6];
				furnaceRecipe = (IRecipe) level.getRecipeManager().getRecipeFor(recipeType, furnaceTestInv, level).orElse((Object) null);
				if (furnaceRecipe != null) {
					output = furnaceRecipe.getResultItem();
					if (recipeType != IRecipeType.SMELTING || isDefaultFurnaceRecipeAcceptable(ingredientStack, output)) {
						return output;
					}
				}
			}
		}

		return ItemStack.EMPTY;
	}

	private boolean isBurning() {
		return burnTime > 0;
	}

	protected boolean isDefaultFurnaceRecipeAcceptable(ItemStack ingredientStack, ItemStack resultStack) {
		Item ingredient = ingredientStack.getItem();
		if (ingredient instanceof BlockItem) {
			Block block = ((BlockItem) ingredient).getBlock();
			Material material = block.defaultBlockState().getMaterial();
			if (material == Material.STONE || material == Material.SAND || material == Material.CLAY) {
				return true;
			}
		}

		if (ingredient.is(LOTRTags.Items.ALLOY_FORGE_EXTRA_SMELTABLES)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		Iterator var1 = inventory.iterator();

		ItemStack itemstack;
		do {
			if (!var1.hasNext()) {
				return true;
			}

			itemstack = (ItemStack) var1.next();
		} while (itemstack.isEmpty());

		return false;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(nbt, inventory);
		burnTime = nbt.getInt("BurnTime");
		cookTime = nbt.getInt("CookTime");
		cookTimeTotal = nbt.getInt("CookTimeTotal");
		burnTimeTotal = getBurnTime((ItemStack) inventory.get(SLOT_FUEL));
		int rSize = nbt.getShort("RecipesUsedSize");

		for (int ri = 0; ri < rSize; ++ri) {
			ResourceLocation res = new ResourceLocation(nbt.getString("RecipeLocation" + ri));
			int count = nbt.getInt("RecipeAmount" + ri);
			usedRecipes.put(res, count);
		}

	}

	public void onResultTaken(PlayerEntity player) {
		List recipes = Lists.newArrayList();
		Iterator var3 = usedRecipes.entrySet().iterator();

		while (var3.hasNext()) {
			Entry entry = (Entry) var3.next();
			player.level.getRecipeManager().byKey((ResourceLocation) entry.getKey()).ifPresent(recipe -> {
				recipes.add(recipe);
				float xp = 0.0F;
				if (recipe instanceof AbstractCookingRecipe) {
					xp = ((AbstractCookingRecipe) recipe).getExperience();
				} else if (recipe instanceof AbstractAlloyForgeRecipe) {
					xp = ((AbstractAlloyForgeRecipe) recipe).getExperience();
				}

				LOTRUtil.spawnXPOrbs(player, (Integer) entry.getValue(), xp);
			});
		}

		player.awardRecipes(recipes);
		usedRecipes.clear();
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ItemStackHelper.removeItem(inventory, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStackHelper.takeItem(inventory, index);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putInt("BurnTime", burnTime);
		nbt.putInt("CookTime", cookTime);
		nbt.putInt("CookTimeTotal", cookTimeTotal);
		ItemStackHelper.saveAllItems(nbt, inventory);
		nbt.putShort("RecipesUsedSize", (short) usedRecipes.size());
		int ri = 0;

		for (Iterator var3 = usedRecipes.entrySet().iterator(); var3.hasNext(); ++ri) {
			Entry entry = (Entry) var3.next();
			nbt.putString("RecipeLocation" + ri, ((ResourceLocation) entry.getKey()).toString());
			nbt.putInt("RecipeAmount" + ri, (Integer) entry.getValue());
		}

		return nbt;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		ItemStack itemstack = (ItemStack) inventory.get(index);
		boolean sameItem = !stack.isEmpty() && stack.sameItem(itemstack) && ItemStack.tagMatches(stack, itemstack);
		inventory.set(index, stack);
		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}

		if (ArrayUtils.contains(SLOTS_INGREDIENT, index) && !sameItem) {
			cookTimeTotal = getCookTime();
			setChanged();
		}

	}

	@Override
	public void setRecipeUsed(IRecipe recipe) {
		if (recipe != null) {
			usedRecipes.compute(recipe.getId(), (res, i) -> Integer.valueOf(1 + (i == null ? 0 : ((Integer) i).intValue())));
		}

	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		for (LazyOptional handler : handlers) {
			handler.invalidate();
		}

	}

	private void smeltItemInSlot(int slotIngredient, int slotAlloy, int slotOutput) {
		IRecipe foundRecipe = null;
		boolean isAlloyRecipe = false;
		ItemStack ingredientStack = (ItemStack) inventory.get(slotIngredient);
		ItemStack alloyStack = (ItemStack) inventory.get(slotAlloy);
		Inventory alloyTestInv;
		IRecipeType[] var9;
		int var10;
		int var11;
		IRecipeType recipeType;
		if (!alloyStack.isEmpty()) {
			alloyTestInv = new Inventory(2);
			alloyTestInv.setItem(0, ingredientStack);
			alloyTestInv.setItem(1, alloyStack);
			var9 = alloyRecipeTypes;
			var10 = var9.length;

			for (var11 = 0; var11 < var10; ++var11) {
				recipeType = var9[var11];
				foundRecipe = (IRecipe) level.getRecipeManager().getRecipeFor(recipeType, alloyTestInv, level).orElse((Object) null);
				if (foundRecipe != null) {
					isAlloyRecipe = true;
					break;
				}
			}
		} else {
			alloyTestInv = new Inventory(1);
			alloyTestInv.setItem(0, ingredientStack);
			var9 = recipeTypes;
			var10 = var9.length;

			for (var11 = 0; var11 < var10; ++var11) {
				recipeType = var9[var11];
				foundRecipe = (IRecipe) level.getRecipeManager().getRecipeFor(recipeType, alloyTestInv, level).orElse((Object) null);
				if (foundRecipe != null) {
					break;
				}
			}
		}

		if (foundRecipe != null && canSmelt(slotIngredient, slotAlloy, slotOutput)) {
			ItemStack output = foundRecipe.getResultItem();
			ItemStack existingOutput = (ItemStack) inventory.get(slotOutput);
			if (existingOutput.isEmpty()) {
				inventory.set(slotOutput, output.copy());
			} else if (existingOutput.getItem() == output.getItem()) {
				existingOutput.grow(output.getCount());
			}

			if (!level.isClientSide) {
				this.setRecipeUsed(foundRecipe);
			}

			if (ingredientStack.getItem() == Blocks.WET_SPONGE.asItem() && !((ItemStack) inventory.get(SLOT_FUEL)).isEmpty() && ((ItemStack) inventory.get(SLOT_FUEL)).getItem() == Items.BUCKET) {
				inventory.set(SLOT_FUEL, new ItemStack(Items.WATER_BUCKET));
			}

			ingredientStack.shrink(1);
			if (isAlloyRecipe) {
				alloyStack.shrink(1);
			}
		}

	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if (level.getBlockEntity(worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void tick() {
		boolean wasBurning = isBurning();
		boolean needUpdate = false;
		if (isBurning()) {
			--burnTime;
		}

		if (!level.isClientSide) {
			ItemStack fuelItem = (ItemStack) inventory.get(SLOT_FUEL);
			boolean anyIngredients = false;
			int[] var5 = SLOTS_INGREDIENT;
			int var6 = var5.length;

			for (int var7 = 0; var7 < var6; ++var7) {
				int i = var5[var7];
				if (!((ItemStack) inventory.get(i)).isEmpty()) {
					anyIngredients = true;
					break;
				}
			}

			if (!isBurning() && (fuelItem.isEmpty() || !anyIngredients)) {
				if (!isBurning() && cookTime > 0) {
					cookTime = MathHelper.clamp(cookTime - 2, 0, cookTimeTotal);
				}
			} else {
				if (!isBurning() && canDoSmelting()) {
					burnTime = getBurnTime(fuelItem);
					burnTimeTotal = burnTime;
					if (isBurning()) {
						needUpdate = true;
						if (fuelItem.hasContainerItem()) {
							inventory.set(SLOT_FUEL, fuelItem.getContainerItem());
						} else if (!fuelItem.isEmpty()) {
							fuelItem.getItem();
							fuelItem.shrink(1);
							if (fuelItem.isEmpty()) {
								inventory.set(SLOT_FUEL, fuelItem.getContainerItem());
							}
						}
					}
				}

				if (isBurning() && canDoSmelting()) {
					++cookTime;
					if (cookTime == cookTimeTotal) {
						cookTime = 0;
						cookTimeTotal = getCookTime();
						doSmelt();
						needUpdate = true;
					}
				} else {
					cookTime = 0;
				}
			}

			if (wasBurning != isBurning()) {
				needUpdate = true;
				level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(AbstractFurnaceBlock.LIT, isBurning()), 3);
			}
		}

		if (needUpdate) {
			setChanged();
		}

	}
}
