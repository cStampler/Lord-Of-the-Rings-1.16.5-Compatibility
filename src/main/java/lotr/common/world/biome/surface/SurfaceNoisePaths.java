package lotr.common.world.biome.surface;

import java.util.Random;

import net.minecraft.block.*;

public class SurfaceNoisePaths {
	public static BlockState getReplacement(int x, int z, BlockState in, boolean top, Random rand) {
		if (top) {
			int xMod = x;
			int zMod = z;
			boolean adjDifferent = false;
			int noiseCentral = (int) ((MiddleEarthSurfaceConfig.getNoise1(x, z, 0.003D) + MiddleEarthSurfaceConfig.getNoise2(x, z, 0.003D)) * 2.0D);

			for (int i = -1; i <= 1; ++i) {
				for (int k = -1; k <= 1; ++k) {
					if (i != 0 || k != 0) {
						int noiseAdj = (int) ((MiddleEarthSurfaceConfig.getNoise1(xMod + i, zMod + k, 0.003D) + MiddleEarthSurfaceConfig.getNoise2(xMod + i, zMod + k, 0.003D)) * 2.0D);
						if (noiseAdj != noiseCentral) {
							adjDifferent = true;
						}
					}
				}
			}

			if (adjDifferent) {
				return Blocks.GRASS_PATH.defaultBlockState();
			}
		}

		return in;
	}
}
