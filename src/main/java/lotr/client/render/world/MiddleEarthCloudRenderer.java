package lotr.client.render.world;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.render.ProjectionUtil;
import lotr.common.LOTRMod;
import lotr.common.config.LOTRConfig;
import lotr.common.time.MiddleEarthCalendar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.*;
import net.minecraft.world.World;
import net.minecraftforge.client.ICloudRenderHandler;

public class MiddleEarthCloudRenderer implements ICloudRenderHandler {
	private static final ResourceLocation CLOUDS_LOW = new ResourceLocation("lotr", "textures/sky/clouds_low.png");
	private static final ResourceLocation CLOUDS_MID = new ResourceLocation("lotr", "textures/sky/clouds_mid.png");
	private static final ResourceLocation CLOUDS_HIGH = new ResourceLocation("lotr", "textures/sky/clouds_high.png");
	private static final Random CLOUD_RNG = new Random(81747493362629326L);
	private final MiddleEarthCloudRenderer.CloudProperty cloudOpacitySeed = new MiddleEarthCloudRenderer.CloudProperty(233591206262L, 0.0F, 1.0F, 0.1F);
	private final MiddleEarthCloudRenderer.CloudProperty cloudSpeed = new MiddleEarthCloudRenderer.CloudProperty(6283905602629L, 0.0F, 0.5F, 0.001F);
	private final MiddleEarthCloudRenderer.CloudProperty cloudAngle = new MiddleEarthCloudRenderer.CloudProperty(360360635650636L, 0.0F, 6.2831855F, 0.01F);
	private double cloudPosXPre;
	private double cloudPosX;
	private double cloudPosZPre;
	private double cloudPosZ;

	private Vector3d getCloudColor(ClientWorld world, BlockPos viewPos, float partialTicks) {
		return ((LOTRDimensionRenderInfo) world.effects()).getBlendedCompleteCloudColor(world, viewPos, partialTicks);
	}

	private float getCloudOpacity(ClientWorld world, BlockPos viewPos, float partialTicks) {
		float opacitySeed = cloudOpacitySeed.getValue(partialTicks);
		float coverageHere = ((LOTRDimensionRenderInfo) world.effects()).getCloudCoverage(world, viewPos, partialTicks);
		float maxOpacityAtFullCoverage = 1.0F;
		float maxOpacity = MathHelper.lerp(coverageHere, 0.5F, maxOpacityAtFullCoverage);
		float minOpacityAtFullCoverage = 0.2F;
		float x0 = 0.0F;
		float x1 = 1.0F;
		float dx = x1 - x0;
		float gradientAtFullCoverage = (maxOpacityAtFullCoverage - minOpacityAtFullCoverage) / dx;
		float xInterceptAtFullCoverage = -minOpacityAtFullCoverage / gradientAtFullCoverage;
		float xIntercept = MathHelper.lerp(coverageHere, x1, xInterceptAtFullCoverage);
		float gradient = (maxOpacity - 0.0F) / Math.max(1.0F - xIntercept, 1.0E-7F);
		float opacity = gradient * (opacitySeed - xIntercept);
		return Math.max(opacity, 0.0F);
	}

	@Override
	public void render(int ticks, float partialTicks, MatrixStack matStack, ClientWorld world, Minecraft mc, double viewEntityX, double viewEntityY, double viewEntityZ) {
		world.getProfiler().push("lotrClouds");
		LOTRDimensionRenderInfo dimensionRenderInfo = (LOTRDimensionRenderInfo) world.effects();
		float cloudHeight = dimensionRenderInfo.getCloudHeight();
		if (!Float.isNaN(cloudHeight)) {
			BlockPos viewPos = mc.gameRenderer.getMainCamera().getBlockPosition();
			float cloudOpacity = getCloudOpacity(world, viewPos, partialTicks);
			cloudOpacity *= 1.0F - LOTRMod.PROXY.getCurrentSandstormFogStrength();
			if (cloudOpacity > 0.0F) {
				int configCloudRange = (Integer) LOTRConfig.CLIENT.cloudRange.get();
				int farCloudRange = configCloudRange * 3;
				Matrix4f projectMatrix = ProjectionUtil.getProjection(mc, partialTicks, farCloudRange);
				RenderSystem.matrixMode(5889);
				RenderSystem.pushMatrix();
				RenderSystem.loadIdentity();
				RenderSystem.multMatrix(projectMatrix);
				RenderSystem.matrixMode(5888);
				RenderSystem.pushMatrix();
				RenderSystem.loadIdentity();
				matStack.pushPose();
				RenderSystem.multMatrix(matStack.last().pose());
				RenderSystem.disableCull();
				RenderSystem.depthMask(false);
				RenderSystem.enableDepthTest();
				RenderSystem.enableAlphaTest();
				RenderSystem.alphaFunc(516, 0.01F);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Vector3d cloudColor = getCloudColor(world, viewPos, partialTicks);
				renderCloudLayer(mc, partialTicks, CLOUDS_LOW, cloudColor, cloudOpacity * 0.9F, cloudHeight, configCloudRange, 1.0D);
				if (Minecraft.useFancyGraphics()) {
					renderCloudLayer(mc, partialTicks, CLOUDS_MID, cloudColor, cloudOpacity * 0.6F, cloudHeight + 50.0F, configCloudRange, 0.5D);
					renderCloudLayer(mc, partialTicks, CLOUDS_HIGH, cloudColor, cloudOpacity * 0.7F, cloudHeight + 500.0F, farCloudRange, 0.25D);
				}

				FogRenderer.setupNoFog();
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.enableCull();
				RenderSystem.depthMask(true);
				RenderSystem.defaultAlphaFunc();
				RenderSystem.disableBlend();
				RenderSystem.matrixMode(5889);
				RenderSystem.popMatrix();
				matStack.popPose();
				RenderSystem.matrixMode(5888);
				RenderSystem.popMatrix();
			}
		}

		world.getProfiler().pop();
	}

