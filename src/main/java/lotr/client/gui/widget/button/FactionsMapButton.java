package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.MiddleEarthFactionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class FactionsMapButton extends Button {
	public FactionsMapButton(int xIn, int yIn, IPressable onPress) {
		super(xIn, yIn, 8, 8, StringTextComponent.EMPTY, onPress);
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bind(MiddleEarthFactionsScreen.FACTIONS_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int yOffset = isHovered() ? 1 : 0;
		this.blit(matStack, x, y, 17 + yOffset * 8, 142, width, height);
		renderBg(matStack, mc, mouseX, mouseY);
	}
}
