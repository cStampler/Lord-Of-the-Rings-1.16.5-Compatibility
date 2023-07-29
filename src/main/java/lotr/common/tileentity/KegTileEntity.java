package lotr.common.tileentity;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.*;

import lotr.common.LOTRLog;
import lotr.common.block.KegBlock;
import lotr.common.init.*;
import lotr.common.inv.*;
import lotr.common.item.VesselDrinkItem;
import lotr.common.recipe.*;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class KegTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
	public static final int DEFAULT_BREWING_TIME = 12000;
	public static final int FULL_KEG_AMOUNT = 16;
	private static final int[] SLOTS_INGREDIENT = { 0, 1, 2, 3, 4, 5 };
	private static final int[] SLOTS_WATER = { 6, 7, 8 };
	private NonNullList inventory;
	private KegTileEntity.KegMode kegMode;
	private int brewTime;
	private int brewTimeTotal;
	private final IIntArray kegData;
	private ResourceLocation currentBrewingRecipe;
	private final Map usedRecipes;
	private List usingPlayers;
	private int numPlayersOpened;
	private LazyOptional[] handlers;

	public KegTileEntity() {
		super((TileEntityType) LOTRTileEntities.KEG.get());
		inventory = NonNullList.withSize(SLOTS_INGREDIENT.length + SLOTS_WATER.length + 1, ItemStack.EMPTY);
		kegMode = KegTileEntity.KegMode.EMPTY;
		kegData = new IIntArray() {
			@Override
			public int get(int index) {
				switch (index) {
				case 0:
					return kegMode.id;
				case 1:
					return brewTime;
				case 2:
					return brewTimeTotal;
				default:
					return 0;
				}
			}

			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
				case 0:
					kegMode = KegTileEntity.KegMode.forId(value);
					break;
				case 1:
					brewTime = value;
					break;
				case 2:
					brewTimeTotal = value;
				}

			}
		};
		usedRecipes = Maps.newHashMap();
		usingPlayers = new ArrayList();
		handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);
	}

	private void addUsingPlayer(ServerPlayerEntity player) {
		if (!usingPlayers.contains(player)) {
			usingPlayers.add(player);
		}

	}

	@Override
	public void awardUsedRecipes(PlayerEntity player) {
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if (ArrayUtils.contains(SLOTS_INGREDIENT, index)) {
			return true;
		}
		return ArrayUtils.contains(SLOTS_WATER, index) ? DrinkBrewingRecipe.isWaterSource(stack) : false;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		return kegMode == KegTileEntity.KegMode.EMPTY ? canPlaceItem(index, stack) : false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		if (direction == Direction.DOWN && isIngredientOrWaterSlot(index)) {
			return kegMode != KegTileEntity.KegMode.EMPTY;
		}
		return false;
	}

	public void clearBrewedSlot() {
		setItem(9, ItemStack.EMPTY);
	}

	@Override
	public void clearContent() {
		inventory.clear();
		updateBrewingRecipe();
	}

	private void completeCurrentRecipe(PlayerEntity player) {
		List recipes = Lists.newArrayList();
		Iterator var3 = usedRecipes.entrySet().iterator();

		while (var3.hasNext()) {
			Entry entry = (Entry) var3.next();
			player.level.getRecipeManager().byKey((ResourceLocation) entry.getKey()).ifPresent(recipe -> {
				recipes.add(recipe);
				float xp = 0.0F;
				if (recipe instanceof DrinkBrewingRecipe) {
					xp = ((DrinkBrewingRecipe) recipe).getExperience();
				}

				LOTRUtil.spawnXPOrbs(player, (Integer) entry.getValue(), xp);
			});
		}

		player.awardRecipes(recipes);
		usedRecipes.clear();
		currentBrewingRecipe = null;
		brewTimeTotal = 0;
	}

	public void consumeServing() {
		ItemStack drink = getFinishedBrewDrink();
		if (!drink.isEmpty()) {
			drink.shrink(1);
			setItem(9, drink);
			if (drink.isEmpty()) {
				kegMode = KegTileEntity.KegMode.EMPTY;
				setChanged();
			}
		}

	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new KegContainer(id, player, this, kegData);
	}

	public void dropContentsExceptBrew() {
		for (int i = 0; i < getContainerSize(); ++i) {
			if (i != 9) {
				InventoryHelper.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), getItem(i));
			}
		}

	}

	public void fillBrewedWith(ItemStack stack) {
		setItem(9, stack);
		kegMode = KegTileEntity.KegMode.FULL;
		setChanged();
	}

	@Override
	public void fillStackedContents(RecipeItemHelper helper) {
		Iterator var2 = inventory.iterator();

		while (var2.hasNext()) {
			ItemStack itemstack = (ItemStack) var2.next();
			helper.accountStack(itemstack);
		}

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

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container.lotr.keg");
	}

	public ItemStack getFinishedBrewDrink() {
		return kegMode == KegTileEntity.KegMode.FULL ? getItem(9) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack getItem(int index) {
		return (ItemStack) inventory.get(index);
	}

	public KegTileEntity.KegMode getKegMode() {
		return kegMode;
	}

	@Override
	public IRecipe getRecipeUsed() {
		return null;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.DOWN) {
			return LOTRUtil.combineArrays(SLOTS_INGREDIENT, SLOTS_WATER);
		}
		return side == Direction.UP ? SlotAndCount.sortSlotsByCount(this, SLOTS_INGREDIENT) : SLOTS_WATER;
	}

	public void handleBrewButtonPress(ServerPlayerEntity player) {
		ItemStack brewingItem = getItem(9);
		if (kegMode == KegTileEntity.KegMode.EMPTY && !brewingItem.isEmpty()) {
			kegMode = KegTileEntity.KegMode.BREWING;
			int[] inputSlots = LOTRUtil.combineArrays(SLOTS_INGREDIENT, SLOTS_WATER);
			int[] var4 = inputSlots;
			int var5 = inputSlots.length;

			for (int var6 = 0; var6 < var5; ++var6) {
				int slot = var4[var6];
				ItemStack inSlot = getItem(slot);
				if (!inSlot.isEmpty()) {
					ItemStack suitableContainer = ItemStack.EMPTY;
					if (inSlot.hasContainerItem()) {
						suitableContainer = inSlot.getContainerItem();
						if (suitableContainer.isDamageableItem() && suitableContainer.getDamageValue() > suitableContainer.getMaxDamage()) {
							suitableContainer = ItemStack.EMPTY;
						}
					}

					inSlot.shrink(1);
					if (inSlot.isEmpty()) {
						setItem(slot, suitableContainer);
					}
				}
			}

			resendKegInventoryToPlayers();
		} else if (kegMode == KegTileEntity.KegMode.BREWING && !brewingItem.isEmpty()) {
			VesselDrinkItem.Potency potency = VesselDrinkItem.getPotency(brewingItem);
			if (!potency.isMin()) {
				VesselDrinkItem.setPotency(brewingItem, potency.getPrev());
				kegMode = KegTileEntity.KegMode.FULL;
				brewTime = 0;
				brewTimeTotal = 0;
				completeCurrentRecipe(player);
			}
		}

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

	private boolean isIngredientOrWaterSlot(int index) {
		return ArrayUtils.contains(SLOTS_INGREDIENT, index) || ArrayUtils.contains(SLOTS_WATER, index);
	}

	public void kegTick() {
		int i = worldPosition.getX();
		int j = worldPosition.getY();
		int k = worldPosition.getZ();
		numPlayersOpened = LOTRUtil.calculatePlayersUsingSingleContainer(level, i, j, k, KegContainer.class, kegContainer -> (((KegContainer) kegContainer).theKeg == this));
		if (numPlayersOpened > 0) {
			scheduleTick();
		} else {
			BlockState state = getBlockState();
			if (state.getBlock() != LOTRBlocks.KEG.get()) {
				LOTRLog.warn("Keg tileentity ticking at (%s) expected keg block but found %s - removing", worldPosition, state.getBlock().getRegistryName());
				setRemoved();
				return;
			}

			boolean open = state.getValue(KegBlock.OPEN);
			if (open) {
				playOpenOrCloseSound(state, SoundEvents.BARREL_CLOSE);
				setOpenProperty(state, false);
			}
		}

	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		CompoundNBT droppableNbt = nbt.getCompound("KegDroppableData");
		inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(droppableNbt, inventory);
		kegMode = KegTileEntity.KegMode.forId(droppableNbt.getByte("KegMode"));
		brewTime = droppableNbt.getInt("BrewingTime");
		brewTimeTotal = droppableNbt.getInt("BrewingTimeTotal");
		if (droppableNbt.contains("CurrentRecipe")) {
			currentBrewingRecipe = new ResourceLocation(droppableNbt.getString("CurrentRecipe"));
		}

		if (droppableNbt.contains("RecipesUsed")) {
			ListNBT recipesList = droppableNbt.getList("RecipesUsed", 10);

			for (int ri = 0; ri < recipesList.size(); ++ri) {
				CompoundNBT recipeTag = recipesList.getCompound(ri);
				ResourceLocation res = new ResourceLocation(recipeTag.getString("Id"));
				int count = recipeTag.getInt("Count");
				usedRecipes.put(res, count);
			}
		}

	}

	private void playOpenOrCloseSound(BlockState state, SoundEvent sound) {
		Vector3i facingOffset = state.getValue(HorizontalBlock.FACING).getNormal();
		double x = worldPosition.getX() + 0.5D + facingOffset.getX() / 2.0D;
		double y = worldPosition.getY() + 0.5D + facingOffset.getY() / 2.0D;
		double z = worldPosition.getZ() + 0.5D + facingOffset.getZ() / 2.0D;
		level.playSound((PlayerEntity) null, x, y, z, sound, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack ret = ItemStackHelper.removeItem(inventory, index, count);
		if (isIngredientOrWaterSlot(index)) {
			updateBrewingRecipe();
		}

		return ret;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack ret = ItemStackHelper.takeItem(inventory, index);
		if (isIngredientOrWaterSlot(index)) {
			updateBrewingRecipe();
		}

		return ret;
	}

	private void removeUsingPlayer(ServerPlayerEntity player) {
		usingPlayers.remove(player);
	}

	private void resendKegInventoryToPlayers() {
		Iterator var1 = usingPlayers.iterator();

		while (var1.hasNext()) {
			ServerPlayerEntity user = (ServerPlayerEntity) var1.next();
			user.containerMenu.broadcastChanges();
		}

	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		CompoundNBT droppableNbt = new CompoundNBT();
		ItemStackHelper.saveAllItems(droppableNbt, inventory);
		droppableNbt.putByte("KegMode", (byte) kegMode.id);
		droppableNbt.putInt("BrewingTime", brewTime);
		droppableNbt.putInt("BrewingTimeTotal", brewTimeTotal);
		if (currentBrewingRecipe != null) {
			droppableNbt.putString("CurrentRecipe", currentBrewingRecipe.toString());
		}

		ListNBT recipesList = new ListNBT();
		Iterator var4 = usedRecipes.entrySet().iterator();

		while (var4.hasNext()) {
			Entry entry = (Entry) var4.next();
			CompoundNBT recipeTag = new CompoundNBT();
			recipeTag.putString("Id", ((ResourceLocation) entry.getKey()).toString());
			recipeTag.putInt("Count", (Integer) entry.getValue());
			recipesList.add(recipeTag);
		}

		droppableNbt.put("RecipesUsed", recipesList);
		nbt.put("KegDroppableData", droppableNbt);
		return nbt;
	}

	private void scheduleTick() {
		level.getBlockTicks().scheduleTick(getBlockPos(), getBlockState().getBlock(), 5);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		inventory.get(index);
		inventory.set(index, stack);
		if (stack.getCount() > getMaxStackSize()) {
			stack.setCount(getMaxStackSize());
		}

		if (isIngredientOrWaterSlot(index)) {
			updateBrewingRecipe();
		}

	}

	private void setOpenProperty(BlockState state, boolean open) {
		level.setBlock(getBlockPos(), state.setValue(KegBlock.OPEN, open), 3);
	}

	@Override
	public void setRecipeUsed(IRecipe recipe) {
		if (recipe != null) {
			setRecipeUsed_id(recipe.getId());
		}

	}

	private void setRecipeUsed_id(ResourceLocation rId) {
		usedRecipes.compute(rId, (res, i) -> Integer.valueOf(1 + (i == null ? 0 : ((Integer) i).intValue())));
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		for (LazyOptional handler : handlers) {
			handler.invalidate();
		}

	}

	@Override
	public void startOpen(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			addUsingPlayer((ServerPlayerEntity) player);
		}

		if (!player.isSpectator()) {
			if (numPlayersOpened < 0) {
				numPlayersOpened = 0;
			}

			++numPlayersOpened;
			BlockState state = getBlockState();
			boolean open = state.getValue(KegBlock.OPEN);
			if (!open) {
				playOpenOrCloseSound(state, SoundEvents.BARREL_OPEN);
				setOpenProperty(state, true);
			}

			scheduleTick();
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
	public void stopOpen(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity) {
			removeUsingPlayer((ServerPlayerEntity) player);
		}

		if (!player.isSpectator()) {
			--numPlayersOpened;
		}

	}

	@Override
	public void tick() {
		boolean needUpdate = false;
		if (!level.isClientSide) {
			if (kegMode == KegTileEntity.KegMode.BREWING) {
				ItemStack brewingItem = getItem(9);
				if (!brewingItem.isEmpty()) {
					++brewTime;
					if (brewTime >= brewTimeTotal) {
						brewTime = 0;
						VesselDrinkItem.Potency potency = VesselDrinkItem.getPotency(brewingItem);
						if (potency.isMax()) {
							kegMode = KegTileEntity.KegMode.FULL;
							brewTime = 0;
							brewTimeTotal = 0;
						} else {
							VesselDrinkItem.setPotency(brewingItem, potency.getNext());
							setRecipeUsed_id(currentBrewingRecipe);
						}
						needUpdate = true;
					}
				} else {
					kegMode = KegTileEntity.KegMode.EMPTY;
					brewTime = 0;
					brewTimeTotal = 0;
					currentBrewingRecipe = null;
					needUpdate = true;
				}
			} else {
				brewTime = 0;
			}

			if (kegMode == KegTileEntity.KegMode.FULL) {
				if (!usingPlayers.isEmpty() && currentBrewingRecipe != null) {
					PlayerEntity firstUser = (PlayerEntity) usingPlayers.get(0);
					completeCurrentRecipe(firstUser);
				}

				if (getItem(9).isEmpty()) {
					kegMode = KegTileEntity.KegMode.EMPTY;
					needUpdate = true;
				}
			}
		}

		if (needUpdate) {
			setChanged();
		}

	}

	private void updateBrewingRecipe() {
		if (kegMode == KegTileEntity.KegMode.EMPTY) {
			IInventory proxyInv = new Inventory(9);
			int[] var2 = SLOTS_INGREDIENT;
			int var3 = var2.length;

			int var4;
			int i;
			for (var4 = 0; var4 < var3; ++var4) {
				i = var2[var4];
				proxyInv.setItem(i, getItem(i));
			}

			var2 = SLOTS_WATER;
			var3 = var2.length;

			for (var4 = 0; var4 < var3; ++var4) {
				i = var2[var4];
				proxyInv.setItem(i, getItem(i));
			}

			Optional opt = level.getRecipeManager().getRecipeFor(LOTRRecipes.DRINK_BREWING, proxyInv, level);
			if (opt.isPresent()) {
				DrinkBrewingRecipe recipe = (DrinkBrewingRecipe) opt.get();
				currentBrewingRecipe = recipe.getId();
				ItemStack result = recipe.assemble(proxyInv);
				result.setCount(16);
				VesselDrinkItem.setPotency(result, VesselDrinkItem.Potency.WEAK);
				setItem(9, result);
				brewTimeTotal = recipe.getBrewTime();
			} else {
				currentBrewingRecipe = null;
				setItem(9, ItemStack.EMPTY);
				brewTimeTotal = 0;
			}

			setChanged();
			if (!level.isClientSide) {
				resendKegInventoryToPlayers();
			}
		}

	}

	public enum KegMode {
		EMPTY(0), BREWING(1), FULL(2);

		public final int id;

		KegMode(int i) {
			id = i;
		}

		public static KegTileEntity.KegMode forId(int i) {
			KegTileEntity.KegMode[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				KegTileEntity.KegMode b = var1[var3];
				if (b.id == i) {
					return b;
				}
			}

			return EMPTY;
		}
	}
}
