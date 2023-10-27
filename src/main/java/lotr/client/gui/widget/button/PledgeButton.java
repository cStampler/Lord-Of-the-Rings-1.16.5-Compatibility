package lotr.client.gui.widget.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.gui.util.AlignmentRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class PledgeButton extends Button {
	private final Screen parentScreen;
	private boolean isBroken;
	private List tooltipLines = new ArrayList();

	public PledgeButton(Screen parent, int xIn, int yIn, boolean broken, IPressable onPressIn) {
		super(xIn, yIn, 32, 32, StringTextComponent.EMPTY, onPressIn);
		parentScreen = parent;
		isBroken = broken;
	}

	@Override
	protected int getYImage(boolean hovered) {
		if (isBroken) {
			return hovered ? 4 : 3;
		}
		if (!active) {
			return 0;
		}
		return hovered ? 2 : 1;
	}

	@Override
	public void renderButton(MatrixStack matStack, int mouseX, int mouseY, float f) {
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.getTextureManager().bind(AlignmentRenderer.ALIGNMENT_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		int yOffset = getYImage(isHovered());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		this.blit(matStack, x, y, 0 + yOffset * width, 180, width, height);
		renderBg(matStack, minecraft, mouseX, mouseY);
		if (!tooltipLines.isEmpty() && isHovered()) {
			parentScreen.renderComponentTooltip(matStack, tooltipLines, mouseX, mouseY);
		}

	}

	public void setDisplayAsBroken(boolean flag) {
		isBroken = flag;
	}

	public void setTooltipLines(ITextComponent... lines) {
		if (lines == null) {
			lines = new ITextComponent[0];
		}

		tooltipLines = Arrays.asList(lines);
	}
}
