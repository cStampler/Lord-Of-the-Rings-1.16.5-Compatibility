package lotr.common.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum MESeedRiversLayer implements IAreaTransformer0 {
	INSTANCE;

	@Override
	public int applyPixel(INoiseRandom context, int x, int z) {
		return context.nextRandom(299999) + 2;
	}
}
