package lotr.client;

import java.awt.Color;
import java.io.*;
import java.util.function.Predicate;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.world.map.MapSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.texture.NativeImage.PixelFormat;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.resource.*;

public class MapImageTextures implements ISelectiveResourceReloadListener {
	private static Minecraft mc = Minecraft.getInstance();
	private static final ResourceLocation MAP_TEXTURE = new ResourceLocation("lotr", "textures/map/loaded_map.png");
	private static final ResourceLocation SEPIA_MAP_TEXTURE = new ResourceLocation("lotr", "textures/map/loaded_map_sepia.png");
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("lotr", "textures/map/overlay.png");
	public static final ResourceLocation MAP_ICONS = new ResourceLocation("lotr", "textures/map/screen.png");
	public static final ResourceLocation MAP_TERRAIN = new ResourceLocation("lotr", "textures/map/terrain.png");
	public static final ResourceLocation FOG_OF_WAR_TEXTURE = new ResourceLocation("lotr", "textures/map/fog_of_war.png");
	public static final ResourceLocation SEPIA_FOG_OF_WAR_TEXTURE = new ResourceLocation("lotr", "textures/map/fog_of_war_sepia.png");
	public static final MapImageTextures INSTANCE = new MapImageTextures();
	public static final ResourceLocation OSRS_ICONS = new ResourceLocation("lotr", "map/osrs.png");
	public static final int OSRS_WATER = 6453158;
	public static final int OSRS_GRASS = 5468426;
	public static final int OSRS_BEACH = 9279778;
	public static final int OSRS_HILL = 6575407;
	public static final int OSRS_MOUNTAIN = 14736861;
	public static final int OSRS_MOUNTAIN_EDGE = 9005125;
	public static final int OSRS_SNOW = 14215139;
	public static final int OSRS_TUNDRA = 9470587;
	public static final int OSRS_SAND = 13548147;
	public static final int OSRS_TREE = 2775058;
	public static final int OSRS_WILD = 3290677;
	public static final int OSRS_PATH = 6575407;
	public static final int OSRS_KINGDOM_COLOR = 16755200;
	private ResourceLocation currentMapImagePath;
	private int backgroundColor;
	private int sepiaBackgroundColor;

	private MapImageTextures() {
		IReloadableResourceManager resMgr = (IReloadableResourceManager) mc.getResourceManager();
		resMgr.registerReloadListener(this);
	}

	private int determineBackgroundColor(NativeImage mapImage) {
		int colorNative = mapImage.getPixelRGBA(0, 0);
		return nativeImageColorToNormal(colorNative);
	}

	public int getMapBackgroundColor(boolean sepia) {
		return sepia ? sepiaBackgroundColor : backgroundColor;
	}

	public void loadMapTexturesIfNew(MapSettings mapSettings) {
		if (currentMapImagePath == null || !mapSettings.getMapImagePath().equals(currentMapImagePath)) {
			try {
				currentMapImagePath = mapSettings.getMapImagePath();
				NativeImage mapImage = readMapImageOrOverride(mapSettings);
				NativeImage sepiaImage = convertToSepia(mapImage);
				backgroundColor = determineBackgroundColor(mapImage);
				sepiaBackgroundColor = determineBackgroundColor(sepiaImage);
				mc.getTextureManager().register(MAP_TEXTURE, new DynamicTexture(mapImage));
				mc.getTextureManager().register(SEPIA_MAP_TEXTURE, new DynamicTexture(sepiaImage));
			} catch (IOException var4) {
				LOTRLog.error("Failed to load map image textures for %s", mapSettings.getMapImagePath());
				var4.printStackTrace();
			}

		}
	}

	@Override
	public void onResourceManagerReload(IResourceManager resMgr, Predicate resPredicate) {
		if (resPredicate.test(VanillaResourceType.TEXTURES)) {
			currentMapImagePath = null;
			backgroundColor = 0;
			sepiaBackgroundColor = 0;
		}

	}

	private InputStream overrideExists(ResourceLocation mapImage) throws IOException {
		ResourceLocation potentialOverride = new ResourceLocation(mapImage.getNamespace(), "textures/mapoverride/" + mapImage.getPath());
		IResourceManager resMgr = mc.getResourceManager();
		return resMgr.hasResource(potentialOverride) ? resMgr.getResource(potentialOverride).getInputStream() : null;
	}

	private NativeImage readMapImageOrOverride(MapSettings mapSettings) throws IOException {
		InputStream overrideStream = overrideExists(mapSettings.getMapImagePath());
		return overrideStream != null ? NativeImage.read(overrideStream) : NativeImage.read(mapSettings.createCachedImageInputStream());
	}

