package lotr.common.world.gen.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum ClassicRemoveSeaAtOriginLayer implements IAreaTransformer1 {
	INSTANCE;

	@Override
	public int applyPixel(IExtendedNoiseRandom noiseRand, IArea zoomedSeaLayer, int x, int z) {
		int sea = zoomedSeaLayer.get(x, z);
		if (Math.abs(x) <= 1 && Math.abs(z) <= 1) {
			sea = 0;
		}

		return sea;
	}

	@Override
	public int getParentX(int x) {
		return x;
	}

	@Override
	public int getParentY(int z) {
		return z;
	}
}
