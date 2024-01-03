package lotr.common.init;

import java.util.Comparator;
import java.util.function.Supplier;

import lotr.common.item.LOTRSpawnEggItem;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LOTRItemGroups {
	public static final ItemGroup BLOCKS = new LOTRItemGroups.LOTRItemGroup("blocks", () -> LOTRBlocks.GONDOR_BRICK.get());
	public static final ItemGroup UTIL = new LOTRItemGroups.LOTRItemGroup("util", () -> LOTRBlocks.DWARVEN_CRAFTING_TABLE.get());
	public static final ItemGroup DECO = new LOTRItemGroups.LOTRItemGroup("decorations", () -> LOTRBlocks.SIMBELMYNE.get());
	public static final ItemGroup MATERIALS = new LOTRItemGroups.LOTRItemGroup("materials", () -> (IItemProvider)LOTRItems.MITHRIL_INGOT.get());
	public static final ItemGroup MISC = new LOTRItemGroups.LOTRItemGroup("misc", () ->(IItemProvider) LOTRItems.GOLD_RING.get());
	public static final ItemGroup FOOD = new LOTRItemGroups.LOTRItemGroup("food", () -> (IItemProvider)LOTRItems.LEMBAS.get());
	public static final ItemGroup TOOLS = new LOTRItemGroups.LOTRItemGroup("tools", () -> (IItemProvider)LOTRItems.DWARVEN_PICKAXE.get());
	public static final ItemGroup COMBAT = new LOTRItemGroups.LOTRItemGroup("combat", () -> (IItemProvider)LOTRItems.GONDOR_SWORD.get());
	public static final ItemGroup STORY = new LOTRItemGroups.LOTRItemGroup("story", () -> (IItemProvider)LOTRItems.RED_BOOK.get());
	public static final ItemGroup SPAWNERS = new LOTRItemGroups.LOTRItemGroup("spawners", () -> LOTRSpawnEggItem.getModSpawnEgg((EntityType<?>) LOTREntities.HOBBIT.get()));

	public static class LOTRItemGroup extends ItemGroup {
		private final Supplier<? extends IItemProvider> iconSup;

		public LOTRItemGroup(String s, Supplier<? extends IItemProvider> itemSup) {
			super("lotr." + s);
			iconSup = itemSup;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void fillItemList(NonNullList<ItemStack> items) {
			super.fillItemList(items);
			items.sort(Comparator.comparing(LOTRItems::getCreativeTabOrderForItem));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack((IItemProvider) iconSup.get());
		}
	}
}
