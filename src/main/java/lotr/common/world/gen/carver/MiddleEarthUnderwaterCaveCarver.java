package lotr.common.world.gen.carver;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.carver.UnderwaterCaveWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class MiddleEarthUnderwaterCaveCarver extends UnderwaterCaveWorldCarver {
	public MiddleEarthUnderwaterCaveCarver(Codec<ProbabilityConfig> codec) {
		super(codec);
		replaceableBlocks = LOTRWorldCarvers.listUnderwaterCarvableBlocks();
	}
}
