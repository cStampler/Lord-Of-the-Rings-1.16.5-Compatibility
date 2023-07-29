package lotr.common.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum MERiverLayer implements ICastleTransformer {
	INSTANCE;

	@Override
	public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
		int i = riverFilter(center);
		return i == riverFilter(east) && i == riverFilter(north) && i == riverFilter(west) && i == riverFilter(south) ? 0 : 1;
	}

	private static int riverFilter(int riverMix) {
		return riverMix >= 2 ? 2 + (riverMix & 1) : riverMix;
	}
}
