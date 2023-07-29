package lotr.client.event;

import lotr.common.config.LOTRConfig;
import lotr.common.init.LOTRBiomes;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;

public class MistyMountainsMist {
	private int mistTick;
	private int prevMistTick;

	public float getCurrentMistFactor(Entity viewer, float partialTick) {
		float mistTickF = prevMistTick + (mistTick - prevMistTick) * partialTick;
		mistTickF /= 80.0F;
		float mistFactorY = (float) viewer.getY() / 256.0F;
		return mistTickF * mistFactorY;
	}

	private boolean hasMistAtLocation(World world, BlockPos pos) {
		if (pos.getY() >= 72 && world.canSeeSkyFromBelowWater(pos) && world.getBrightness(LightType.BLOCK, pos) < 7) {
			Biome biome = world.getBiome(pos);
			return LOTRBiomes.getWrapperFor(biome, world).hasMountainsMist();
		}
		return false;
	}

	public void reset() {
		prevMistTick = mistTick = 0;
	}

	public void update(World world, Entity viewer) {
		if ((Boolean) LOTRConfig.CLIENT.mistyMountainsMist.get()) {
			prevMistTick = mistTick;
			if (hasMistAtLocation(world, viewer.blockPosition())) {
				if (mistTick < 80) {
					++mistTick;
				}
			} else if (mistTick > 0) {
				--mistTick;
			}
		} else {
			reset();
		}

	}
}
