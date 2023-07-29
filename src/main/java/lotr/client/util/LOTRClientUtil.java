package lotr.client.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.LOTRClientProxy;
import lotr.common.LOTRLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

public class LOTRClientUtil {
	private static final AbstractGui GUI_BLIT_PROXY = new AbstractGui() {
	};
	private static final Map averagedPageColors = new HashMap();

	public static void blit(MatrixStack matStack, int x, int y, int u, int v, int width, int height) {
		GUI_BLIT_PROXY.setBlitOffset(0);
		GUI_BLIT_PROXY.blit(matStack, x, y, u, v, width, height);
	}

	public static void blitFloat(AbstractGui gui, MatrixStack matStack, float x0, float y0, float u0, float v0, float w, float h) {
		blitFloat(matStack, x0, y0, gui.getBlitOffset(), u0, v0, w, h);
	}

	public static void blitFloat(MatrixStack matStack, float x0, float y0, float z, float u0, float v0, float w, float h) {
		blitFloat(matStack, x0, y0, z, u0, v0, w, h, 256, 256);
	}

	public static void blitFloat(MatrixStack matStack, float x0, float y0, float z, float u0, float v0, float w, float h, int texW, int texH) {
		innerBlitFloat(matStack, x0, x0 + w, y0, y0 + h, z, w, h, u0, v0, texW, texH);
	}

	public static int computeAverageFactionPageColor(Minecraft mc, ResourceLocation texture, int u0, int v0, int u1, int v1) {
		if (averagedPageColors.containsKey(texture)) {
			return (Integer) averagedPageColors.get(texture);
		}
		int avgColor;
		try {
			BufferedImage pageImage = ImageIO.read(mc.getResourceManager().getResource(texture).getInputStream());
			long totalR = 0L;
			long totalG = 0L;
			long totalB = 0L;
			long totalA = 0L;
			int count = 0;

			int u;
			int v;
			int rgb;
			for (u = u0; u < u1; ++u) {
				for (v = v0; v < v1; ++v) {
					rgb = pageImage.getRGB(u, v);
					Color color = new Color(rgb);
					totalR += color.getRed();
					totalG += color.getGreen();
					totalB += color.getBlue();
					totalA += color.getAlpha();
					++count;
				}
			}

			u = (int) (totalR / count);
			v = (int) (totalG / count);
			rgb = (int) (totalB / count);
			int avgA = (int) (totalA / count);
			avgColor = new Color(u, v, rgb, avgA).getRGB();
		} catch (IOException var21) {
			LOTRLog.error("LOTR: Failed to generate average page colour for %s", texture);
			var21.printStackTrace();
			avgColor = 0;
		}

		averagedPageColors.put(texture, avgColor);
		return avgColor;
	}

	private static void doBlitFloat(Matrix4f matrix, float x0, float x1, float y0, float y1, float z, float u0, float u1, float v0, float v1) {
		BufferBuilder buf = Tessellator.getInstance().getBuilder();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(matrix, x0, y1, z).uv(u0, v1).endVertex();
		buf.vertex(matrix, x1, y1, z).uv(u1, v1).endVertex();
		buf.vertex(matrix, x1, y0, z).uv(u1, v0).endVertex();
		buf.vertex(matrix, x0, y0, z).uv(u0, v0).endVertex();
		buf.end();
		RenderSystem.enableAlphaTest();
		WorldVertexBufferUploader.end(buf);
	}

	public static int doDrawEntityText(FontRenderer fr, IReorderingProcessor rText, int x, int y, int color, boolean dropShadow, Matrix4f matrix, boolean seethrough, int colorBg, int packedLight) {
		Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
		int ret = fr.drawInBatch(rText, x, y, color, dropShadow, matrix, irendertypebuffer$impl, seethrough, colorBg, packedLight);
		irendertypebuffer$impl.endBatch();
		return ret;
	}

	public static boolean doesClientChunkExist(World world, BlockPos pos) {
		return doesClientChunkExist(world, pos.getX(), pos.getZ());
	}

	public static boolean doesClientChunkExist(World world, int x, int z) {
		return world.hasChunk(x >> 4, z >> 4);
	}

	public static int drawSeethroughText(FontRenderer fr, IReorderingProcessor rText, int x, int y, int color, MatrixStack matStack) {
		RenderSystem.enableAlphaTest();
		return doDrawEntityText(fr, rText, x, y, color, false, matStack.last().pose(), true, 0, LOTRClientProxy.MAX_LIGHTMAP);
	}

	public static int drawSeethroughText(FontRenderer fr, ITextComponent text, int x, int y, int color, MatrixStack matStack) {
		return drawSeethroughText(fr, text.getVisualOrderText(), x, y, color, matStack);
	}

	public static int findContrastingColor(int text, int bg) {
		Color cText = new Color(text);
		Color cBg = new Color(bg);
		float[] hsbText = Color.RGBtoHSB(cText.getRed(), cText.getGreen(), cText.getBlue(), (float[]) null);
		float[] hsbBg = Color.RGBtoHSB(cBg.getRed(), cBg.getGreen(), cBg.getBlue(), (float[]) null);
		float bText = hsbText[2];
		float bBg = hsbBg[2];
		float limit = 0.4F;
		if (Math.abs(bText - bBg) < limit) {
			if (bBg > 0.66F) {
				bText = bBg - limit;
			} else {
				bText = bBg + limit;
			}
		}

		return Color.HSBtoRGB(hsbText[0], hsbText[1], bText);
	}

	public static int getAlphaInt(float alphaF) {
		int alphaI = (int) (alphaF * 255.0F);
		return MathHelper.clamp(alphaI, 0, 255);
	}

	public static int getAlphaIntForFontRendering(float alphaF) {
		int alphaI = getAlphaInt(alphaF);
		return Math.max(alphaI, 4);
	}

	public static int getPackedNoOverlay() {
		return OverlayTexture.pack(OverlayTexture.u(0.0F), OverlayTexture.v(false));
	}

	public static int getRGBA(int rgb, float alphaF) {
		return rgb | getAlphaInt(alphaF) << 24;
	}

	public static int getRGBAForFontRendering(int rgb, float alphaF) {
		return rgb | getAlphaIntForFontRendering(alphaF) << 24;
	}

	private static void innerBlitFloat(MatrixStack matStack, float x0, float x1, float y0, float y1, float z, float w, float h, float u0, float v0, int texW, int texH) {
		doBlitFloat(matStack.last().pose(), x0, x1, y0, y1, z, u0 / texW, (u0 + w) / texW, v0 / texH, (v0 + h) / texH);
	}

	public static List trimEachLineToWidth(List lines, FontRenderer fr, int stringWidth) {
		return (List) lines.stream().flatMap(line -> fr.split((ITextProperties) line, stringWidth).stream()).collect(Collectors.toList());
	}
}
