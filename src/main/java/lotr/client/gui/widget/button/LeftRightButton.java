package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.util.LOTRClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class LeftRightButton extends Button {
	private static final ResourceLocation LEFT_RIGHT_BUTTON_TEXTURE = new ResourceLocation("lotr", "textures/gui/widgets.png");
	private final boolean isLeftHanded;

	public LeftRightButton(int xIn, int yIn, int widthIn, int heightIn, boolean left, ITextComponent text, IPressable onPress) {
		super(xIn, yIn, widthIn, heightIn, text, onPress);
		isLeftHanded = left;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft mc = Minecraft.getInstance();
		FontRenderer font = mc.font;
		mc.getTextureManager().bind(LEFT_RIGHT_BUTTON_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int yOffset = getYImage(isHovered());
		int u0 = isLeftHanded ? 0 : 136;
		int v0 = 0 + yOffset * 20;
		this.blit(matStack, x, y, u0, v0, width / 2, height);
		this.blit(matStack, x + width / 2, y, u0 + 120 - width / 2, v0, width / 2, height);
		renderBg(matStack, mc, mouseX, mouseY);
		int textColor = getFGColor();
		int textX = isLeftHanded ? x + 16 + (width - 16) / 2 : x + (width - 16) / 2;
		drawCenteredString(matStack, font, getMessage(), textX, y + (height - 8) / 2, LOTRClientUtil.getRGBAForFontRendering(textColor, alpha));
	}
}
