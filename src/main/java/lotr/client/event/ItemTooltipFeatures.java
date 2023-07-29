package lotr.client.event;

import java.util.*;

import lotr.common.item.ItemOwnership;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ItemTooltipFeatures {
	private static final Minecraft MC = Minecraft.getInstance();

	private static void addItemOwnership(ItemStack itemstack, List tooltip) {
		ITextComponent currentOwner = ItemOwnership.getCurrentOwner(itemstack);
		if (currentOwner != null) {
			tooltip.add(StringTextComponent.EMPTY);
			ITextComponent ownerText = new TranslationTextComponent("item.lotr.generic.currentOwner", currentOwner).withStyle(TextFormatting.GRAY);
			tooltip.add(ownerText);
		}

		List previousOwners = ItemOwnership.getPreviousOwners(itemstack);
		if (!previousOwners.isEmpty()) {
			tooltip.add(StringTextComponent.EMPTY);
			List ownerLines = new ArrayList();
			IFormattableTextComponent beginList;
			if (previousOwners.size() == 1) {
				beginList = new TranslationTextComponent("item.lotr.generic.previousOwner", previousOwners.get(0)).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
				ownerLines.add(beginList);
			} else {
				beginList = new TranslationTextComponent("item.lotr.generic.previousOwnerList").withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
				ownerLines.add(beginList);
				Iterator var7 = previousOwners.iterator();

				while (var7.hasNext()) {
					ITextComponent previousOwner = (ITextComponent) var7.next();
					ITextComponent previousOwnerText = new TranslationTextComponent("%s", previousOwner).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
					ownerLines.add(previousOwnerText);
				}
			}

			tooltip.addAll(ownerLines);
		}

	}

	public static void handleTooltipEvent(ItemTooltipEvent event) {
		ItemStack itemstack = event.getItemStack();
		List tooltip = event.getToolTip();
		event.getPlayer();
		addItemOwnership(itemstack, tooltip);
	}
}
