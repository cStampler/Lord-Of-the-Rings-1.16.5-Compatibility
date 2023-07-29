package lotr.common.world.gen.layer;

import lotr.common.LOTRLog;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.area.*;

public class LayerWithDataDrivenBiomes {
	private final LazyArea genLayers;

	public LayerWithDataDrivenBiomes(IAreaFactory areaFactory) {
		genLayers = (LazyArea) areaFactory.make();
	}

	public Biome getLayerBiome(Registry biomeReg, int x, int z) {
		int biomeId = genLayers.get(x, z);
		Biome biome = (Biome) biomeReg.byId(biomeId);
		if (biome != null) {
			return biome;
		}
		if (SharedConstants.IS_RUNNING_IN_IDE) {
			throw Util.pauseInIde(new IllegalStateException("Unknown biome id: " + biomeId));
		}
		LOTRLog.warn("Unknown biome id: %d", biomeId);
		return (Biome) biomeReg.get(BiomeRegistry.byId(0));
	}
}
