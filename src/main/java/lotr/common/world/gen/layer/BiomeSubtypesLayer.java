package lotr.common.world.gen.layer;

import lotr.common.init.LOTRBiomes;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;

public enum BiomeSubtypesLayer implements IAreaTransformer2 {
	INSTANCE;

	@Override
	public int applyPixel(INoiseRandom context, IArea biomeLayer, IArea subtypeInitLayer, int x, int z) {
		int biomeID = biomeLayer.get(x, z);
		int subtypeSeed = subtypeInitLayer.get(x, z);
		float subtypeF = SeedBiomeSubtypesLayer.randFloat(subtypeSeed);
		IWorld world = LOTRBiomes.getServerBiomeContextWorld();
		int newBiomeID = biomeID;
		if (biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.SHIRE, world)) {
			if (subtypeF < 0.15F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.SHIRE_WOODLANDS, world);
			}
		} else if (biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.NORTHLANDS, world)) {
			if (subtypeF < 0.1F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.NORTHLANDS_FOREST, world);
			} else if (subtypeF < 0.15F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.DENSE_NORTHLANDS_FOREST, world);
			}
		} else if (biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.SNOWY_NORTHLANDS, world)) {
			if (subtypeF < 0.1F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.SNOWY_NORTHLANDS_FOREST, world);
			} else if (subtypeF < 0.15F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.DENSE_SNOWY_NORTHLANDS_FOREST, world);
			}
		} else if (biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.NORTHLANDS_FOREST, world)) {
			if (subtypeF < 0.33F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.DENSE_NORTHLANDS_FOREST, world);
			}
		} else if (biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.SNOWY_NORTHLANDS_FOREST, world)) {
			if (subtypeF < 0.33F) {
				newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.DENSE_SNOWY_NORTHLANDS_FOREST, world);
			}
		} else if (biomeID == LOTRBiomes.getBiomeID(LOTRBiomes.SEA, world) && biomeLayer.get(x - 1, z) == biomeID && biomeLayer.get(x + 1, z) == biomeID && biomeLayer.get(x, z - 1) == biomeID && biomeLayer.get(x, z + 1) == biomeID && subtypeF < 0.02F) {
			newBiomeID = LOTRBiomes.getBiomeID(LOTRBiomes.ISLAND, world);
		}

		return newBiomeID;
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
