package lotr.common.world.gen.layer;

import lotr.common.init.LOTRBiomes;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class MEAddIslandsLayer implements IAreaTransformer1 {
	public static final MEAddIslandsLayer DEFAULT_ADD_ISLANDS = new MEAddIslandsLayer(400);
	private final int islandChance;

	public MEAddIslandsLayer(int chance) {
		islandChance = chance;
	}

	@Override
	public int applyPixel(IExtendedNoiseRandom noiseRand, IArea biomeLayer, int x, int z) {
		IWorld world = LOTRBiomes.getServerBiomeContextWorld();
		int biome = biomeLayer.get(x, z);
		if (biome == LOTRBiomes.getBiomeID(LOTRBiomes.SEA, world) && biomeLayer.get(x - 1, z) == biome && biomeLayer.get(x + 1, z) == biome && biomeLayer.get(x, z - 1) == biome && biomeLayer.get(x, z + 1) == biome && noiseRand.nextRandom(islandChance) == 0) {
			biome = LOTRBiomes.getBiomeID(LOTRBiomes.ISLAND, world);
		}

		return biome;
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
