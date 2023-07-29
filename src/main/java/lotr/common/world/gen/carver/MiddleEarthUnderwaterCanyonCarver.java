package lotr.common.world.gen.carver;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.carver.UnderwaterCanyonWorldCarver;

public class MiddleEarthUnderwaterCanyonCarver extends UnderwaterCanyonWorldCarver {
	public MiddleEarthUnderwaterCanyonCarver(Codec codec) {
		super(codec);
		replaceableBlocks = LOTRWorldCarvers.listUnderwaterCarvableBlocks();
	}
}
