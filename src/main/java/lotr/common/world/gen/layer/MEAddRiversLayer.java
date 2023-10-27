package lotr.common.world.gen.layer;

import lotr.common.init.LOTRBiomes;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MEAddRiversLayer implements IAreaTransformer2, IDimOffset0Transformer {
	INSTANCE;

	@Override
	public int applyPixel(INoiseRandom context, IArea biomeArea, IArea riverArea, int x, int z) {
		int biomeID = biomeArea.get(this.getParentX(x), this.getParentY(z));
		if (MiddleEarthWorldLayers.getActiveMapSettings().getProceduralRivers()) {
			int hasRiver = riverArea.get(this.getParentX(x), this.getParentY(z));
			if (hasRiver >= 1) {
				IWorld world = LOTRBiomes.getServerBiomeContextWorld();
				Biome river = LOTRBiomes.getWrapperFor(LOTRBiomes.getBiomeByID(biomeID, world), world).getRiver(world);
				if (river != null) {
					return LOTRBiomes.getBiomeID(river, world);
				}
			}
		}

		return biomeID;
	}
}
