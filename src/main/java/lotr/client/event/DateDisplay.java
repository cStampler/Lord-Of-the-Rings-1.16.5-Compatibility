package lotr.client.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.util.LOTRClientUtil;
import lotr.common.time.ShireReckoning;
import lotr.common.util.LOTRUtil;
import net.minecraft.client.*;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;

public class DateDisplay {
	private int dateDisplayTime = 0;

	private float calculateDisplayAlpha() {
		return LOTRUtil.normalisedTriangleWave(200 - dateDisplayTime, 200.0F, 0.0F, 1.0F);
	}

	public void displayNewDate() {
		dateDisplayTime = 200;
	}

	public void render(MatrixStack matStack, Minecraft mc) {
		if (dateDisplayTime > 0) {
			ITextComponent date = ShireReckoning.INSTANCE.getCurrentDateAndYearLongform();
			float alpha = calculateDisplayAlpha();
			MainWindow window = mc.getWindow();
			int width = window.getGuiScaledWidth();
			int height = window.getGuiScaledHeight();
			float scale = 1.5F;
			float invScale = 1.0F / scale;
			width = (int) (width * invScale);
			height = (int) (height * invScale);
			FontRenderer font = mc.font;
			int dateX = (width - font.width(date)) / 2;
			font.getClass();
			int dateY = (height - 9) * 2 / 5;
			matStack.pushPose();
			matStack.scale(scale, scale, scale);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			font.draw(matStack, date, dateX, dateY, LOTRClientUtil.getRGBAForFontRendering(16777215, alpha));
			RenderSystem.disableBlend();
			matStack.popPose();
		}

	}

	public void reset() {
		dateDisplayTime = 0;
	}

	public void update() {
		if (dateDisplayTime > 0) {
			--dateDisplayTime;
		}

	}
}
