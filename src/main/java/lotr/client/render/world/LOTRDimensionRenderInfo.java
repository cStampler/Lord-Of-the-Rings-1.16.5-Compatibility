package lotr.client.render.world;

import lotr.common.LOTRMod;
import lotr.common.config.LOTRConfig;
import lotr.common.init.LOTRBiomes;
import net.minecraft.client.*;
import net.minecraft.client.world.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class LOTRDimensionRenderInfo extends DimensionRenderInfo {
	private static final double[] PINKISH_CLOUDS_RGB_MULTIPLIER = { 1.07D, 0.85D, 1.07D };
	private int skyG;
	private int skyB;
	private double cloudG;
	private double cloudB;

	public LOTRDimensionRenderInfo(float cloudHeight, boolean isSkyColored, FogType fogType) {
		super(cloudHeight, isSkyColored, fogType, false, false);
		if ((Boolean) LOTRConfig.CLIENT.modSky.get()) {
			setSkyRenderHandler(new MiddleEarthSkyRenderer());
		}

		if ((Boolean) LOTRConfig.CLIENT.modClouds.get()) {
			setCloudRenderHandler(new MiddleEarthCloudRenderer());
		}

		if ((Boolean) LOTRConfig.CLIENT.newWeatherRendering.get()) {
			setWeatherRenderHandler(new MiddleEarthWeatherRenderer());
		}

		setWeatherParticleRenderHandler(new MiddleEarthWeatherParticleRenderHandler());
	}

	private Vector3d getBaseCloudColor(ClientWorld world, float partialTicks) {
		Vector3d normalClouds = world.getCloudColor(partialTicks);
		return modifyCloudColorForSunriseSunset(normalClouds, world, partialTicks);
	}

	private int getBiomeBlendDistance() {
		Minecraft mc = Minecraft.getInstance();
		GameSettings settings = mc.options;
		int distance = 0;
		if (Minecraft.useFancyGraphics()) {
			distance = Math.min(settings.renderDistance * 2, 36);
		}

		return distance;
	}

	private int getBlendedBiomeSkyColor(World world, BlockPos pos) {
		int distance = getBiomeBlendDistance();
		int skyR = skyG = skyB = 0;
		int count = 0;

		for (int x = -distance; x <= distance; ++x) {
			for (int z = -distance; z <= distance; ++z) {
				Biome biome = world.getBiome(pos.offset(x, 0, z));
				int skyHere = biome.getSkyColor();
				skyR += skyHere >> 16 & 255;
				skyG += skyHere >> 8 & 255;
				skyB += skyHere & 255;
				++count;
			}
		}

		skyR /= count;
		skyG /= count;
		skyB /= count;
		return skyR << 16 | skyG << 8 | skyB;
	}

	public Vector3d getBlendedCompleteCloudColor(ClientWorld world, BlockPos pos, float partialTicks) {
		int distance = getBiomeBlendDistance();
		Vector3d clouds = getBaseCloudColor(world, partialTicks);
		double cloudR = cloudG = cloudB = 0.0D;
		int count = 0;

		for (int x = -distance; x <= distance; ++x) {
			for (int z = -distance; z <= distance; ++z) {
				Vector3d tempClouds = new Vector3d(clouds.x, clouds.y, clouds.z);
				Biome biome = world.getBiome(pos.offset(x, 0, z));
				tempClouds = LOTRBiomes.getWrapperFor(biome, world).alterCloudColor(tempClouds);
				cloudR += tempClouds.x;
				cloudG += tempClouds.y;
				cloudB += tempClouds.z;
				++count;
			}
		}

		cloudR /= count;
		cloudG /= count;
		cloudB /= count;
		return new Vector3d(cloudR, cloudG, cloudB);
	}

	public Vector3d getBlendedCompleteSkyColor(ClientWorld world, BlockPos pos, float partialTicks) {
		float celestialAngleRadians = world.getSunAngle(partialTicks);
		float dayBright = MathHelper.cos(celestialAngleRadians) * 2.0F + 0.5F;
		dayBright = MathHelper.clamp(dayBright, 0.0F, 1.0F);
		int biomeSky = getBlendedBiomeSkyColor(world, pos);
		float r = (biomeSky >> 16 & 255) / 255.0F;
		float g = (biomeSky >> 8 & 255) / 255.0F;
		float b = (biomeSky & 255) / 255.0F;
		r *= dayBright;
		g *= dayBright;
		b *= dayBright;
		float rain = world.getRainLevel(partialTicks);
		float thunder;
		float thunderLerp;
		if (rain > 0.0F) {
			thunder = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.6F;
			thunderLerp = 1.0F - rain * 0.75F;
			r = r * thunderLerp + thunder * (1.0F - thunderLerp);
			g = g * thunderLerp + thunder * (1.0F - thunderLerp);
			b = b * thunderLerp + thunder * (1.0F - thunderLerp);
		}

		thunder = world.getThunderLevel(partialTicks);
		float lightningFlashF;
		if (thunder > 0.0F) {
			thunderLerp = (r * 0.3F + g * 0.59F + b * 0.11F) * 0.2F;
			lightningFlashF = 1.0F - thunder * 0.75F;
			r = r * lightningFlashF + thunderLerp * (1.0F - lightningFlashF);
			g = g * lightningFlashF + thunderLerp * (1.0F - lightningFlashF);
			b = b * lightningFlashF + thunderLerp * (1.0F - lightningFlashF);
		}

		int lightningFlash = world.getSkyFlashTime();
		if (lightningFlash > 0) {
			lightningFlashF = lightningFlash - partialTicks;
			lightningFlashF = Math.min(lightningFlashF, 1.0F);
			lightningFlashF *= 0.45F;
			r = r * (1.0F - lightningFlashF) + 0.8F * lightningFlashF;
			g = g * (1.0F - lightningFlashF) + 0.8F * lightningFlashF;
			b = b * (1.0F - lightningFlashF) + 1.0F * lightningFlashF;
		}

		return new Vector3d(r, g, b);
	}

	@Override
	public Vector3d getBrightnessDependentFogColor(Vector3d fogRgb, float partialTicks) {
		return fogRgb.multiply(partialTicks * 0.94F + 0.06F, partialTicks * 0.94F + 0.06F, partialTicks * 0.91F + 0.09F);
	}

	private World getClientWorld() {
		return LOTRMod.PROXY.getClientWorld();
	}

	public float getCloudCoverage(World world, BlockPos pos, float partialTicks) {
		int distance = getBiomeBlendDistance();
		float cloudCoverage = 0.0F;
		int count = 0;

		for (int x = -distance; x <= distance; ++x) {
			for (int z = -distance; z <= distance; ++z) {
				Biome biome = world.getBiome(pos.offset(x, 0, z));
				float coverageHere = LOTRBiomes.getWrapperFor(biome, world).getCloudCoverage();
				cloudCoverage += coverageHere;
				++count;
			}
		}

		cloudCoverage /= count;
		return cloudCoverage;
	}

	public float getSkyFeatureBrightness(World world, BlockPos pos, float partialTicks) {
		int distance = getBiomeBlendDistance();
		float totalBrightness = 0.0F;
		int count = 0;

		for (int x = -distance; x <= distance; ++x) {
			for (int z = -distance; z <= distance; ++z) {
				Biome biome = world.getBiome(pos.offset(x, 0, z));
				boolean hasSky = LOTRBiomes.getWrapperFor(biome, world).hasSkyFeatures();
				float skyBr = hasSky ? 1.0F : 0.0F;
				totalBrightness += skyBr;
				++count;
			}
		}

		totalBrightness /= count;
		return totalBrightness;
	}

	@Override
	public boolean isFoggyAt(int x, int z) {
		World world = getClientWorld();
		Biome biome = world.getBiome(new BlockPos(x, 0, z));
		return LOTRBiomes.getWrapperFor(biome, world).isFoggy();
	}

	private Vector3d modifyCloudColorForSunriseSunset(Vector3d normalClouds, ClientWorld world, float partialTicks) {
		double[] rgb = { normalClouds.x, normalClouds.y, normalClouds.z };
		float celestialAngleRadians = world.getSunAngle(partialTicks);
		float dayBright = MathHelper.cos(celestialAngleRadians) * 2.0F + 0.5F;
		dayBright = MathHelper.clamp(dayBright, 0.0F, 1.0F);
		if (dayBright >= 0.2F && dayBright <= 0.98F) {
			float pinkishness = dayBright >= 0.5F && dayBright <= 0.8F ? 1.0F : dayBright < 0.5F ? (dayBright - 0.2F) / 0.3F : (0.98F - dayBright) / 0.18F;

			for (int i = 0; i < rgb.length; ++i) {
				double rgbElem = rgb[i];
				rgbElem *= MathHelper.lerp(pinkishness, 1.0D, PINKISH_CLOUDS_RGB_MULTIPLIER[i]);
				rgbElem = MathHelper.clamp(rgbElem, 0.0D, 1.0D);
				rgb[i] = rgbElem;
			}
		}

		return new Vector3d(rgb[0], rgb[1], rgb[2]);
	}

	public float[] modifyFogIntensity(float farPlane, net.minecraft.client.renderer.FogRenderer.FogType fogType, Entity viewer) {
		int distance = getBiomeBlendDistance();
		float fogStart = 0.0F;
		float fogEnd = 0.0F;
		BlockPos viewerPos = viewer.blockPosition();
		int count = 0;

		for (int x = -distance; x <= distance; ++x) {
			for (int z = -distance; z <= distance; ++z) {
				float thisFogStart = 0.0F;
				float thisFogEnd = 0.0F;
				boolean nearFog = isFoggyAt(viewerPos.getX() + x, viewerPos.getZ() + z);
				if (nearFog) {
					thisFogStart = farPlane * 0.05F;
					thisFogEnd = Math.min(farPlane, 192.0F) * 0.5F;
				} else {
					if (fogType == net.minecraft.client.renderer.FogRenderer.FogType.FOG_SKY) {
						thisFogStart = 0.0F;
					} else {
						thisFogStart = farPlane * 0.75F;
					}
					thisFogEnd = farPlane;
				}

				fogStart += thisFogStart;
				fogEnd += thisFogEnd;
				++count;
			}
		}

		fogStart /= count;
		fogEnd /= count;
		return new float[] { fogStart, fogEnd };
	}
}
