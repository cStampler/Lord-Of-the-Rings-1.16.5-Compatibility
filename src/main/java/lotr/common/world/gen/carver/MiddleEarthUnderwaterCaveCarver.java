package lotr.common.world.gen.carver;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;

public class MiddleEarthUnderwaterCaveCarver extends UnderwaterCaveWorldCarver {
	public MiddleEarthUnderwaterCaveCarver(Codec codec) {
		super(codec);
		replaceableBlocks = LOTRWorldCarvers.listUnderwaterCarvableBlocks();
	}
}
