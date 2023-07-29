package lotr.common.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum SeedBiomeSubtypesLayer implements IAreaTransformer0 {
	INSTANCE;

	@Override
	public int applyPixel(INoiseRandom noiseRand, int x, int z) {
		return x == 0 && z == 0 ? 1000 : noiseRand.nextRandom(1000);
	}

	public static final float randFloat(int initVal) {
		return initVal / 1000.0F;
	}
}
