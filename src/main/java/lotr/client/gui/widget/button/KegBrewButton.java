package lotr.client.gui.widget.button;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.inv.KegScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class KegBrewButton extends Button {
	public KegBrewButton(int xIn, int yIn, IPressable onPressIn, ITooltip tooltip) {
		super(xIn, yIn, 42, 14, StringTextComponent.EMPTY, onPressIn, tooltip);
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(KegScreen.KEG_SCREEN);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		int yOffset = getYImage(isHovered());
		this.blit(matStack, x, y, 210, 0 + yOffset * height, width, height);
		renderBg(matStack, minecraft, mouseX, mouseY);
		if (isHovered()) {
			renderToolTip(matStack, mouseX, mouseY);
		}

	}
}
