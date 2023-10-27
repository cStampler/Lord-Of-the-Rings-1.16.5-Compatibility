package lotr.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

public class CustomColoredNameItem extends Item {
	private final int nameColor;

	public CustomColoredNameItem(Properties properties, int nameColor) {
		super(properties);
		this.nameColor = nameColor;
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		IFormattableTextComponent name = new TranslationTextComponent(this.getDescriptionId(stack));
		name.withStyle(Style.EMPTY.withColor(Color.fromRgb(nameColor)));
		return name;
	}
}
