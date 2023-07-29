package lotr.client.event;

import java.awt.Color;

import lotr.common.config.LOTRConfig;
import lotr.common.init.LOTRBiomes;
import lotr.common.world.biome.ExtendedWeatherType;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;

public abstract class ExtendedWeatherFog {
	private float fog;
	private float prevFog;
	private float[] fogHsbCache = null;
	private float[] fogModifiedRgbCache = null;

	public abstract ExtendedWeatherType getTargetWeather();

	public float getWeatherFogStrength(float partialTick) {
		return prevFog + (fog - prevFog) * partialTick;
	}

	public void modifyFogColors(FogColors event, float renderPartialTick) {
		float weatherFogStrength = getWeatherFogStrength(renderPartialTick);
		if (weatherFogStrength > 0.0F) {
			Color fogColor = new Color(event.getRed(), event.getGreen(), event.getBlue());
			fogHsbCache = Color.RGBtoHSB(fogColor.getRed(), fogColor.getGreen(), fogColor.getBlue(), fogHsbCache);
			fogHsbCache = modifyFogHsb(fogHsbCache, weatherFogStrength);
			fogColor = Color.getHSBColor(fogHsbCache[0], fogHsbCache[1], fogHsbCache[2]);
			fogModifiedRgbCache = fogColor.getColorComponents(fogModifiedRgbCache);
			event.setRed(fogModifiedRgbCache[0]);
			event.setGreen(fogModifiedRgbCache[1]);
			event.setBlue(fogModifiedRgbCache[2]);
		}

	}

	protected abstract float[] modifyFogHsb(float[] var1, float var2);

	public void reset() {
		prevFog = fog = 0.0F;
	}

	public void update(World world, Entity viewer) {
		if ((Boolean) LOTRConfig.CLIENT.newWeatherRendering.get()) {
			prevFog = fog;
			Biome biome = world.getBiome(viewer.blockPosition());
			if (world.isRaining() && LOTRBiomes.getWrapperFor(biome, world).getExtendedWeatherVisually() == getTargetWeather()) {
				if (fog < 1.0F) {
					fog += 0.008333334F;
					fog = Math.min(fog, 1.0F);
				}
			} else if (fog > 0.0F) {
				fog -= 0.005F;
				fog = Math.max(fog, 0.0F);
			}
		} else {
			reset();
		}

	}
}
