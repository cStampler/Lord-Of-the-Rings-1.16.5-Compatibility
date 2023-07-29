package lotr.client.event;

import lotr.common.world.biome.ExtendedWeatherType;
import net.minecraft.util.math.MathHelper;

public class SandstormFog extends ExtendedWeatherFog {
	@Override
	public ExtendedWeatherType getTargetWeather() {
		return ExtendedWeatherType.SANDSTORM;
	}

	@Override
	protected float[] modifyFogHsb(float[] fogHsb, float weatherFogStrength) {
		fogHsb[0] = MathHelper.lerp(weatherFogStrength, fogHsb[0], 0.165F);
		fogHsb[1] = MathHelper.lerp(weatherFogStrength, fogHsb[1], 0.1F);
		fogHsb[2] *= MathHelper.lerp(weatherFogStrength, 1.0F, 0.75F);
		return fogHsb;
	}
}
