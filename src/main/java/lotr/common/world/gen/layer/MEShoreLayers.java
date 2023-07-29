package lotr.common.world.gen.layer;

import lotr.common.init.LOTRBiomes;
import lotr.common.world.biome.LOTRBiomeWrapper;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class MEShoreLayers {
	private static boolean isIsland(int biomeID, IWorld world) {
		return biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.ISLAND, world);
	}

	private static boolean isSea(int biomeID, IWorld world) {
		return biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.SEA, world);
	}

	public enum ForIsland implements ICastleTransformer {
		INSTANCE;

		@Override
		public int apply(INoiseRandom noiseRand, int north, int east, int south, int west, int center) {
			IWorld world = LOTRBiomes.getServerBiomeContextWorld();
			return !MEShoreLayers.isSea(center, world) || !MEShoreLayers.isIsland(north, world) && !MEShoreLayers.isIsland(east, world) && !MEShoreLayers.isIsland(south, world) && !MEShoreLayers.isIsland(west, world) ? center : LOTRBiomes.getBiomeID(LOTRBiomes.BEACH, world);
		}
	}

	public enum ForMainland implements ICastleTransformer {
		INSTANCE;

		@Override
		public int apply(INoiseRandom noiseRand, int north, int east, int south, int west, int center) {
			IWorld world = LOTRBiomes.getServerBiomeContextWorld();
			if (MEShoreLayers.isSea(center, world) || MEShoreLayers.isIsland(center, world) || !MEShoreLayers.isSea(north, world) && !MEShoreLayers.isSea(east, world) && !MEShoreLayers.isSea(south, world) && !MEShoreLayers.isSea(west, world)) {
				return center;
			}
			Biome centerBiome = LOTRBiomes.getBiomeByID(center, world);
			LOTRBiomeWrapper shoreBiome = LOTRBiomes.getWrapperFor(centerBiome, world).getShore();
			return LOTRBiomes.getBiomeID(shoreBiome, world);
		}
	}
}