	private void renderCloudLayer(Minecraft mc, float partialTicks, ResourceLocation texture, Vector3d cloudColor, float alpha, float layerHeight, int layerRenderRange, double layerSpeed) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		Vector3d pos = mc.gameRenderer.getMainCamera().getPosition();
		int scale = 4096;
		double invScaleD = 1.0D / scale;
		RenderSystem.fogMode(9729);
		RenderSystem.fogStart(layerRenderRange * 0.9F);
		RenderSystem.fogEnd(layerRenderRange);
		RenderSystem.setupNvFogDistance();
		mc.getTextureManager().bind(texture);
		double posX = pos.x;
		double posY = pos.y;
		double posZ = pos.z;
		double cloudPosXAdd = cloudPosXPre + (cloudPosX - cloudPosXPre) * partialTicks;
		double cloudPosZAdd = cloudPosZPre + (cloudPosZ - cloudPosZPre) * partialTicks;
		cloudPosXAdd *= layerSpeed;
		cloudPosZAdd *= layerSpeed;
		posX += cloudPosXAdd;
		posZ += cloudPosZAdd;
		int x = MathHelper.floor(posX / scale);
		int z = MathHelper.floor(posZ / scale);
		double cloudX = posX - x * scale;
		double cloudZ = posZ - z * scale;
		float cloudY = layerHeight - (float) posY + 0.33F;
		buf.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
		float r = (float) cloudColor.x;
		float g = (float) cloudColor.y;
		float b = (float) cloudColor.z;
		int interval = layerRenderRange;

		for (int i = -layerRenderRange; i < layerRenderRange; i += interval) {
			for (int k = -layerRenderRange; k < layerRenderRange; k += interval) {
				int xMin = i + 0;
				int xMax = i + interval;
				int zMin = k + 0;
				int zMax = k + interval;
				float uMin = (float) ((xMin + cloudX) * invScaleD);
				float uMax = (float) ((xMax + cloudX) * invScaleD);
				float vMin = (float) ((zMin + cloudZ) * invScaleD);
				float vMax = (float) ((zMax + cloudZ) * invScaleD);
				buf.vertex(xMin, cloudY, zMax).color(r, g, b, alpha).uv(uMin, vMax).endVertex();
				buf.vertex(xMax, cloudY, zMax).color(r, g, b, alpha).uv(uMax, vMax).endVertex();
				buf.vertex(xMax, cloudY, zMin).color(r, g, b, alpha).uv(uMax, vMin).endVertex();
				buf.vertex(xMin, cloudY, zMin).color(r, g, b, alpha).uv(uMin, vMin).endVertex();
			}
		}

		tess.end();
	}

	public void resetClouds() {
		cloudOpacitySeed.reset();
		cloudSpeed.reset();
		cloudAngle.reset();
	}

	public void updateClouds(World world) {
		cloudOpacitySeed.update(world);
		cloudSpeed.update(world);
		cloudAngle.update(world);
		float angle = cloudAngle.getValue(1.0F);
		float speed = cloudSpeed.getValue(1.0F);
		cloudPosXPre = cloudPosX;
		cloudPosX += MathHelper.cos(angle) * speed;
		cloudPosZPre = cloudPosZ;
		cloudPosZ += MathHelper.sin(angle) * speed;
	}

	private static class CloudProperty {
		private final long baseSeed;
		private float value;
		private float prevValue;
		private final float minValue;
		private final float maxValue;
		private final float interval;

		public CloudProperty(long l, float min, float max, float i) {
			baseSeed = l;
			value = -1.0F;
			minValue = min;
			maxValue = max;
			interval = i;
		}

		private float getCurrentDayValue() {
			int day = MiddleEarthCalendar.currentDay;
			long seed = day * baseSeed + day + 83025820626792L;
			MiddleEarthCloudRenderer.CLOUD_RNG.setSeed(seed);
			return MathHelper.nextFloat(MiddleEarthCloudRenderer.CLOUD_RNG, minValue, maxValue);
		}

		public float getValue(float f) {
			return prevValue + (value - prevValue) * f;
		}

		public void reset() {
			value = -1.0F;
		}

		public void update(World world) {
			float currentDayValue = getCurrentDayValue();
			if (value == -1.0F) {
				prevValue = value = currentDayValue;
			} else {
				prevValue = value;
				if (value > currentDayValue) {
					value -= interval;
					value = Math.max(value, currentDayValue);
				} else if (value < currentDayValue) {
					value += interval;
					value = Math.min(value, currentDayValue);
				}
			}

		}
	}
}
