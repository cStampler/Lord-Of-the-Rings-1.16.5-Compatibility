package lotr.common.item;

import java.util.*;
import java.util.stream.Collectors;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;

public class ItemOwnership {
	public static void addPreviousOwner(ItemStack itemstack, ITextComponent name) {
		List previousOwners = getPreviousOwners(itemstack);
		List lastPreviousOwners = previousOwners;
		previousOwners.add(name);
		previousOwners.addAll(lastPreviousOwners);

		while (previousOwners.size() > 3) {
			previousOwners.remove(previousOwners.size() - 1);
		}

		ListNBT tagList = new ListNBT();
		tagList.addAll((Collection) previousOwners.stream().map(hummel -> Serializer.toJson((ITextComponent) hummel)).map(hummel -> StringNBT.valueOf((String) hummel)).collect(Collectors.toList()));
		CompoundNBT nbt = itemstack.getOrCreateTagElement("LOTROwnership");
		nbt.put("PreviousOwners", tagList);
	}

	public static ITextComponent getCurrentOwner(ItemStack itemstack) {
		CompoundNBT nbt = itemstack.getTagElement("LOTROwnership");
		if (nbt != null && nbt.contains("CurrentOwner", 8)) {
			String ownerJson = nbt.getString("CurrentOwner");
			return Serializer.fromJsonLenient(ownerJson);
		}
		return null;
	}

	public static List getPreviousOwners(ItemStack itemstack) {
		List owners = new ArrayList();
		CompoundNBT nbt = itemstack.getTagElement("LOTROwnership");
		if (nbt != null && nbt.contains("PreviousOwners", 9)) {
			ListNBT tagList = nbt.getList("PreviousOwners", 8);

			for (int i = 0; i < tagList.size(); ++i) {
				String ownerJson = tagList.getString(i);
				owners.add(Serializer.fromJsonLenient(ownerJson));
			}
		}

		return owners;
	}

	public static void setCurrentOwner(ItemStack itemstack, ITextComponent name) {
		ITextComponent previousCurrentOwner = getCurrentOwner(itemstack);
		if (previousCurrentOwner != null) {
			addPreviousOwner(itemstack, previousCurrentOwner);
		}

		itemstack.getOrCreateTagElement("LOTROwnership").putString("CurrentOwner", Serializer.toJson(name));
	}
}
