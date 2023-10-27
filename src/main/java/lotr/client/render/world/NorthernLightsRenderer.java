package lotr.client.render.world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import lotr.client.render.ProjectionUtil;
import lotr.common.LOTRLog;
import lotr.common.init.LOTRBiomes;
import lotr.common.init.LOTRWorldTypes;
import lotr.common.time.MiddleEarthCalendar;
import lotr.common.time.ShireReckoning;
import lotr.common.util.CalendarUtil;
import lotr.common.world.biome.LOTRBiomeBase;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import lotr.common.world.map.NorthernLightsSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;

public class NorthernLightsRenderer {
	private int nlTick;
	private int currentNightNum = -1;
	private float brightnessTonight;
	private float maxNorthTonight;
	private float minNorthTonight;
	private int rainingTick;
	private int rainingTickPrev;
	private boolean atUtumno = false;
	private int utumnoChange = 0;
	private final Random rand = new Random(54382502529626502L);
	private final Random dateRand = new Random(664292528855626902L);
	private float[] colorTopCurrent;
	private float[] colorMidCurrent;
	private float[] colorBottomCurrent;
	private float[] colorTopNext;
	private float[] colorMidNext;
	private float[] colorBottomNext;
	private int colorChangeTime;
	private int colorChangeTick;
	private int timeUntilColorChange;
	private int utumnoCheckTime;
	private final NorthernLightsRenderer.AuroraCycle wave0 = new NorthernLightsRenderer.AuroraCycle(4.0F, 0.01F, 0.9F);
	private List waveOscillations = new ArrayList();
	private List glowOscillations = new ArrayList();
	private final NorthernLightsRenderer.AuroraCycle glow0 = new NorthernLightsRenderer.AuroraCycle(20.0F, 0.02F, 0.6F);

	private Color[] generateColorSet() {
		float h1 = MathHelper.nextFloat(rand, 0.22F, 0.48F);
		float h2 = MathHelper.nextFloat(rand, 0.22F, 0.48F);
		float h3 = MathHelper.nextFloat(rand, 0.22F, 0.48F);
		if (rand.nextInt(5) == 0) {
			h3 = MathHelper.nextFloat(rand, 0.94F, 1.01F);
		} else {
			if (rand.nextInt(6) == 0) {
				h1 = MathHelper.nextFloat(rand, 0.78F, 1.08F);
			}

			if (rand.nextInt(6) == 0) {
				h1 = MathHelper.nextFloat(rand, 0.78F, 1.08F);
				h2 = MathHelper.nextFloat(rand, 0.85F, 1.08F);
			}
		}

		if (rand.nextInt(50) == 0) {
			h1 = MathHelper.nextFloat(rand, 0.7F, 1.08F);
			h2 = MathHelper.nextFloat(rand, 0.54F, 0.77F);
			h3 = MathHelper.nextFloat(rand, 0.48F, 0.7F);
		}

		Color topColor = new Color(Color.HSBtoRGB(h1, 1.0F, 1.0F));
		Color midColor = new Color(Color.HSBtoRGB(h2, 1.0F, 1.0F));
		Color bottomColor = new Color(Color.HSBtoRGB(h3, 1.0F, 1.0F));
		return new Color[] { topColor, midColor, bottomColor };
	}

	private float getNorthernness(Minecraft mc, Entity entity, ClientWorld world) {
		if (!LOTRWorldTypes.hasMapFeaturesClientside()) {
			return getBiomeDependentBrightness(mc, entity, world);
		}
		float minNorth = minNorthTonight;
		float maxNorth = maxNorthTonight;
		float northernness = ((float) entity.getZ() - minNorth) / (maxNorth - minNorth);
		return MathHelper.clamp(northernness, 0.0F, 1.0F);
	}

	private float glowEquation(float t, float tick, boolean fancy) {
		float f = 0.0F;
		f += glow0.calc(t, tick);
		NorthernLightsRenderer.AuroraCycle c;
		if (fancy) {
			for (Iterator var6 = glowOscillations.iterator(); var6.hasNext(); f += c.calc(t, tick)) {
				c = (NorthernLightsRenderer.AuroraCycle) var6.next();
			}
		}

		return f;
	}