	private static NativeImage convertToSepia(NativeImage srcImage) {
		int mapWidth = srcImage.getWidth();
		int mapHeight = srcImage.getHeight();
		NativeImage newMapImage = new NativeImage(PixelFormat.RGBA, mapWidth, mapHeight, true);

		for (int y = 0; y < mapHeight; ++y) {
			for (int x = 0; x < mapWidth; ++x) {
				int colorNative = srcImage.getPixelRGBA(x, y);
				int colorRGB = nativeImageColorToNormal(colorNative);
				colorRGB = getSepia(colorRGB);
				colorNative = normalColorToNative(colorRGB);
				newMapImage.setPixelRGBA(x, y, colorNative);
			}
		}

		return newMapImage;
	}

	public static void drawMap(MatrixStack matStack, PlayerEntity player, boolean sepia, float x0, float x1, float y0, float y1, int z, float minU, float maxU, float minV, float maxV, float alpha) {
		Matrix4f mat = matStack.last().pose();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		mc.getTextureManager().bind(getMapTexture(sepia));
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(mat, x0, y1, z).uv(minU, maxV).endVertex();
		buf.vertex(mat, x1, y1, z).uv(maxU, maxV).endVertex();
		buf.vertex(mat, x1, y0, z).uv(maxU, minV).endVertex();
		buf.vertex(mat, x0, y0, z).uv(minU, minV).endVertex();
		tess.end();
	}

	public static void drawMap(MatrixStack matStack, PlayerEntity player, float x0, float x1, float y0, float y1, int z, float minU, float maxU, float minV, float maxV) {
		boolean sepia = (Boolean) LOTRConfig.CLIENT.sepiaMap.get();
		drawMap(matStack, player, sepia, x0, x1, y0, y1, z, minU, maxU, minV, maxV, 1.0F);
	}

	public static void drawMapCompassBottomLeft(MatrixStack matStack, float x, float y, float z, float scale) {
		mc.getTextureManager().bind(MAP_ICONS);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Matrix4f mat = matStack.last().pose();
		int width = 32;
		int height = 32;
		float x1 = x + width * scale;
		float y0 = y - height * scale;
		int texU = 224;
		int texV = 200;
		float u0 = texU / 256.0F;
		float u1 = (texU + width) / 256.0F;
		float v0 = texV / 256.0F;
		float v1 = (texV + height) / 256.0F;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(mat, x, y, z).uv(u0, v1).endVertex();
		buf.vertex(mat, x1, y, z).uv(u1, v1).endVertex();
		buf.vertex(mat, x1, y0, z).uv(u1, v0).endVertex();
		buf.vertex(mat, x, y0, z).uv(u0, v0).endVertex();
		tess.end();
	}

	public static void drawMapOverlay(MatrixStack matStack, PlayerEntity entityplayer, float x0, float x1, float y0, float y1, float z, float minU, float maxU, float minV, float maxV) {
		Matrix4f mat = matStack.last().pose();
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		mc.getTextureManager().bind(OVERLAY_TEXTURE);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.2F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(mat, x0, y1, z).uv(0.0F, 1.0F).endVertex();
		buf.vertex(mat, x1, y1, z).uv(1.0F, 1.0F).endVertex();
		buf.vertex(mat, x1, y0, z).uv(1.0F, 0.0F).endVertex();
		buf.vertex(mat, x0, y0, z).uv(0.0F, 0.0F).endVertex();
		tess.end();
	}

	private static ResourceLocation getMapTexture(boolean sepia) {
		return sepia ? SEPIA_MAP_TEXTURE : MAP_TEXTURE;
	}

	private static int getSepia(int rgb) {
		Color color = new Color(rgb);
		int alpha = rgb >> 24 & 255;
		float[] colors = color.getColorComponents((float[]) null);
		float r = colors[0];
		float g = colors[1];
		float b = colors[2];
		float newR = r * 0.79F + g * 0.39F + b * 0.26F;
		float newG = r * 0.52F + g * 0.35F + b * 0.19F;
		float newB = r * 0.35F + g * 0.26F + b * 0.15F;
		newR = MathHelper.clamp(newR, 0.0F, 1.0F);
		newG = MathHelper.clamp(newG, 0.0F, 1.0F);
		newB = MathHelper.clamp(newB, 0.0F, 1.0F);
		int sepia = new Color(newR, newG, newB).getRGB();
		sepia |= alpha << 24;
		return sepia;
	}

	private static int nativeImageColorToNormal(int abgr) {
		int a = abgr >> 24 & 255;
		int b = abgr >> 16 & 255;
		int g = abgr >> 8 & 255;
		int r = abgr & 255;
		return a << 24 | r << 16 | g << 8 | b;
	}

	private static int normalColorToNative(int argb) {
		int a = argb >> 24 & 255;
		int r = argb >> 16 & 255;
		int g = argb >> 8 & 255;
		int b = argb & 255;
		return a << 24 | b << 16 | g << 8 | r;
	}
}
