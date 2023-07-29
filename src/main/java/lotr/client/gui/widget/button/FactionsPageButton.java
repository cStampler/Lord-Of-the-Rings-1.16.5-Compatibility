package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.MiddleEarthFactionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;

public class FactionsPageButton extends Button {
	private final boolean isLeftHanded;

	public FactionsPageButton(int xIn, int yIn, boolean left, ITextComponent text, IPressable onPress) {
		super(xIn, yIn, 16, 16, text, onPress);
		isLeftHanded = left;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft mc = Minecraft.getInstance();
		FontRenderer font = mc.font;
		mc.getTextureManager().bind(MiddleEarthFactionsScreen.FACTIONS_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int yOffset = getYImage(isHovered());
		this.blit(matStack, x, y, 0 + yOffset * 16, isLeftHanded ? 160 : 176, width, height);
		renderBg(matStack, mc, mouseX, mouseY);
		if (active) {
			int stringBorder = -1;
			int var10000 = y + height / 2;
			font.getClass();
			int stringY = var10000 - 9 / 2;
			int textColor = 0;
			ITextComponent msg = getMessage();
			if (isLeftHanded) {
				font.draw(matStack, msg, x + width + stringBorder, stringY, textColor);
			} else {
				font.draw(matStack, msg, x - stringBorder - font.width(msg), stringY, textColor);
			}
		}

	}
}
