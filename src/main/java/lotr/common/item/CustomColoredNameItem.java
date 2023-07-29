package lotr.common.item;

import net.minecraft.item.*;
import net.minecraft.util.text.*;

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
