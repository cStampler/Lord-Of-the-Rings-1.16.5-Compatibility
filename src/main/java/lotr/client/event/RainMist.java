package lotr.client.event;

import lotr.common.config.LOTRConfig;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;

public class RainMist {
	private float rainMist;
	private float prevRainMist;

	public float getRainMistStrength(float partialTick) {
		return prevRainMist + (rainMist - prevRainMist) * partialTick;
	}

	public void reset() {
		prevRainMist = rainMist = 0.0F;
	}

	public void update(World world, Entity viewer) {
		if ((Boolean) LOTRConfig.CLIENT.rainMist.get()) {
			prevRainMist = rainMist;
			Biome biome = world.getBiome(viewer.blockPosition());
			RainType precip = biome.getPrecipitation();
			if (!world.isRaining() || precip != RainType.RAIN && precip != RainType.SNOW) {
				if (rainMist > 0.0F) {
					rainMist -= 0.0016666667F;
					rainMist = Math.max(rainMist, 0.0F);
				}
			} else if (rainMist < 1.0F) {
				rainMist += 0.008333334F;
				rainMist = Math.min(rainMist, 1.0F);
			}
		} else {
			reset();
		}

	}
}
