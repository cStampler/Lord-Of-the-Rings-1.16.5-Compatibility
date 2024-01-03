package lotr.common.world.gen.carver;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.carver.UnderwaterCanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class MiddleEarthUnderwaterCanyonCarver extends UnderwaterCanyonWorldCarver {
	public MiddleEarthUnderwaterCanyonCarver(Codec<ProbabilityConfig> codec) {
		super(codec);
		replaceableBlocks = LOTRWorldCarvers.listUnderwaterCarvableBlocks();
	}
}
