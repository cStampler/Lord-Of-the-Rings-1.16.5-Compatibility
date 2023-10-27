package lotr.client.gui;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.util.LOTRClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public abstract class BasicIngameScreen extends Screen {
	public BasicIngameScreen(ITextComponent titleIn) {
		super(titleIn);
	}

	protected void addTextField(TextFieldWidget textField) {
		children.add(textField);
	}

	protected void addTextFieldAndSetFocused(TextFieldWidget textField) {
		addTextField(textField);
		textField.setFocus(true);
		setInitialFocus(textField);
	}

	protected int drawCenteredStringLinesWrappedToWidth(MatrixStack matStack, FontRenderer fr, ITextProperties text, int wrapWidth, int x, int y, int color) {
		List<IReorderingProcessor> loreLines = font.split(text, wrapWidth);

		for (Iterator<IReorderingProcessor> var9 = loreLines.iterator(); var9.hasNext(); y += 9) {
			IReorderingProcessor line = (IReorderingProcessor) var9.next();
			font.drawShadow(matStack, line, x - font.width(line) / 2, y, color);
			font.getClass();
		}

		return y;
	}

	public void drawCenteredStringNoShadow(MatrixStack matStack, FontRenderer fr, IReorderingProcessor text, int x, int y, int color) {
		fr.draw(matStack, text, x - fr.width(text) / 2, y, color);
	}

	public void drawCenteredStringNoShadow(MatrixStack matStack, FontRenderer fr, ITextComponent text, int x, int y, int color) {
		fr.draw(matStack, text, x - fr.width(text) / 2, y, color);
	}

	public void drawCenteredStringNoShadow(MatrixStack matStack, FontRenderer fr, String text, int x, int y, int color) {
		fr.draw(matStack, text, x - fr.width(text) / 2, y, color);
	}

	protected boolean isEscapeOrInventoryKey(int key, int scan) {
		return key == 256 || minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(key, scan));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scan, int param3) {
		if (super.keyPressed(key, scan, param3)) {
			return true;
		}
		IGuiEventListener focused = getFocused();
		if (focused instanceof TextFieldWidget) {
			TextFieldWidget focusedTextField = (TextFieldWidget) focused;
			if (focusedTextField.canConsumeInput()) {
				return true;
			}
		}

		if (shouldCloseOnEsc() && isEscapeOrInventoryKey(key, scan)) {
			minecraft.player.closeContainer();
			onClose();
			return true;
		}
		return false;
	}

	protected void removeButton(Widget widget) {
		buttons.remove(widget);
		children.remove(widget);
	}

	protected void renderButtonTooltipIfHovered(MatrixStack matStack, Button button, ITextProperties tooltip, int mouseX, int mouseY) {
		this.renderButtonTooltipIfHovered(matStack, button, ImmutableList.of(tooltip), mouseX, mouseY);
	}

	protected void renderButtonTooltipIfHovered(MatrixStack matStack, Button button, List<ITextProperties> tooltipLines, int mouseX, int mouseY) {
		if (button.active && button.isHovered()) {
			int stringWidth = 200;
			this.renderTooltip(matStack, LOTRClientUtil.trimEachLineToWidth(tooltipLines, font, stringWidth), mouseX, mouseY);
		}

	}

	@Override
	public void tick() {
		super.tick();
		if (!minecraft.player.isAlive() || minecraft.player.removed) {
			minecraft.player.closeContainer();
			onClose();
		}

	}

	public static void playButtonClick() {
		Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}
}
