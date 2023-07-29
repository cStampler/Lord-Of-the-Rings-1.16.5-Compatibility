package lotr.client.gui.util;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.util.LOTRClientUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public class AlignmentTextRenderer {
	private final boolean isInWorldRendering;
	private boolean defaultSeethrough = false;

	private AlignmentTextRenderer(boolean inWorld) {
		isInWorldRendering = inWorld;
	}

	public void drawAlignmentText(MatrixStack matStack, FontRenderer fr, int x, int y, ITextComponent text, float alphaF) {
		this.drawAlignmentText(matStack, fr, x, y, text, alphaF, defaultSeethrough);
	}

	public void drawAlignmentText(MatrixStack matStack, FontRenderer fr, int x, int y, ITextComponent text, float alphaF, boolean seethrough) {
		this.drawBorderedText(matStack, fr, x, y, text, 16772620, alphaF, seethrough);
	}

	public void drawBorderedText(MatrixStack matStack, FontRenderer fr, int x, int y, ITextComponent text, int color, float alphaF) {
		this.drawBorderedText(matStack, fr, x, y, text, color, alphaF, defaultSeethrough);
	}

	public void drawBorderedText(MatrixStack matStack, FontRenderer fr, int x, int y, ITextComponent text, int color, float alphaF, boolean seethrough) {
		int colorWithAlpha = LOTRClientUtil.getRGBAForFontRendering(color, alphaF);
		int blackWithAlpha = LOTRClientUtil.getRGBAForFontRendering(0, alphaF);
		if (isInWorldRendering) {
			matStack.pushPose();
			matStack.translate(0.0D, 0.0D, 0.001D);
		}

		LOTRClientUtil.drawSeethroughText(fr, text, x - 1, y - 1, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x, y - 1, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x + 1, y - 1, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x + 1, y, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x + 1, y + 1, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x, y + 1, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x - 1, y + 1, blackWithAlpha, matStack);
		LOTRClientUtil.drawSeethroughText(fr, text, x - 1, y, blackWithAlpha, matStack);
		if (isInWorldRendering) {
			matStack.popPose();
		}

		LOTRClientUtil.drawSeethroughText(fr, text, x, y, colorWithAlpha, matStack);
	}

	public void drawConquestText(MatrixStack matStack, FontRenderer fr, int x, int y, ITextComponent text, boolean isConquestCleanse, float alphaF) {
		this.drawConquestText(matStack, fr, x, y, text, isConquestCleanse, alphaF, defaultSeethrough);
	}

	public void drawConquestText(MatrixStack matStack, FontRenderer fr, int x, int y, ITextComponent text, boolean isConquestCleanse, float alphaF, boolean seethrough) {
		this.drawBorderedText(matStack, fr, x, y, text, isConquestCleanse ? 16773846 : 14833677, alphaF, seethrough);
	}

	public AlignmentTextRenderer setDefaultSeethrough(boolean flag) {
		defaultSeethrough = flag;
		return this;
	}

	public static AlignmentTextRenderer newGUIRenderer() {
		return new AlignmentTextRenderer(false);
	}

	public static AlignmentTextRenderer newInWorldRenderer() {
		return new AlignmentTextRenderer(true);
	}
}
