package lotr.common.init;

import java.util.Comparator;
import java.util.function.Supplier;

import lotr.common.item.LOTRSpawnEggItem;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.api.distmarker.*;

public class LOTRItemGroups {
	public static final ItemGroup BLOCKS = new LOTRItemGroups.LOTRItemGroup("blocks", () -> LOTRBlocks.GONDOR_BRICK.get());
	public static final ItemGroup UTIL = new LOTRItemGroups.LOTRItemGroup("util", () -> LOTRBlocks.DWARVEN_CRAFTING_TABLE.get());
	public static final ItemGroup DECO = new LOTRItemGroups.LOTRItemGroup("decorations", () -> LOTRBlocks.SIMBELMYNE.get());
	public static final ItemGroup MATERIALS = new LOTRItemGroups.LOTRItemGroup("materials", () -> LOTRItems.MITHRIL_INGOT.get());
	public static final ItemGroup MISC = new LOTRItemGroups.LOTRItemGroup("misc", () -> LOTRItems.GOLD_RING.get());
	public static final ItemGroup FOOD = new LOTRItemGroups.LOTRItemGroup("food", () -> LOTRItems.LEMBAS.get());
	public static final ItemGroup TOOLS = new LOTRItemGroups.LOTRItemGroup("tools", () -> LOTRItems.DWARVEN_PICKAXE.get());
	public static final ItemGroup COMBAT = new LOTRItemGroups.LOTRItemGroup("combat", () -> LOTRItems.GONDOR_SWORD.get());
	public static final ItemGroup STORY = new LOTRItemGroups.LOTRItemGroup("story", () -> LOTRItems.RED_BOOK.get());
	public static final ItemGroup SPAWNERS = new LOTRItemGroups.LOTRItemGroup("spawners", () -> LOTRSpawnEggItem.getModSpawnEgg((EntityType) LOTREntities.HOBBIT.get()));

	public static class LOTRItemGroup extends ItemGroup {
		private final Supplier iconSup;

		public LOTRItemGroup(String s, Supplier itemSup) {
			super("lotr." + s);
			iconSup = itemSup;
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public void fillItemList(NonNullList items) {
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
