package lotr.common.world.gen.layer;

import java.util.function.LongFunction;

import lotr.common.world.gen.MiddleEarthBiomeGenSettings;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.*;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class MapSettingsDependentBiomeZoomLayer implements IAreaTransformer1 {
	private final MiddleEarthBiomeGenSettings biomeGenSettings;
	private final boolean classicBiomes;
	private final LongFunction longFunc;

	public MapSettingsDependentBiomeZoomLayer(MiddleEarthBiomeGenSettings settings, boolean classic, LongFunction lFunc) {
		biomeGenSettings = settings;
		classicBiomes = classic;
		longFunc = lFunc;
	}

	@Override
	public int applyPixel(IExtendedNoiseRandom noiseRand, IArea baseBiomeLayer, int x, int z) {
		int mapScale = MiddleEarthWorldLayers.getActiveMapSettings().getScalePower();
		int classicBiomeScale = biomeGenSettings.getClassicBiomeSize();
		int curZoom = 0;
		int numZooms = classicBiomes ? classicBiomeScale : mapScale - 2;
		IAreaFactory zoomedBiomeLayer = () -> baseBiomeLayer;

		while (true) {
			if (curZoom == Math.max(0, numZooms - 5)) {
			}

			if (curZoom == Math.max(0, numZooms - 4)) {
				zoomedBiomeLayer = MEAddIslandsLayer.DEFAULT_ADD_ISLANDS.run((IExtendedNoiseRandom) longFunc.apply(300L), zoomedBiomeLayer);
				zoomedBiomeLayer = MEShoreLayers.ForMainland.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(1000L), zoomedBiomeLayer);
			}

			if (curZoom == Math.max(0, numZooms - 3)) {
				zoomedBiomeLayer = MEShoreLayers.ForIsland.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(900L), zoomedBiomeLayer);
			}

			if (curZoom == Math.max(0, numZooms - 2)) {
			}

			if (curZoom >= numZooms) {
				return zoomedBiomeLayer.make().get(x, z);
			}

			zoomedBiomeLayer = ZoomLayer.NORMAL.run((IExtendedNoiseRandom) longFunc.apply(1000L + curZoom), zoomedBiomeLayer);
			++curZoom;
		}
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
