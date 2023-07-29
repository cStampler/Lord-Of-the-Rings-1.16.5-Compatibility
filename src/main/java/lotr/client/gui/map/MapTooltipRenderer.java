package lotr.client.gui.map;

import com.mojang.blaze3d.matrix.MatrixStack;

import lotr.client.util.LOTRClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.math.MathHelper;

public abstract class MapTooltipRenderer<T> {
	protected MiddleEarthMapScreen mapScreen;
	protected Minecraft mc;
	protected FontRenderer font;
	protected int mapXMin;
	protected int mapXMax;
	protected int mapYMin;
	protected int mapYMax;
	protected float selectionProgress;

	public boolean charTyped(char c, int modifiers) {
		return false;
	}

	protected float getExpandingTextAlpha() {
		return MathHelper.clamp((this.selectionProgress - 0.5F) * 2.0F, 0.0F, 1.0F);
	}

	protected float getSelectionExpandProgress() {
		return MathHelper.clamp(this.selectionProgress * 2.0F, 0.0F, 1.0F);
	}

	protected int getTextColor(boolean highlighted, float alpha) {
		return LOTRClientUtil.getRGBAForFontRendering(highlighted ? 16777215 : 12632256, alpha);
	}

	public void init(MiddleEarthMapScreen mapScreen, Minecraft mc, FontRenderer font) {
		this.mapScreen = mapScreen;
		this.mc = mc;
		this.font = font;
	}

	public boolean isTextFieldFocused() {
		return false;
	}

	public boolean keyPressed(int key, int scan, int param3) {
		return false;
	}

	public boolean mouseClicked(double x, double y, int code) {
		return false;
	}

	public void onSelect(Object mapObject) {
	}

	public abstract void render(MatrixStack var1, T var2, boolean var3, int var4, int var5, float var6);

	public void setMapDimensions(int xMin, int xMax, int yMin, int yMax) {
		this.mapXMin = xMin;
		this.mapXMax = xMax;
		this.mapYMin = yMin;
		this.mapYMax = yMax;
	}

	public void setSelectionProgress(int prevTickdown, int currentTickdown, int time, float f) {
		float tickdownF = prevTickdown + (currentTickdown - prevTickdown) * f;
		this.selectionProgress = (time - tickdownF) / time;
	}

	public void tick() {
	}
}