	public void render(Minecraft mc, ClientWorld world, MatrixStack matStack, float tick) {
		if (mc.options.renderDistance >= 4) {
			IProfiler profiler = world.getProfiler();
			float minSun = 0.2F;
			float daylight = (world.getSkyDarken(tick) - minSun) / (1.0F - minSun);
			float maxDaylight = 0.3F;
			float nlBrightness = (1.0F - daylight - (1.0F - maxDaylight)) / maxDaylight;
			if (nlBrightness > 0.0F) {
				float tonight = brightnessTonight;
				float utumno = utumnoChange / 200.0F;
				tonight += (1.0F - tonight) * utumno;
				if (tonight > 0.0F) {
					nlBrightness *= tonight;
					float northernness = getNorthernness(mc, mc.cameraEntity, world);
					if (northernness > 0.0F) {
						nlBrightness *= northernness;
						float raininess = (rainingTickPrev + (rainingTick - rainingTickPrev) * tick) / 80.0F;
						if (raininess < 1.0F) {
							nlBrightness *= 1.0F - raininess;
							nlBrightness *= 0.3F + (1.0F - world.getRainLevel(tick)) * 0.7F;
							profiler.push("aurora");
							float nlScale = 2000.0F;
							float nlDistance = (1.0F - northernness) * nlScale * 2.0F;
							float nlHeight = nlScale * 0.48F;
							Matrix4f projectMatrix = ProjectionUtil.getProjection(mc, tick, nlScale * 5.0F);
							RenderSystem.matrixMode(5889);
							RenderSystem.pushMatrix();
							RenderSystem.loadIdentity();
							RenderSystem.multMatrix(projectMatrix);
							RenderSystem.matrixMode(5888);
							RenderSystem.pushMatrix();
							RenderSystem.loadIdentity();
							matStack.pushPose();
							matStack.translate(0.0D, nlHeight, -nlDistance);
							RenderSystem.multMatrix(matStack.last().pose());
							RenderSystem.disableTexture();
							RenderSystem.disableAlphaTest();
							RenderSystem.depthMask(false);
							RenderSystem.enableBlend();
							RenderSystem.defaultBlendFunc();
							RenderSystem.alphaFunc(516, 0.01F);
							RenderSystem.shadeModel(7425);
							RenderSystem.disableCull();
							profiler.push("sheet");
							boolean fancy = Minecraft.useFancyGraphics();
							renderSheet(fancy, nlScale * -0.5F, nlBrightness * 0.8F, nlScale * 1.0F, nlScale * 0.23F, 0.25502F, tick, profiler);
							renderSheet(fancy, nlScale * 0.0F, nlBrightness * 1.0F, nlScale * 1.5F, nlScale * 0.27F, 0.15696F, tick, profiler);
							renderSheet(fancy, nlScale * 0.5F, nlBrightness * 0.8F, nlScale * 1.0F, nlScale * 0.23F, 0.67596F, tick, profiler);
							profiler.pop();
							RenderSystem.enableCull();
							RenderSystem.shadeModel(7424);
							RenderSystem.defaultAlphaFunc();
							RenderSystem.disableBlend();
							RenderSystem.depthMask(true);
							RenderSystem.enableAlphaTest();
							RenderSystem.enableTexture();
							RenderSystem.matrixMode(5889);
							RenderSystem.popMatrix();
							matStack.popPose();
							RenderSystem.matrixMode(5888);
							RenderSystem.popMatrix();
							profiler.pop();
						}
					}
				}
			}
		}
	}

