package lotr.common.world.gen.layer;

import java.util.function.LongFunction;

import lotr.common.world.gen.MiddleEarthBiomeGenSettings;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.layer.LayerUtil;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

public class MiddleEarthWorldLayers {
	public static final int MIN_SCALE_POWER = 2;
	public static final int MAX_SCALE_POWER = 10;

	public static LayerWithDataDrivenBiomes create(long worldSeed, boolean classicBiomes, MiddleEarthBiomeGenSettings genSettings) {
		int maxCacheSize = 25;
		IAreaFactory areaFactory = createLayers(classicBiomes, genSettings, seedModifier -> new LazyAreaLayerContext(maxCacheSize, worldSeed, seedModifier));
		return new LayerWithDataDrivenBiomes(areaFactory);
	}

	public static IAreaFactory createLayers(boolean classicBiomes, MiddleEarthBiomeGenSettings genSettings, LongFunction longFunc) {
		int riverScale = genSettings.getRiverSize();
		IAreaFactory riverLayer = MESeedRiversLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(100L));
		riverLayer = LayerUtil.zoom(1000L, ZoomLayer.NORMAL, riverLayer, 2 + riverScale, longFunc);
		riverLayer = MERiverLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(1L), riverLayer);
		riverLayer = SmoothLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(1000L), riverLayer);
		IAreaFactory subBiomesLayer = SeedBiomeSubtypesLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(3000L));
		subBiomesLayer = LayerUtil.zoom(3000L, ZoomLayer.NORMAL, subBiomesLayer, 2, longFunc);
		IAreaFactory biomeLayer = null;
		if (classicBiomes) {
			IAreaFactory seaLayer = ClassicSeedSeasLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(2012L));
			seaLayer = LayerUtil.zoom(200L, ZoomLayer.NORMAL, seaLayer, 3, longFunc);
			seaLayer = ClassicRemoveSeaAtOriginLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(300L), seaLayer);
			biomeLayer = new ClassicBiomeLayer(genSettings).run((IExtendedNoiseRandom) longFunc.apply(2013L), seaLayer);
			biomeLayer = LayerUtil.zoom(300L, ZoomLayer.NORMAL, biomeLayer, 2, longFunc);
		} else {
			biomeLayer = MiddleEarthMapLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(1954L));
		}

		biomeLayer = BiomeSubtypesLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(1000L), biomeLayer, subBiomesLayer);
		biomeLayer = new MEAddIslandsLayer(400).run((IExtendedNoiseRandom) longFunc.apply(400L), biomeLayer);
		biomeLayer = new MapSettingsDependentBiomeZoomLayer(genSettings, classicBiomes, longFunc).run((IExtendedNoiseRandom) longFunc.apply(0L), biomeLayer);
		biomeLayer = SmoothLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(1000L), biomeLayer);
		return MEAddRiversLayer.INSTANCE.run((IExtendedNoiseRandom) longFunc.apply(100L), biomeLayer, riverLayer);
	}

	public static MapSettings getActiveMapSettings() {
		return MapSettingsManager.serverInstance().getCurrentLoadedMap();
	}
}
