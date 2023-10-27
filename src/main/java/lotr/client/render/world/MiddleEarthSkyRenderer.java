package lotr.client.render.world;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.render.RandomTextureVariants;
import lotr.common.dim.LOTRDimensionType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo.FogType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.ISkyRenderHandler;

public class MiddleEarthSkyRenderer implements ISkyRenderHandler {
	private static final ResourceLocation SUN_TEXTURE = new ResourceLocation("lotr", "textures/sky/sun.png");
	private static final ResourceLocation MOON_TEXTURE = new ResourceLocation("lotr", "textures/sky/moon.png");
	private static final ResourceLocation EARENDIL_TEXTURE = new ResourceLocation("lotr", "textures/sky/earendil.png");
	private RandomTextureVariants skyTextures;
	private ResourceLocation currentSkyTexture;
	private final VertexFormat skyVertexFormat;
	private VertexBuffer skyVBO;
	private VertexBuffer sky2VBO;

	public MiddleEarthSkyRenderer() {
		skyVertexFormat = DefaultVertexFormats.POSITION;
		skyTextures = RandomTextureVariants.loadSkinsList("lotr", "textures/sky/night");
	}

	private void generateSky() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuilder();
		if (skyVBO != null) {
			skyVBO.close();
		}

		skyVBO = new VertexBuffer(skyVertexFormat);
		preRenderSky(buf, 16.0F, false);
		skyVBO.upload(buf);
	}

	private void generateSky2() {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buf = tessellator.getBuilder();
		if (sky2VBO != null) {
			sky2VBO.close();
		}

		sky2VBO = new VertexBuffer(skyVertexFormat);
		preRenderSky(buf, -16.0F, true);
		sky2VBO.upload(buf);
	}

	private void preRenderSky(BufferBuilder buf, float posY, boolean reverseX) {

		buf.begin(7, skyVertexFormat);

		for (int k = -384; k <= 384; k += 64) {
			for (int l = -384; l <= 384; l += 64) {
				float f = k;
				float f1 = k + 64;
				if (reverseX) {
					f1 = k;
					f = k + 64;
				}

				buf.vertex(f, posY, l).endVertex();
				buf.vertex(f1, posY, l).endVertex();
				buf.vertex(f1, posY, l + 64).endVertex();
				buf.vertex(f, posY, l + 64).endVertex();
			}
		}

		buf.end();
	}

	@Override
	public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
		if (skyVBO == null) {
			generateSky();
		}

		if (sky2VBO == null) {
			generateSky2();
		}

		TextureManager texMgr = mc.getTextureManager();
		LOTRDimensionType dimensionType = (LOTRDimensionType) world.dimensionType();
		LOTRDimensionRenderInfo dimensionRenderInfo = (LOTRDimensionRenderInfo) world.effects();
		long worldTime = dimensionType.getWorldTime(world);
		if (dimensionRenderInfo.skyType() == FogType.NORMAL) {
			RenderSystem.disableTexture();
			ActiveRenderInfo ari = mc.gameRenderer.getMainCamera();
			Vector3d skyColor = dimensionRenderInfo.getBlendedCompleteSkyColor(world, ari.getBlockPosition(), partialTicks);
			float skyR = (float) skyColor.x;
			float skyG = (float) skyColor.y;
			float skyB = (float) skyColor.z;
			FogRenderer.levelFogColor();
			BufferBuilder buf = Tessellator.getInstance().getBuilder();
			RenderSystem.depthMask(false);
			RenderSystem.enableFog();
			RenderSystem.color3f(skyR, skyG, skyB);
			skyVBO.bind();
			skyVertexFormat.setupBufferState(0L);
			skyVBO.draw(matrixStack.last().pose(), 7);
			VertexBuffer.unbind();
			skyVertexFormat.clearBufferState();
			RenderSystem.disableFog();
			RenderSystem.disableAlphaTest();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			float celestialAngle = world.getTimeOfDay(partialTicks);
			float[] sunriseColors = dimensionRenderInfo.getSunriseColor(celestialAngle, partialTicks);
			float skyFeatureBrightness;
			float sunriseR;
			float starBrightness;
			float rSun;
			int moonPhase;
			float eMax;
			float rMoon;
			float moonUMin;
			if (sunriseColors != null) {
				RenderSystem.disableTexture();
				RenderSystem.shadeModel(7425);
				matrixStack.pushPose();
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
				skyFeatureBrightness = MathHelper.sin(world.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F;
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(skyFeatureBrightness));
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
				sunriseR = sunriseColors[0];
				starBrightness = sunriseColors[1];
				float sunriseB = sunriseColors[2];
				rSun = sunriseColors[3];
				sunriseR *= 1.2F;
				starBrightness *= 1.2F;
				sunriseR = MathHelper.clamp(sunriseR, 0.0F, 1.0F);
				starBrightness = MathHelper.clamp(starBrightness, 0.0F, 1.0F);
				Matrix4f matrix4f = matrixStack.last().pose();
				buf.begin(6, DefaultVertexFormats.POSITION_COLOR);
				buf.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(sunriseR, starBrightness, sunriseB, rSun).endVertex();

				for (moonPhase = 0; moonPhase <= 16; ++moonPhase) {
					eMax = moonPhase * 6.2831855F / 16.0F;
					rMoon = MathHelper.sin(eMax);
					moonUMin = MathHelper.cos(eMax);
					buf.vertex(matrix4f, rMoon * 120.0F, moonUMin * 120.0F, -moonUMin * 40.0F * rSun).color(sunriseR, starBrightness, sunriseB, 0.0F).endVertex();
				}

				buf.end();
				WorldVertexBufferUploader.end(buf);
				matrixStack.popPose();
				RenderSystem.shadeModel(7424);
			}

			skyFeatureBrightness = dimensionRenderInfo.getSkyFeatureBrightness(world, ari.getBlockPosition(), partialTicks);
			sunriseR = 1.0F - world.getRainLevel(partialTicks);
			skyFeatureBrightness *= sunriseR;
			RenderSystem.enableTexture();
			RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
			matrixStack.pushPose();
			matrixStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
			matrixStack.mulPose(Vector3f.XP.rotationDegrees(celestialAngle * 360.0F));
			starBrightness = world.getStarBrightness(partialTicks) * skyFeatureBrightness;
			if (starBrightness > 0.0F) {
				if (currentSkyTexture == null) {
					currentSkyTexture = skyTextures.getRandomSkin();
				}

				texMgr.bind(currentSkyTexture);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, starBrightness);
				matrixStack.pushPose();
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
				renderSkyboxSide(buf, matrixStack, 4);
				matrixStack.pushPose();
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
				renderSkyboxSide(buf, matrixStack, 1);
				matrixStack.popPose();
				matrixStack.pushPose();
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
				renderSkyboxSide(buf, matrixStack, 0);
				matrixStack.popPose();
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
				renderSkyboxSide(buf, matrixStack, 5);
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
				renderSkyboxSide(buf, matrixStack, 2);
				matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
				renderSkyboxSide(buf, matrixStack, 3);
				matrixStack.popPose();
			} else {
				currentSkyTexture = null;
			}

			Matrix4f matrix = matrixStack.last().pose();
			float moonUMax;
			float moonVMin;
			float sunriseBlend;
			if (skyFeatureBrightness > 0.0F) {
				RenderSystem.defaultBlendFunc();
				rSun = 12.5F;
				texMgr.bind(SUN_TEXTURE);
				int pass = 0;

				while (true) {
					if (pass > 1) {
						RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE, SourceFactor.ONE, DestFactor.ZERO);
						RenderSystem.color4f(1.0F, 1.0F, 1.0F, skyFeatureBrightness);
						pass = DimensionType.MOON_BRIGHTNESS_PER_PHASE.length;
						moonPhase = dimensionType.moonPhase(worldTime);
						boolean lunarEclipse = dimensionType.isLunarEclipse(world);
						if (lunarEclipse) {
							RenderSystem.color3f(1.0F, 0.6F, 0.4F);
						}

						texMgr.bind(MOON_TEXTURE);
						rMoon = 12.5F;
						moonUMin = (float) moonPhase / (float) pass;
						moonUMax = (float) (moonPhase + 1) / (float) pass;
						moonVMin = 0.0F;
						float moonVMax = 1.0F;
						buf.begin(7, DefaultVertexFormats.POSITION_TEX);
						buf.vertex(matrix, -rMoon, -100.0F, rMoon).uv(moonUMax, moonVMax).endVertex();
						buf.vertex(matrix, rMoon, -100.0F, rMoon).uv(moonUMin, moonVMax).endVertex();
						buf.vertex(matrix, rMoon, -100.0F, -rMoon).uv(moonUMin, moonVMin).endVertex();
						buf.vertex(matrix, -rMoon, -100.0F, -rMoon).uv(moonUMax, moonVMin).endVertex();
						buf.end();
						WorldVertexBufferUploader.end(buf);
						break;
					}

					label70: {
						if (pass == 0) {
							RenderSystem.color4f(1.0F, 1.0F, 1.0F, skyFeatureBrightness);
						} else if (pass == 1) {
							if (sunriseColors == null) {
								break label70;
							}

							sunriseBlend = sunriseColors[3];
							sunriseBlend *= 0.5F;
							RenderSystem.color4f(1.0F, 0.9F, 0.2F, sunriseBlend * skyFeatureBrightness);
						}

						buf.begin(7, DefaultVertexFormats.POSITION_TEX);
						buf.vertex(matrix, -rSun, 100.0F, -rSun).uv(0.0F, 0.0F).endVertex();
						buf.vertex(matrix, rSun, 100.0F, -rSun).uv(1.0F, 0.0F).endVertex();
						buf.vertex(matrix, rSun, 100.0F, rSun).uv(1.0F, 1.0F).endVertex();
						buf.vertex(matrix, -rSun, 100.0F, rSun).uv(0.0F, 1.0F).endVertex();
						buf.end();
						WorldVertexBufferUploader.end(buf);
					}

					++pass;
				}
			}

			rSun = celestialAngle - 0.5F;
			float celestialAngleAbs = Math.abs(rSun);
			sunriseBlend = 0.15F;
			eMax = 0.3F;
			if (celestialAngleAbs >= sunriseBlend && celestialAngleAbs <= eMax) {
				rMoon = (sunriseBlend + eMax) / 2.0F;
				moonUMin = eMax - rMoon;
				moonUMax = MathHelper.cos((celestialAngleAbs - rMoon) / moonUMin * 3.1415927F / 2.0F);
				moonUMax *= moonUMax;
				moonUMax = MathHelper.clamp(moonUMax, 0.0F, 1.0F);
				moonVMin = Math.signum(rSun) * 18.0F;
				matrixStack.pushPose();
				matrixStack.mulPose(Vector3f.XP.rotationDegrees(moonVMin));
				Matrix4f matrixE = matrixStack.last().pose();
				RenderSystem.defaultBlendFunc();
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, moonUMax);
				RenderSystem.enableAlphaTest();
				texMgr.bind(EARENDIL_TEXTURE);
				float rEarendil = 1.5F;
				buf.begin(7, DefaultVertexFormats.POSITION_TEX);
				buf.vertex(matrixE, -rEarendil, 100.0F, -rEarendil).uv(0.0F, 0.0F).endVertex();
				buf.vertex(matrixE, rEarendil, 100.0F, -rEarendil).uv(1.0F, 0.0F).endVertex();
				buf.vertex(matrixE, rEarendil, 100.0F, rEarendil).uv(1.0F, 1.0F).endVertex();
				buf.vertex(matrixE, -rEarendil, 100.0F, rEarendil).uv(0.0F, 1.0F).endVertex();
				buf.end();
				WorldVertexBufferUploader.end(buf);
				matrixStack.popPose();
				RenderSystem.disableAlphaTest();
			}

			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.disableBlend();
			RenderSystem.enableAlphaTest();
			RenderSystem.enableFog();
			matrixStack.popPose();
			RenderSystem.disableTexture();
			RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
			double aboveHorizon = mc.player.getEyePosition(partialTicks).y - world.getLevelData().getHorizonHeight();
			if (aboveHorizon < 0.0D) {
				matrixStack.pushPose();
				matrixStack.translate(0.0D, 12.0D, 0.0D);
				sky2VBO.bind();
				skyVertexFormat.setupBufferState(0L);
				sky2VBO.draw(matrixStack.last().pose(), 7);
				VertexBuffer.unbind();
				skyVertexFormat.clearBufferState();
				matrixStack.popPose();
			}

			if (dimensionRenderInfo.hasGround()) {
				RenderSystem.color3f(skyR * 0.2F + 0.04F, skyG * 0.2F + 0.04F, skyB * 0.6F + 0.1F);
			} else {
				RenderSystem.color3f(skyR, skyG, skyB);
			}

			RenderSystem.enableTexture();
			RenderSystem.depthMask(true);
			RenderSystem.disableFog();
		}

	}

	private void renderSkyboxSide(BufferBuilder buf, MatrixStack matStack, int side) {
		int sideX = side % 3;
		int sideY = side / 3;
		float uMin = sideX / 3.0F;
		float uMax = (sideX + 1) / 3.0F;
		float vMin = sideY / 2.0F;
		float vMax = (sideY + 1) / 2.0F;
		float depth = 100.0F;
		Matrix4f matrix = matStack.last().pose();
		buf.begin(7, DefaultVertexFormats.POSITION_TEX);
		buf.vertex(matrix, -depth, -depth, -depth).uv(uMin, vMin).endVertex();
		buf.vertex(matrix, -depth, -depth, depth).uv(uMin, vMax).endVertex();
		buf.vertex(matrix, depth, -depth, depth).uv(uMax, vMax).endVertex();
		buf.vertex(matrix, depth, -depth, -depth).uv(uMax, vMin).endVertex();
		buf.end();
		WorldVertexBufferUploader.end(buf);
	}
}