	private void renderSheet(boolean fancy, float nlDistance, float nlBrightness, float halfWidth, float halfHeight, float tickExtra, float tick, IProfiler profiler) {
		float r1 = colorTopCurrent[0];
		float g1 = colorTopCurrent[1];
		float b1 = colorTopCurrent[2];
		float r2 = colorMidCurrent[0];
		float g2 = colorMidCurrent[1];
		float b2 = colorMidCurrent[2];
		float r3 = colorBottomCurrent[0];
		float g3 = colorBottomCurrent[1];
		float b3 = colorBottomCurrent[2];
		float a1;
		if (colorChangeTime > 0) {
			a1 = (float) colorChangeTick / (float) colorChangeTime;
			a1 = 1.0F - a1;
			r1 = colorTopCurrent[0] + (colorTopNext[0] - colorTopCurrent[0]) * a1;
			g1 = colorTopCurrent[1] + (colorTopNext[1] - colorTopCurrent[1]) * a1;
			b1 = colorTopCurrent[2] + (colorTopNext[2] - colorTopCurrent[2]) * a1;
			r2 = colorMidCurrent[0] + (colorMidNext[0] - colorMidCurrent[0]) * a1;
			g2 = colorMidCurrent[1] + (colorMidNext[1] - colorMidCurrent[1]) * a1;
			b2 = colorMidCurrent[2] + (colorMidNext[2] - colorMidCurrent[2]) * a1;
			r3 = colorBottomCurrent[0] + (colorBottomNext[0] - colorBottomCurrent[0]) * a1;
			g3 = colorBottomCurrent[1] + (colorBottomNext[1] - colorBottomCurrent[1]) * a1;
			b3 = colorBottomCurrent[2] + (colorBottomNext[2] - colorBottomCurrent[2]) * a1;
		}

		a1 = 0.0F;
		float a2 = 0.4F;
		float a3 = 0.8F;
		a1 *= nlBrightness;
		a2 *= nlBrightness;
		a3 *= nlBrightness;
		float fullTick = nlTick + tick + tickExtra;
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buf = tess.getBuilder();
		buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
		profiler.push("vertexLoop");
		int strips = 500;
		if (!fancy) {
			strips = 80;
		}

		for (int l = 0; l < strips; ++l) {
			float t = (float) l / (float) strips;
			float t1 = (float) (l + 1) / (float) strips;
			float a1_here = a1;
			float a2_here = a2;
			float a3_here = a3;
			float fadeEdge = 0.3F;
			float fadePos = Math.min(t, 1.0F - t);
			float randomFade;
			if (fadePos < fadeEdge) {
				randomFade = fadePos / fadeEdge;
				a1_here = a1 * randomFade;
				a2_here = a2 * randomFade;
				a3_here = a3 * randomFade;
			}

			randomFade = 0.5F + glowEquation(t, fullTick, fancy) * 0.5F;
			a1_here *= randomFade;
			a2_here *= randomFade;
			a3_here *= randomFade;
			float x0 = -halfWidth + halfWidth * 2.0F * t;
			float x1 = x0 + halfWidth * 2.0F / strips;
			float yMin = -halfHeight;
			float yMid = -halfHeight * 0.3F;
			float maxWaveDisplacement = halfWidth * 0.15F;
			float z0 = nlDistance + waveEquation(t, fullTick) * maxWaveDisplacement;
			float z1 = nlDistance + waveEquation(t1, fullTick) * maxWaveDisplacement;
			float extra = halfHeight * 0.25F;
			buf.vertex(x0, yMin - extra, z0).color(r3, g3, b3, 0.0F).endVertex();
			buf.vertex(x1, yMin - extra, z1).color(r3, g3, b3, 0.0F).endVertex();
			buf.vertex(x1, yMin, z1).color(r3, g3, b3, a3_here).endVertex();
			buf.vertex(x0, yMin, z0).color(r3, g3, b3, a3_here).endVertex();
			buf.vertex(x0, yMin, z0).color(r3, g3, b3, a3_here).endVertex();
			buf.vertex(x1, yMin, z1).color(r3, g3, b3, a3_here).endVertex();
			buf.vertex(x1, yMid, z1).color(r2, g2, b2, a2_here).endVertex();
			buf.vertex(x0, yMid, z0).color(r2, g2, b2, a2_here).endVertex();
			buf.vertex(x0, yMid, z0).color(r2, g2, b2, a2_here).endVertex();
			buf.vertex(x1, yMid, z1).color(r2, g2, b2, a2_here).endVertex();
			buf.vertex(x1, halfHeight, z1).color(r1, g1, b1, a1_here).endVertex();
			buf.vertex(x0, halfHeight, z0).color(r1, g1, b1, a1_here).endVertex();
		}

		profiler.pop();
		profiler.push("draw");
		tess.end();
		profiler.pop();
	}

