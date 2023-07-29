package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.RedBookScreen;
import lotr.client.util.LOTRClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class RedBookButton extends Button {
	private boolean hasRedText = false;

	public RedBookButton(int xIn, int yIn, int widthIn, int heightIn, ITextComponent textIn, IPressable onPressIn) {
		super(xIn, yIn, widthIn, heightIn, textIn, onPressIn);
	}

	@Override
	public int getFGColor() {
		if (packedFGColor != -1) {
			return packedFGColor;
		}
		return active ? hasRedText ? 16711680 : 8019267 : 5521198;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft minecraft = Minecraft.getInstance();
		FontRenderer fr = minecraft.font;
		minecraft.getTextureManager().bind(RedBookScreen.BOOK_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int yOffset = getYImage(isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		blit(matStack, x, y, 170.0F, 256 + yOffset * 20, width / 2, height, 512, 512);
		blit(matStack, x + width / 2, y, 370 - width / 2, 256 + yOffset * 20, width / 2, height, 512, 512);
		renderBg(matStack, minecraft, mouseX, mouseY);
		int textColor = getFGColor();
		fr.draw(matStack, getMessage(), x + width / 2 - fr.width(getMessage()) / 2, y + (height - 8) / 2, LOTRClientUtil.getRGBAForFontRendering(textColor, alpha));
	}

	public RedBookButton setRedText() {
		hasRedText = true;
		return this;
	}
}
