package lotr.client.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class CustomWaypointScreen extends BasicIngameScreen {
	public static final int TEXT_WRAP_WIDTH = 300;

	public CustomWaypointScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	protected final void renderPublicButtonTooltip(Button publicButton, MatrixStack matStack, int mouseX, int mouseY) {
		if (publicButton.active && publicButton.isHovered()) {
			List<IReorderingProcessor> lines = new ArrayList<IReorderingProcessor>();
			int stringWidth = 200;
			lines.addAll(font.split(new TranslationTextComponent("gui.lotr.cwp.create.public.help.1"), stringWidth));
			lines.add(IReorderingProcessor.EMPTY);
			lines.addAll(font.split(new TranslationTextComponent("gui.lotr.cwp.create.public.help.2").withStyle(TextFormatting.RED), stringWidth));
			this.renderTooltip(matStack, lines, mouseX, mouseY);
		}

	}
}
