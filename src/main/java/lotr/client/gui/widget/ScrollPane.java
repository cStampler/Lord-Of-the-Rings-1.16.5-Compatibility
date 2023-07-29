package lotr.client.gui.widget;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;

public class ScrollPane {
	private final int scrollWidgetWidth;
	private final int scrollWidgetHeight;
	private int barColor;
	private int widgetColor;
	public int scrollBarX0;
	public int paneX0;
	public int paneY0;
	public int paneY1;
	public boolean hasScrollBar;
	public float currentScroll;
	public boolean isScrolling;
	public boolean mouseOver;
	private boolean wasMouseDown;

	public ScrollPane(int ww, int wh) {
		scrollWidgetWidth = ww;
		scrollWidgetHeight = wh;
		barColor = -1711276033;
		widgetColor = -1426063361;
	}

	public void drawScrollBar(MatrixStack matStack) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x0 = scrollBarX0 + scrollWidgetWidth / 2;
		int y0 = paneY0;
		int y1 = paneY1;
		AbstractGui.fill(matStack, x0, y0, x0 + 1, y1, barColor);
		int scroll = (int) (currentScroll * (y1 - y0 - scrollWidgetHeight));
		x0 = scrollBarX0;
		int x1 = x0 + scrollWidgetWidth;
		y0 += scroll;
		y1 = y0 + scrollWidgetHeight;
		AbstractGui.fill(matStack, x0, y0, x1, y1, widgetColor);
	}

	public int[] getMinMaxIndices(List list, int displayed) {
		int size = list.size();
		int min = 0 + Math.round(currentScroll * (size - displayed));
		int max = displayed - 1 + Math.round(currentScroll * (size - displayed));
		min = Math.max(min, 0);
		max = Math.min(max, size - 1);
		return new int[] { min, max };
	}

	public void mouseDragScroll(int mouseX, int mouseY, boolean isMouseDown) {
		if (!hasScrollBar) {
			resetScroll();
		} else {
			int x0 = paneX0;
			int x1 = scrollBarX0 + scrollWidgetWidth;
			int y0 = paneY0;
			int y1 = paneY1;
			mouseOver = mouseX >= x0 && mouseY >= y0 && mouseX < x1 && mouseY < y1;
			x0 = scrollBarX0;
			boolean mouseOverScroll = mouseX >= x0 && mouseY >= y0 && mouseX < x1 && mouseY < y1;
			if (!wasMouseDown && isMouseDown && mouseOverScroll) {
				isScrolling = true;
			}

			if (!isMouseDown) {
				isScrolling = false;
			}

			if (isScrolling) {
				currentScroll = (mouseY - y0 - scrollWidgetHeight / 2.0F) / ((float) (y1 - y0) - (float) scrollWidgetHeight);
				currentScroll = MathHelper.clamp(currentScroll, 0.0F, 1.0F);
			}
		}

		wasMouseDown = isMouseDown;
	}

	public void mouseWheelScroll(double delta, int size) {
		currentScroll -= (float) delta / size;
		currentScroll = MathHelper.clamp(currentScroll, 0.0F, 1.0F);
	}

	public void resetScroll() {
		currentScroll = 0.0F;
		isScrolling = false;
	}

	public ScrollPane setColors(int c1, int c2) {
		int alphaMask = -16777216;
		if ((c1 & alphaMask) == 0) {
			c1 |= alphaMask;
		}

		if ((c2 & alphaMask) == 0) {
			c2 |= alphaMask;
		}

		barColor = c1;
		widgetColor = c2;
		return this;
	}
}
