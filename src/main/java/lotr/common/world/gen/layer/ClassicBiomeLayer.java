package lotr.common.world.gen.layer;

import java.util.List;

import lotr.common.init.LOTRBiomes;
import lotr.common.world.gen.MiddleEarthBiomeGenSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class ClassicBiomeLayer implements IAreaTransformer1 {
	private final List classicBiomeNames = LOTRBiomes.listBiomeNamesForClassicGen();

	public ClassicBiomeLayer(MiddleEarthBiomeGenSettings genSettings) {
	}

	@Override
	public int applyPixel(IExtendedNoiseRandom noiseRand, IArea seaLayer, int x, int z) {
		IWorld world = LOTRBiomes.getServerBiomeContextWorld();
		int sea = seaLayer.get(x, z);
		if (sea > 0) {
			return LOTRBiomes.getBiomeID(LOTRBiomes.SEA, world);
		}
		ResourceLocation biomeName = (ResourceLocation) classicBiomeNames.get(noiseRand.nextRandom(classicBiomeNames.size()));
		return LOTRBiomes.getBiomeIDByRegistryName(biomeName, world);
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
