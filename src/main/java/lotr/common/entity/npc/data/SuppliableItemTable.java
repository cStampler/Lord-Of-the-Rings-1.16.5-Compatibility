package lotr.common.entity.npc.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class SuppliableItemTable {
	private final List<Supplier<ItemStack>> itemSupplierList = new ArrayList<>();

	public SuppliableItemTable(Object... items) {
		for (Object item : items) {
			if (item instanceof IItemProvider) {
				itemSupplierList.add(() -> new ItemStack((IItemProvider) item));
			} else if (item instanceof Supplier) {
				itemSupplierList.add(() -> {
					Object supplied = ((Supplier) item).get();
					if (supplied instanceof IItemProvider) {
						return new ItemStack((IItemProvider) supplied);
					}
					if (supplied instanceof ItemStack) {
						return (ItemStack) supplied;
					}
					throw new IllegalArgumentException(String.format("DEVELOPMENT ERROR! Unacceptable Supplier-supplied type %s in %s constructor", supplied, getClass().getName()));
				});
			} else {
				throw new IllegalArgumentException(String.format("DEVELOPMENT ERROR! Unacceptable object type %s in %s constructor", item, getClass().getName()));
			}
		}
	}

	public final ItemStack getRandomItem(Random random) {
		return itemSupplierList.get(random.nextInt(itemSupplierList.size())).get();
	}

	public final ItemStack getRandomItem(Random random, Predicate filter) {
		List<ItemStack> matchingItems = (List<ItemStack>) itemSupplierList.stream().map(hummel -> ((Supplier) hummel).get()).filter(filter).collect(Collectors.toList());
		return matchingItems.get(random.nextInt(matchingItems.size()));
	}
}
