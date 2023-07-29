package lotr.client.event;

import lotr.common.config.LOTRConfig;
import lotr.common.dim.LOTRDimensionType;
import lotr.common.init.LOTRBiomes;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.biome.Biome;

public class SunGlare {
	private static final float[] NORMAL_RGB = { 1.0F, 1.0F, 1.0F };
	private static final float[] YELLOW_RGB = { 1.0F, 0.6F, 0.0F };
	private float sunGlare;
	private float prevSunGlare;
	private float glareYellowness;

	private RayTraceResult getBlockRayTrace(Entity viewer, double distance) {
		return viewer.pick(distance, 1.0F, true);
	}

	public float getGlareBrightness(float partialTick) {
		float lerp = prevSunGlare + (sunGlare - prevSunGlare) * partialTick;
		return Math.min(lerp, 1.0F);
	}

	public float[] getGlareColorRGB() {
		float[] rgb = new float[3];

		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = MathHelper.lerp(glareYellowness, NORMAL_RGB[i], YELLOW_RGB[i]);
		}

		return rgb;
	}

	private float getSunBrightnessBasedOnlyOnAngle(ClientWorld world, float partialTicks) {
		float f = world.getTimeOfDay(partialTicks);
		float f1 = 1.0F - (MathHelper.cos(f * 3.1415927F * 2.0F) * 2.0F + 0.2F);
		f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
		f1 = 1.0F - f1;
		return f1 * 0.8F + 0.2F;
	}

	public void reset() {
		prevSunGlare = sunGlare = 0.0F;
		glareYellowness = 0.0F;
	}

	public void update(ClientWorld world, Entity viewer) {
		if ((Boolean) LOTRConfig.CLIENT.sunGlare.get() && world.dimensionType() instanceof LOTRDimensionType && world.dimensionType().hasSkyLight()) {
			prevSunGlare = sunGlare;
			float renderTick = 1.0F;
			RayTraceResult look = getBlockRayTrace(viewer, 10000.0D);
			boolean lookingAtSky = look.getType() == Type.MISS;
			Biome biome = world.getBiome(viewer.blockPosition());
			boolean biomeHasSun = LOTRBiomes.getWrapperFor(biome, world).hasSkyFeatures();
			float celestialAngle = world.getTimeOfDay(renderTick) * 360.0F - 90.0F;
			float sunYaw = 90.0F;
			float yc = MathHelper.cos((float) Math.toRadians(-sunYaw - 180.0F));
			float ys = MathHelper.sin((float) Math.toRadians(-sunYaw - 180.0F));
			float pc = -MathHelper.cos((float) Math.toRadians(-celestialAngle));
			float ps = MathHelper.sin((float) Math.toRadians(-celestialAngle));
			Vector3d sunVec = new Vector3d(ys * pc, ps, yc * pc);
			Vector3d lookVec = viewer.getViewVector(renderTick);
			double cos = lookVec.dot(sunVec) / (lookVec.length() * sunVec.length());
			float cQ = ((float) cos - 0.97F) / 0.029999971F;
			cQ = Math.max(cQ, 0.0F);
			float brightness = world.getSkyDarken(renderTick);
			float bQ = (brightness - 0.45F) / 0.55F;
			bQ = Math.max(bQ, 0.0F);
			float brightnessForYellowness = getSunBrightnessBasedOnlyOnAngle(world, renderTick);
			glareYellowness = (0.75F - brightnessForYellowness) / 0.14999998F;
			glareYellowness = MathHelper.clamp(glareYellowness, 0.0F, 1.0F);
			float maxGlareNow = cQ * bQ;
			float maxGlareNowWithOversaturation = maxGlareNow * 1.4F;
			if (maxGlareNow > 0.0F && lookingAtSky && !world.isRaining() && biomeHasSun) {
				if (sunGlare < maxGlareNowWithOversaturation) {
					sunGlare += 0.1F * maxGlareNow;
					sunGlare = Math.min(sunGlare, maxGlareNowWithOversaturation);
				} else if (sunGlare > maxGlareNowWithOversaturation) {
					sunGlare -= 0.02F;
					sunGlare = Math.max(sunGlare, maxGlareNowWithOversaturation);
				}
			} else {
				if (sunGlare > 0.0F) {
					sunGlare -= 0.02F;
				}

				sunGlare = Math.max(sunGlare, 0.0F);
			}
		} else {
			reset();
		}

	}
}
