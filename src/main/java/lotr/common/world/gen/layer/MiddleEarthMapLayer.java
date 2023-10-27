package lotr.common.world.gen.layer;

import lotr.common.init.LOTRBiomes;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;

public enum MiddleEarthMapLayer implements IAreaTransformer0 {
	INSTANCE;

	@Override
	public int applyPixel(INoiseRandom noiseRand, int x, int z) {
		MapSettings mapSettings = MapSettingsManager.serverInstance().getCurrentLoadedMap();
		IWorld world = LOTRBiomes.getServerBiomeContextWorld();
		int xRelative = x + mapSettings.getOriginX();
		int zRelative = z + mapSettings.getOriginZ();
		return mapSettings.getBiomeIdAt(xRelative, zRelative, world);
	}
}