	public void update(Entity viewer) {
		++nlTick;
		World world = viewer.getCommandSenderWorld();
		int effectiveDay = MiddleEarthCalendar.currentDay;
		float daytime = world.getDayTime() % 48000L / 48000.0F;
		if (daytime < 0.25F) {
			--effectiveDay;
		}

		if (effectiveDay != currentNightNum) {
			currentNightNum = effectiveDay;
			dateRand.setSeed(currentNightNum * 35920558925051L + currentNightNum + 83025820626792L);
			ShireReckoning.Month month = ((ShireReckoning.ShireDate) ShireReckoning.INSTANCE.getDate(currentNightNum)).month;
			boolean isYule = month == ShireReckoning.Month.YULE_1 || month == ShireReckoning.Month.YULE_2;
			MapSettings loadedMapSettings = MapSettingsManager.clientInstance().getLoadedMapOrLoadDefault(Minecraft.getInstance().getResourceManager());
			if (loadedMapSettings == null) {
				LOTRLog.error("No MapSettings instance is loaded on the client! This should not happen and is very bad!");
			}

			NorthernLightsSettings northernLightsSettings = loadedMapSettings.getNorthernLights();
			int fullNorth = northernLightsSettings.getFullNorth_world();
			int minSouth = northernLightsSettings.getStartSouth_world();
			int maxSouth = northernLightsSettings.getFurthestPossibleSouth_world();
			maxNorthTonight = fullNorth;
			float southRand = dateRand.nextFloat();
			float southRandAmount;
			if (!isYule && southRand >= 0.01F) {
				if (southRand < 0.1F) {
					southRandAmount = MathHelper.nextFloat(dateRand, 0.5F, 0.75F);
				} else if (southRand < 0.5F) {
					southRandAmount = MathHelper.nextFloat(dateRand, 0.25F, 0.5F);
				} else {
					southRandAmount = MathHelper.nextFloat(dateRand, 0.0F, 0.25F);
				}
			} else {
				southRandAmount = MathHelper.nextFloat(dateRand, 0.75F, 1.0F);
			}

			minNorthTonight = MathHelper.lerp(southRandAmount, minSouth, maxSouth);
			if (CalendarUtil.isChristmas()) {
				minNorthTonight = 1.0E8F;
			}

			float appearChance = 0.5F;
			if (!isYule && effectiveDay != 0 && dateRand.nextFloat() >= appearChance) {
				brightnessTonight = 0.0F;
			} else {
				brightnessTonight = MathHelper.nextFloat(dateRand, 0.4F, 1.0F);
			}
		}

		rainingTickPrev = rainingTick;
		boolean raining = isRainLayerAt(viewer);
		if (raining) {
			if (rainingTick < 80) {
				++rainingTick;
			}
		} else if (rainingTick > 0) {
			--rainingTick;
		}

		Color[] cs;
		if (colorTopCurrent == null) {
			cs = generateColorSet();
			colorTopCurrent = cs[0].getColorComponents((float[]) null);
			colorMidCurrent = cs[1].getColorComponents((float[]) null);
			colorBottomCurrent = cs[2].getColorComponents((float[]) null);
		}

		if (timeUntilColorChange > 0) {
			--timeUntilColorChange;
		} else if (rand.nextInt(1200) == 0) {
			cs = generateColorSet();
			colorTopNext = cs[0].getColorComponents((float[]) null);
			colorMidNext = cs[1].getColorComponents((float[]) null);
			colorBottomNext = cs[2].getColorComponents((float[]) null);
			colorChangeTime = MathHelper.nextInt(rand, 100, 200);
			colorChangeTick = colorChangeTime;
			utumnoCheckTime = 0;
		}

		if (colorChangeTick > 0) {
			--colorChangeTick;
			if (colorChangeTick <= 0) {
				colorChangeTime = 0;
				colorTopCurrent = colorTopNext;
				colorMidCurrent = colorMidNext;
				colorBottomCurrent = colorBottomNext;
				colorTopNext = null;
				colorMidNext = null;
				colorBottomNext = null;
				timeUntilColorChange = MathHelper.nextInt(rand, 1200, 2400);
			}
		}

		if (utumnoCheckTime > 0) {
			--utumnoCheckTime;
		} else {
			atUtumno = false;
			utumnoCheckTime = 200;
		}

		if (atUtumno) {
			if (utumnoChange < 200) {
				++utumnoChange;
			}
		} else if (utumnoChange > 0) {
			--utumnoChange;
		}

		float freq;
		float speed;
		float amp;
		NorthernLightsRenderer.AuroraCycle cycle;
		if (rand.nextInt(50) == 0) {
			freq = MathHelper.nextFloat(rand, 8.0F, 100.0F);
			speed = freq * 5.0E-4F;
			amp = MathHelper.nextFloat(rand, 0.05F, 0.3F);
			cycle = new NorthernLightsRenderer.AuroraCycle(freq, speed, amp);
			cycle.age = cycle.maxAge = MathHelper.nextInt(rand, 100, 400);
			waveOscillations.add(cycle);
		}

		HashSet removes;
		Iterator var21;
		NorthernLightsRenderer.AuroraCycle c;
		if (!waveOscillations.isEmpty()) {
			removes = new HashSet();
			var21 = waveOscillations.iterator();

			while (var21.hasNext()) {
				c = (NorthernLightsRenderer.AuroraCycle) var21.next();
				c.update();
				if (c.age <= 0) {
					removes.add(c);
				}
			}

			waveOscillations.removeAll(removes);
		}

		if (rand.nextInt(120) == 0) {
			freq = MathHelper.nextFloat(rand, 30.0F, 150.0F);
			speed = freq * 0.002F;
			amp = MathHelper.nextFloat(rand, 0.05F, 0.5F);
			cycle = new NorthernLightsRenderer.AuroraCycle(freq, speed, amp);
			cycle.age = cycle.maxAge = MathHelper.nextInt(rand, 100, 400);
			glowOscillations.add(cycle);
		}

		if (rand.nextInt(300) == 0) {
			freq = MathHelper.nextFloat(rand, 400.0F, 500.0F);
			speed = freq * 0.004F;
			amp = MathHelper.nextFloat(rand, 0.1F, 0.2F);
			cycle = new NorthernLightsRenderer.AuroraCycle(freq, speed, amp);
			cycle.age = cycle.maxAge = MathHelper.nextInt(rand, 100, 200);
			glowOscillations.add(cycle);
		}

		if (!glowOscillations.isEmpty()) {
			removes = new HashSet();
			var21 = glowOscillations.iterator();

			while (var21.hasNext()) {
				c = (NorthernLightsRenderer.AuroraCycle) var21.next();
				c.update();
				if (c.age <= 0) {
					removes.add(c);
				}
			}

			glowOscillations.removeAll(removes);
		}

	}

