package lotr.client.gui.map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.MapImageTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;

public class MiddleEarthMapRenderer {
	public static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");
	private double mapX;
	private double mapY;
	private double prevMapX;
	private double prevMapY;
	private float zoomExp;
	private float zoomStable;
	private final boolean sepia;
	private final boolean renderFogOfWarIfConfigured;

	public MiddleEarthMapRenderer(boolean sepia, boolean fog) {
		this.sepia = sepia;
		renderFogOfWarIfConfigured = fog;
	}

	public double getMapX() {
		return mapX;
	}

	public double getMapY() {
		return mapY;
	}

	public void moveBy(double dx, double dy) {
		moveTo(mapX + dx, mapY + dy);
	}

	public void moveTo(double x, double y) {
		mapX = x;
		mapY = y;
	}

	public void renderMap(MatrixStack matStack, Screen gui, MiddleEarthMapScreen mapGui, float tick) {
		this.renderMap(matStack, gui, mapGui, tick, 0, 0, gui.width, gui.height);
	}

	public void renderMap(MatrixStack matStack, Screen gui, MiddleEarthMapScreen mapGui, float tick, int x0, int y0, int x1, int y1) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		int seaColor = MapImageTextures.INSTANCE.getMapBackgroundColor(sepia);
		AbstractGui.fill(matStack, x0, y0, x1, y1, seaColor);
		float zoom = (float) Math.pow(2.0D, zoomExp);
		double mapPosX = prevMapX + (mapX - prevMapX) * tick;
		double mapPosY = prevMapY + (mapY - prevMapY) * tick;
		mapGui.setMapViewportAndPositionAndScale(x0, x1, y0, y1, mapPosX, mapPosY, zoom, zoomExp, zoomStable);
		mapGui.enableZoomOutObjectFading = false;
		mapGui.setBlitOffset(gui.getBlitOffset());
		mapGui.renderMapAndOverlay(matStack, tick, sepia, 1.0F, true);
		mapGui.renderRoads(matStack, tick, false);
		if (renderFogOfWarIfConfigured) {
			mapGui.renderFogOfWar(matStack, tick);
		}

		mapGui.renderWaypoints(matStack, 0, 0, 0, tick, false);
	}

	public void renderVignette(MatrixStack matStack, Screen gui, float zLevel) {
		this.renderVignette(matStack, gui, zLevel, 0, 0, gui.width, gui.height);
	}

	public void renderVignette(MatrixStack matStack, Screen gui, float zLevel, int x0, int y0, int x1, int y1) {
		Minecraft.getInstance().getTextureManager().bind(VIGNETTE_TEXTURE);
		float alpha = 1.0F;
		RenderSystem.color4f(alpha, alpha, alpha, 1.0F);
		float u0 = (float) x0 / (float) gui.width;
		float u1 = (float) x1 / (float) gui.width;
		float v0 = (float) y0 / (float) gui.height;
		float v1 = (float) y1 / (float) gui.height;
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(SourceFactor.ONE_MINUS_SRC_ALPHA, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		Matrix4f mat = matStack.last().pose();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(mat, x0, y1, zLevel).uv(u0, v1).endVertex();
		buf.vertex(mat, x1, y1, zLevel).uv(u1, v1).endVertex();
		buf.vertex(mat, x1, y0, zLevel).uv(u1, v0).endVertex();
		buf.vertex(mat, x0, y0, zLevel).uv(u0, v0).endVertex();
		tess.end();
		RenderSystem.defaultBlendFunc();
	}

	public void renderVignettes(MatrixStack matStack, Screen gui, float zLevel, int count) {
		for (int l = 0; l < count; ++l) {
			this.renderVignette(matStack, gui, zLevel);
		}

	}

	public void renderVignettes(MatrixStack matStack, Screen gui, float zLevel, int count, int x0, int y0, int x1, int y1) {
		for (int l = 0; l < count; ++l) {
			this.renderVignette(matStack, gui, zLevel, x0, y0, x1, y1);
		}

	}

	public void setInstantaneousPosition(double x, double y) {
		prevMapX = mapX = x;
		prevMapY = mapY = y;
	}

	public void setStableZoom(float z) {
		zoomStable = z;
	}

	public void setZoomExp(float z) {
		zoomExp = z;
	}

	public void tick() {
		prevMapX = mapX;
		prevMapY = mapY;
	}
}
