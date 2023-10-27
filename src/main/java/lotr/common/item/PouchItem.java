package lotr.common.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import io.netty.buffer.Unpooled;
import lotr.common.LOTRMod;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.init.LOTRContainers;
import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRSoundEvents;
import lotr.common.inv.OpenPouchContainer;
import lotr.common.inv.PouchContainer;
import lotr.common.inv.PouchInventory;
import lotr.common.stat.LOTRStats;
import lotr.common.util.PlayerInventorySlotsHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class PouchItem extends Item {
	public static final Set<Item> ALL_POUCH_ITEMS = new HashSet<Item>();
	public static final Map<Integer, PouchItem> POUCHES_BY_CAPACITY = new HashMap<Integer, PouchItem>();
	public static final int MAX_NAME_LENGTH = 64;
	private final int capacity;
	private final SoundEvent openSound;
	private final SoundEvent closeSound;

	public PouchItem(int capacity, SoundEvent openSound, SoundEvent closeSound) {
		this(new Properties().stacksTo(1).tab(LOTRItemGroups.MISC), capacity, openSound, closeSound);
	}

	public PouchItem(Properties properties, int capacity, SoundEvent openSound, SoundEvent closeSound) {
		super(properties);
		this.capacity = capacity;
		this.openSound = openSound;
		this.closeSound = closeSound;
		ALL_POUCH_ITEMS.add(this);
		if (POUCHES_BY_CAPACITY.containsKey(capacity)) {
			throw new IllegalArgumentException(String.format("Tried to add a new pouch item with capacity %d, but a pouch item with that capacity already exists - %s", capacity, ((PouchItem) POUCHES_BY_CAPACITY.get(capacity)).getCapacity()));
		}
		POUCHES_BY_CAPACITY.put(capacity, this);
	}

	private void addShulkerBoxStyleTooltip(ItemStack pouch, List<ITextComponent> tooltip) {
		TextFormatting textColor = TextFormatting.DARK_GRAY;
		PouchInventory pouchInv = getPouchInventoryForTooltip(pouch);
		int listed = 0;
		int total = 0;

		for (int i = 0; i < pouchInv.getContainerSize(); ++i) {
			ItemStack stack = pouchInv.getItem(i);
			if (!stack.isEmpty()) {
				++total;
				if (listed < 5) {
					++listed;
					IFormattableTextComponent itemName = stack.getHoverName().copy();
					itemName.append(" x").append(String.valueOf(stack.getCount()));
					tooltip.add(itemName.withStyle(textColor));
				}
			}
		}

		if (total - listed > 0) {
			tooltip.add(new TranslationTextComponent("container.shulkerBox.more", total - listed).withStyle(TextFormatting.ITALIC, textColor));
		}

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (getPickedUpNewItems(stack)) {
			tooltip.add(new TranslationTextComponent("item.lotr.pouch.picked_up_new_items").withStyle(TextFormatting.YELLOW));
		}

		int slots = capacity;
		int slotsFull = determineSlotsFull(stack);
		tooltip.add(new TranslationTextComponent("item.lotr.pouch.slots", slotsFull, slots).withStyle(TextFormatting.GRAY));
		addShulkerBoxStyleTooltip(stack, tooltip);
		if (isPouchDyed(stack)) {
			Faction dyedByFaction = getPouchDyedByFaction(stack, world);
			if (dyedByFaction != null) {
				tooltip.add(new TranslationTextComponent("item.lotr.pouch.dyed.faction", dyedByFaction.getDisplayName()).withStyle(TextFormatting.GRAY));
			} else {
				tooltip.add(new TranslationTextComponent("item.lotr.pouch.dyed").withStyle(TextFormatting.GRAY));
			}
		}

	}

	private int determineSlotsFull(ItemStack pouch) {
		return getPouchInventoryForTooltip(pouch).getNumSlotsFull();
	}

	public int getCapacity() {
		return capacity;
	}

	public SoundEvent getCloseSound() {
		return closeSound;
	}

	public SoundEvent getOpenSound() {
		return openSound;
	}

	private PouchInventory getPouchInventoryForTooltip(ItemStack pouch) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		if (player != null && player.containerMenu instanceof OpenPouchContainer) {
			OpenPouchContainer container = (OpenPouchContainer) player.containerMenu;
			if (container.isOpenPouch(pouch)) {
				return container.getPouchInventory();
			}
		}

		return PouchInventory.temporaryReadOnly(pouch);
	}

	@Override
	public ActionResult<ItemStack>  use(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (!world.isClientSide) {
			ContainerType<PouchContainer> containerType = (ContainerType<PouchContainer>)LOTRContainers.POUCH.get();
			ITextComponent containerTitle = heldItem.getHoverName();
			int invSlot = PlayerInventorySlotsHelper.getHandHeldItemIndex(player, hand);
			PacketBuffer initData = new PacketBuffer(Unpooled.buffer());
			PouchContainer.writeContainerInitData(initData, invSlot);
			NetworkHooks.openGui((ServerPlayerEntity) player, new SimpleNamedContainerProvider((i, inv, p) -> containerType.create(i, inv, initData), containerTitle), buf -> {
				PouchContainer.writeContainerInitData(buf, invSlot);
			});
			player.awardStat(LOTRStats.OPEN_POUCH);
			world.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), getOpenSound(), SoundCategory.PLAYERS, 1.0F, 1.0F);
		}

		return ActionResult.success(heldItem);
	}

	public static void attemptRestockPouches(PlayerEntity player) {
		PlayerInventory inv = player.inventory;
		List<Integer> pouchSlots = new ArrayList<Integer>();
		List<Integer> itemSlots = new ArrayList<Integer>();

		for (int i = 0; i < inv.items.size(); ++i) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof PouchItem) {
					pouchSlots.add(i);
				} else {
					itemSlots.add(i);
				}
			}
		}

		boolean movedAny = false;
		Iterator<Integer> var13 = itemSlots.iterator();

		while (true) {
			while (var13.hasNext()) {
				int i = (Integer) var13.next();
				ItemStack stack = inv.getItem(i);
				Iterator<Integer> var8 = pouchSlots.iterator();

				while (var8.hasNext()) {
					int p = (Integer) var8.next();
					ItemStack pouch = inv.getItem(p);
					PouchItem.AddItemResult result = tryAddItemToPouch(pouch, stack, true);
					if (result != PouchItem.AddItemResult.NONE_ADDED) {
						movedAny = true;
					}

					if (stack.isEmpty()) {
						inv.setItem(i, ItemStack.EMPTY);
						break;
					}
				}
			}

			if (movedAny) {
				player.containerMenu.broadcastChanges();
				player.level.playSound((PlayerEntity) null, player.getX(), player.getY(), player.getZ(), LOTRSoundEvents.RESTOCK_POUCHES, SoundCategory.PLAYERS, 1.0F, 1.0F);
			}

			return;
		}
	}

	public static CompoundNBT getOrCreatePouchRootNBT(ItemStack stack) {
		return stack.getOrCreateTagElement("Pouch");
	}

	public static boolean getPickedUpNewItems(ItemStack stack) {
		CompoundNBT nbt = getPouchRootNBT(stack);
		return nbt != null && nbt.contains("PickedUpNewItems") ? nbt.getBoolean("PickedUpNewItems") : false;
	}

	public static int getPouchColor(ItemStack stack) {
		int dye = getSavedDyeColor(stack);
		return dye != -1 ? dye : 10841676;
	}

	public static Faction getPouchDyedByFaction(ItemStack stack, World world) {
		CompoundNBT nbt = getPouchRootNBT(stack);
		if (nbt != null && nbt.contains("ColorFaction", 8)) {
			ResourceLocation facName = new ResourceLocation(nbt.getString("ColorFaction"));
			return FactionSettingsManager.sidedInstance(world).getCurrentLoadedFactions().getFactionByName(facName);
		}
		return null;
	}

	public static CompoundNBT getPouchRootNBT(ItemStack stack) {
		return stack.getTagElement("Pouch");
	}

	private static int getSavedDyeColor(ItemStack stack) {
		CompoundNBT nbt = getPouchRootNBT(stack);
		return nbt != null && nbt.contains("Color", 3) ? nbt.getInt("Color") : -1;
	}

	public static boolean isPouchDyed(ItemStack stack) {
		return getSavedDyeColor(stack) != -1;
	}

	public static void removePouchDye(ItemStack stack) {
		CompoundNBT nbt = getPouchRootNBT(stack);
		if (nbt != null) {
			nbt.remove("Color");
			nbt.remove("ColorFaction");
		}

	}

	public static void setPickedUpNewItems(ItemStack stack, boolean flag) {
		if (flag) {
			getOrCreatePouchRootNBT(stack).putBoolean("PickedUpNewItems", true);
		} else {
			CompoundNBT nbt = getPouchRootNBT(stack);
			if (nbt != null) {
				nbt.remove("PickedUpNewItems");
			}
		}

	}

	public static void setPouchDyedByColor(ItemStack stack, int color) {
		CompoundNBT nbt = getOrCreatePouchRootNBT(stack);
		nbt.putInt("Color", color);
		nbt.remove("ColorFaction");
	}

	public static void setPouchDyedByFaction(ItemStack stack, Faction faction) {
		CompoundNBT nbt = getOrCreatePouchRootNBT(stack);
		nbt.putInt("Color", faction.getColor());
		nbt.putString("ColorFaction", faction.getName().toString());
	}

	public static PouchItem.AddItemResult tryAddItemToPouch(ItemStack pouch, ItemStack stack, boolean requireMatchAlreadyInPouch) {
		int stackSizeBefore = stack.getCount();
		if (!stack.isEmpty()) {
			PouchInventory pouchInv = PouchInventory.temporaryWritable(pouch);

			for (int i = 0; i < pouchInv.getContainerSize() && !stack.isEmpty(); ++i) {
				ItemStack itemInSlot = pouchInv.getItem(i);
				if (itemInSlot.isEmpty()) {
					if (requireMatchAlreadyInPouch) {
						continue;
					}
				} else if (itemInSlot.getCount() >= itemInSlot.getMaxStackSize() || itemInSlot.getItem() != stack.getItem() || !itemInSlot.isStackable() || !ItemStack.tagMatches(itemInSlot, stack)) {
					continue;
				}

				if (itemInSlot.isEmpty()) {
					pouchInv.setItem(i, stack.copy());
					stack.setCount(0);
					return PouchItem.AddItemResult.FULLY_ADDED;
				}

				int maxStackSize = Math.min(itemInSlot.getMaxStackSize(), pouchInv.getMaxStackSize());
				int difference = maxStackSize - itemInSlot.getCount();
				if (difference > stack.getCount()) {
					difference = stack.getCount();
				}

				stack.shrink(difference);
				itemInSlot.grow(difference);
				pouchInv.setItem(i, itemInSlot);
				if (stack.isEmpty()) {
					return PouchItem.AddItemResult.FULLY_ADDED;
				}
			}
		}

		return stack.getCount() < stackSizeBefore ? PouchItem.AddItemResult.SOME_ADDED : PouchItem.AddItemResult.NONE_ADDED;
	}

	public enum AddItemResult {
		SOME_ADDED, FULLY_ADDED, NONE_ADDED;
	}
}
