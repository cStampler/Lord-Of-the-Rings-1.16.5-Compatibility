package lotr.common.item;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class FishAndChipsItem extends HeavyFoodSubtitledItem {
	private static final Random RAND = new Random();

	public FishAndChipsItem(Properties properties) {
		super(properties);
	}

	@Override
	public ITextComponent getName(ItemStack stack) {
		ITextComponent name = super.getName(stack);
		if ("Fish and Chips".equalsIgnoreCase(name.getString())) {
			long l = System.currentTimeMillis() / 10000L;
			RAND.setSeed(709247283937L * (l + 31L) + 17L);
			if (RAND.nextInt(360) == 0) {
				return new StringTextComponent("Chips and Fish").withStyle(name.getStyle());
			}
		}

		return name;
	}
}
