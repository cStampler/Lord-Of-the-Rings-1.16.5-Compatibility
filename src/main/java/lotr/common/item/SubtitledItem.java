package lotr.common.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;

public class SubtitledItem extends Item {
	public SubtitledItem(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List tooltip, ITooltipFlag flag) {
		tooltip.add(new TranslationTextComponent(this.getDescriptionId() + ".subtitle").withStyle(TextFormatting.GRAY));
	}
}
