package lotr.client.event;

import java.util.ArrayList;
import java.util.List;

import lotr.common.item.ItemOwnership;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class ItemTooltipFeatures {
	private static final Minecraft MC = Minecraft.getInstance();

	private static void addItemOwnership(ItemStack itemstack, List<ITextComponent> tooltip) {
	    ITextComponent currentOwner = ItemOwnership.getCurrentOwner(itemstack);
	    if (currentOwner != null) {
	      tooltip.add(StringTextComponent.EMPTY);
	      IFormattableTextComponent iFormattableTextComponent = (new TranslationTextComponent("item.lotr.generic.currentOwner", new Object[] { currentOwner })).withStyle(TextFormatting.GRAY);
	      tooltip.add(iFormattableTextComponent);
	    } 
	    List<ITextComponent> previousOwners = ItemOwnership.getPreviousOwners(itemstack);
	    if (!previousOwners.isEmpty()) {
	      tooltip.add(StringTextComponent.EMPTY);
	      List<ITextComponent> ownerLines = new ArrayList<>();
	      if (previousOwners.size() == 1) {
	        IFormattableTextComponent iFormattableTextComponent = (new TranslationTextComponent("item.lotr.generic.previousOwner", new Object[] { previousOwners.get(0) })).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
	        ownerLines.add(iFormattableTextComponent);
	      } else {
	        IFormattableTextComponent iFormattableTextComponent = (new TranslationTextComponent("item.lotr.generic.previousOwnerList")).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
	        ownerLines.add(iFormattableTextComponent);
	        for (ITextComponent previousOwner : previousOwners) {
	          IFormattableTextComponent iFormattableTextComponent1 = (new TranslationTextComponent("%s", new Object[] { previousOwner })).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
	          ownerLines.add(iFormattableTextComponent1);
	        } 
	      } 
	      tooltip.addAll(ownerLines);
	    } 
	  }

	public static void handleTooltipEvent(ItemTooltipEvent event) {
		ItemStack itemstack = event.getItemStack();
		List<ITextComponent> tooltip = event.getToolTip();
		event.getPlayer();
		addItemOwnership(itemstack, tooltip);
	}
}
