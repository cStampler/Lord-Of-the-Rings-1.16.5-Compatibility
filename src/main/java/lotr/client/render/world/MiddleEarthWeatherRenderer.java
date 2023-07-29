package lotr.client.render.world;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import lotr.common.init.LOTRBiomes;
import lotr.common.world.biome.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.client.IWeatherRenderHandler;

public class MiddleEarthWeatherRenderer implements IWeatherRenderHandler {
	private static final ResourceLocation RAIN_TEXTURE = new ResourceLocation("lotr", "textures/weather/rain.png");
	private static final ResourceLocation SNOW_TEXTURE = new ResourceLocation("lotr", "textures/weather/snow.png");
	private static final ResourceLocation ASH_TEXTURE = new ResourceLocation("lotr", "textures/weather/ash.png");
	private static final ResourceLocation SANDSTORM_TEXTURE = new ResourceLocation("lotr", "textures/weather/sandstorm.png");
	private final float[] rainCoordsX = new float[1024];
	private final float[] rainCoordsZ = new float[1024];

	public MiddleEarthWeatherRenderer() {
		setupRainCoords();
	}

	@Override
	public void render(int fullTicks, float partialTicks, ClientWorld world, Minecraft mc, LightTexture lightmap, double xIn, double yIn, double zIn) {
		float f = mc.level.getRainLevel(partialTicks);
		if (f > 0.0F) {
			lightmap.turnOnLightLayer();
			int x = MathHelper.floor(xIn);
			int y = MathHelper.floor(yIn);
			int z = MathHelper.floor(zIn);
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder buf = tess.getBuilder();
			RenderSystem.disableCull();
			RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.defaultAlphaFunc();
			int rainRange = 5;
			if (Minecraft.useFancyGraphics()) {
				rainRange = 10;
			}

			int drawn = -1;
			float tick = fullTicks + partialTicks;
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			Mutable movingPos = new Mutable();

			for (int z1 = z - rainRange; z1 <= z + rainRange; ++z1) {
				for (int x1 = x - rainRange; x1 <= x + rainRange; ++x1) {
					int rainIndex = (z1 - z + 16) * 32 + x1 - x + 16;
					double rainX = rainCoordsX[rainIndex] * 0.5D;
					double rainZ = rainCoordsZ[rainIndex] * 0.5D;
					movingPos.set(x1, 0, z1);
					Biome biome = world.getBiome(movingPos);
					LOTRBiomeWrapper biomeWrapper = LOTRBiomes.getWrapperFor(biome, world);
					RainType rainType = biomeWrapper.getPrecipitationVisually();
					ExtendedWeatherType extWeatherType = biomeWrapper.getExtendedWeatherVisually();
					if (rainType != RainType.NONE || extWeatherType != ExtendedWeatherType.NONE) {
						int rainHeight = world.getHeightmapPos(Type.MOTION_BLOCKING, movingPos).getY();
						int rainMinY = y - rainRange;
						int rainMaxY = y + rainRange;
						if (rainMinY < rainHeight) {
							rainMinY = rainHeight;
						}

						if (rainMaxY < rainHeight) {
							rainMaxY = rainHeight;
						}

						int l2 = rainHeight;
						if (rainHeight < y) {
							l2 = y;
						}

						if (rainMinY != rainMaxY) {
							Random rand = new Random(x1 * x1 * 3121 + x1 * 45238971 ^ z1 * z1 * 418711 + z1 * 13761);
							movingPos.set(x1, rainMinY, z1);
							float f6;
							float f3;
							float f8;
							double d3;
							double d5;
							float f5;
							float f10;
							int k3;
							int l3;
							int i4;
							int j4;
							int k4;
							if (extWeatherType == ExtendedWeatherType.ASHFALL) {
								if (drawn != 1) {
									if (drawn >= 0) {
										tess.end();
									}

									drawn = 1;
									mc.getTextureManager().bind(ASH_TEXTURE);
									buf.begin(7, DefaultVertexFormats.PARTICLE);
								}

								f6 = -((fullTicks & 511) + partialTicks) / 512.0F;
								f3 = (float) (rand.nextDouble() * 0.30000001192092896D + tick * 0.003D * (float) rand.nextGaussian());
								f8 = (float) (rand.nextDouble() + tick * (float) rand.nextGaussian() * 0.001D);
								d3 = x1 + 0.5F - xIn;
								d5 = z1 + 0.5F - zIn;
								f5 = MathHelper.sqrt(d3 * d3 + d5 * d5) / rainRange;
								f10 = ((1.0F - f5 * f5) * 0.3F + 0.5F) * f;
								movingPos.set(x1, l2, z1);
								k3 = WorldRenderer.getLightColor(world, movingPos);
								l3 = k3 >> 16 & '\uffff';
								i4 = (k3 & '\uffff') * 3;
								j4 = (l3 * 3 + 240) / 4;
								k4 = (i4 * 3 + 240) / 4;
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMaxY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F + f3, rainMinY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMaxY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F + f3, rainMinY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMinY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F + f3, rainMaxY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMinY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F + f3, rainMaxY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
							} else if (extWeatherType == ExtendedWeatherType.SANDSTORM) {
								if (drawn != 1) {
									if (drawn >= 0) {
										tess.end();
									}

									drawn = 1;
									mc.getTextureManager().bind(SANDSTORM_TEXTURE);
									buf.begin(7, DefaultVertexFormats.PARTICLE);
								}

								f6 = -((fullTicks & 511) + partialTicks) / 512.0F;
								f3 = tick * (0.07F + (float) rand.nextGaussian() * 0.01F);
								f8 = (float) (rand.nextDouble() + tick * (float) rand.nextGaussian() * 0.001D);
								d3 = x1 + 0.5F - xIn;
								d5 = z1 + 0.5F - zIn;
								f5 = MathHelper.sqrt(d3 * d3 + d5 * d5) / rainRange;
								f10 = ((1.0F - f5 * f5) * 0.3F + 0.5F) * f;
								movingPos.set(x1, l2, z1);
								k3 = WorldRenderer.getLightColor(world, movingPos);
								l3 = k3 >> 16 & '\uffff';
								i4 = (k3 & '\uffff') * 3;
								j4 = (l3 * 3 + 240) / 4;
								k4 = (i4 * 3 + 240) / 4;
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMaxY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F + f3, rainMinY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMaxY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F + f3, rainMinY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMinY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F + f3, rainMaxY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMinY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F + f3, rainMaxY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
							} else if (!LOTRBiomeBase.isSnowingVisually(biomeWrapper, world, movingPos)) {
								if (drawn != 0) {
									if (drawn >= 0) {
										tess.end();
									}

									drawn = 0;
									mc.getTextureManager().bind(RAIN_TEXTURE);
									buf.begin(7, DefaultVertexFormats.PARTICLE);
								}

								int i3 = fullTicks + x1 * x1 * 3121 + x1 * 45238971 + z1 * z1 * 418711 + z1 * 13761 & 31;
								f3 = -(i3 + partialTicks) / 32.0F * (3.0F + rand.nextFloat());
								double d2 = x1 + 0.5F - xIn;
								double d4 = z1 + 0.5F - zIn;
								float f4 = MathHelper.sqrt(d2 * d2 + d4 * d4) / rainRange;
								f5 = ((1.0F - f4 * f4) * 0.5F + 0.5F) * f;
								movingPos.set(x1, l2, z1);
								int j3 = WorldRenderer.getLightColor(world, movingPos);
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMaxY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F, rainMinY * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).uv2(j3).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMaxY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F, rainMinY * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).uv2(j3).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMinY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F, rainMaxY * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).uv2(j3).endVertex();
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMinY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F, rainMaxY * 0.25F + f3).color(1.0F, 1.0F, 1.0F, f5).uv2(j3).endVertex();
							} else {
								if (drawn != 1) {
									if (drawn >= 0) {
										tess.end();
									}

									drawn = 1;
									mc.getTextureManager().bind(SNOW_TEXTURE);
									buf.begin(7, DefaultVertexFormats.PARTICLE);
								}

								f6 = -((fullTicks & 511) + partialTicks) / 512.0F;
								f3 = (float) (rand.nextDouble() + tick * 0.01D * (float) rand.nextGaussian());
								f8 = (float) (rand.nextDouble() + tick * (float) rand.nextGaussian() * 0.001D);
								d3 = x1 + 0.5F - xIn;
								d5 = z1 + 0.5F - zIn;
								f5 = MathHelper.sqrt(d3 * d3 + d5 * d5) / rainRange;
								f10 = ((1.0F - f5 * f5) * 0.3F + 0.5F) * f;
								movingPos.set(x1, l2, z1);
								k3 = WorldRenderer.getLightColor(world, movingPos);
								l3 = k3 >> 16 & '\uffff';
								i4 = (k3 & '\uffff') * 3;
								j4 = (l3 * 3 + 240) / 4;
								k4 = (i4 * 3 + 240) / 4;
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMaxY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F + f3, rainMinY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMaxY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F + f3, rainMinY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn + rainX + 0.5D, rainMinY - yIn, z1 - zIn + rainZ + 0.5D).uv(1.0F + f3, rainMaxY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
								buf.vertex(x1 - xIn - rainX + 0.5D, rainMinY - yIn, z1 - zIn - rainZ + 0.5D).uv(0.0F + f3, rainMaxY * 0.25F + f6 + f8).color(1.0F, 1.0F, 1.0F, f10).uv2(k4, j4).endVertex();
							}
						}
					}
				}
			}

			if (drawn >= 0) {
				tess.end();
			}

			RenderSystem.enableCull();
			RenderSystem.disableBlend();
			RenderSystem.defaultAlphaFunc();
			lightmap.turnOffLightLayer();
		}

	}

	private void setupRainCoords() {
		for (int x = 0; x < 32; ++x) {
			for (int z = 0; z < 32; ++z) {
				float xf = z - 16;
				float zf = x - 16;
				float dist = MathHelper.sqrt(xf * xf + zf * zf);
				rainCoordsX[x << 5 | z] = -zf / dist;
				rainCoordsZ[x << 5 | z] = xf / dist;
			}
		}

	}
}