	private float waveEquation(float t, float tick) {
		float f = 0.0F;
		f += wave0.calc(t, tick);

		NorthernLightsRenderer.AuroraCycle c;
		for (Iterator var6 = waveOscillations.iterator(); var6.hasNext(); f += c.calc(t, tick)) {
			c = (NorthernLightsRenderer.AuroraCycle) var6.next();
		}

		return f;
	}

	private static float getBiomeDependentBrightness(Minecraft mc, Entity entity, ClientWorld world) {
		int sampled = 0;
		int total = 0;
		int range = Math.min(mc.options.renderDistance * 2, 36);
		BlockPos entityPos = entity.blockPosition();
		Mutable movingPos = new Mutable();

		for (int x = -range; x <= range; ++x) {
			for (int z = -range; z <= range; ++z) {
				movingPos.set(entityPos).move(x, 0, z);
				Biome biome = world.getBiome(movingPos);
				if (biome.getPrecipitation() == RainType.SNOW) {
					++total;
				}

				++sampled;
			}
		}

		return (float) total / (float) sampled;
	}

	private static boolean isRainLayerAt(Entity entity) {
		World world = entity.getCommandSenderWorld();
		BlockPos pos = entity.blockPosition();
		if (!world.isRaining()) {
			return false;
		}
		Biome biome = world.getBiome(pos);
		if (biome.getPrecipitation() == RainType.SNOW) {
			return false;
		}
		float temp = LOTRBiomes.getWrapperFor(biome, world).getTemperatureForSnowWeatherRendering(world, pos);
		return !LOTRBiomeBase.isTemperatureSuitableForSnow(temp) && biome.getPrecipitation() == RainType.RAIN;
	}

	private static class AuroraCycle {
		public final float freq;
		public final float tickMultiplier;
		public final float amp;
		public int age;
		public int maxAge = -1;
		private float ampModifier = 1.0F;

		public AuroraCycle(float f, float t, float a) {
			freq = f;
			tickMultiplier = t;
			amp = a;
		}

		public float calc(float t, float tick) {
			return MathHelper.cos(t * freq + tick * tickMultiplier) * amp * ampModifier;
		}

		public void update() {
			if (age >= 0) {
				--age;
				float a = (float) (maxAge - age) / (float) maxAge;
				ampModifier = Math.min(a, 1.0F - a);
			}

		}
	}
}
