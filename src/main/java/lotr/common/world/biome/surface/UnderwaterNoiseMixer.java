package lotr.common.world.biome.surface;

import java.util.Map;
import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.util.LOTRUtil;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.IStringSerializable;

public enum UnderwaterNoiseMixer implements IStringSerializable {
	NONE("none", (x, z, in, rand) -> in), SEA_LATITUDE("sea_latitude", (x, z, in, rand) -> {
		double sandProgressF = MapSettingsManager.serverInstance().getCurrentLoadedMap().getWaterLatitudes().getSandCoverageForLatitude(z);
		boolean sandy = false;
		if (sandProgressF <= 0.0D) {
			sandy = false;
		} else if (sandProgressF >= 1.0D) {
			sandy = true;
		} else {
			double noiseAvg = MiddleEarthSurfaceConfig.getNoise1(x, z, 0.1D, 0.03D);
			double noiseNorm = (noiseAvg + 1.0D) / 2.0D;
			sandy = noiseNorm < sandProgressF;
		}

		return sandy ? Blocks.SAND.defaultBlockState() : Blocks.GRAVEL.defaultBlockState();
	});

	public static final Codec<UnderwaterNoiseMixer> CODEC = IStringSerializable.fromEnum(UnderwaterNoiseMixer::values, UnderwaterNoiseMixer::forName);
	private static final Map<String, UnderwaterNoiseMixer> NAME_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), hummel -> ((UnderwaterNoiseMixer) hummel).getSerializedName());
	private final String name;
	private final UnderwaterNoiseMixer.UnderwaterBlockReplacer underwaterBlockReplacer;

	UnderwaterNoiseMixer(String s, UnderwaterNoiseMixer.UnderwaterBlockReplacer replacer) {
		name = s;
		underwaterBlockReplacer = replacer;
	}

	public BlockState getReplacement(int x, int z, BlockState in, Random rand) {
		return underwaterBlockReplacer.getReplacement(x, z, in, rand);
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public static UnderwaterNoiseMixer forName(String name) {
		return (UnderwaterNoiseMixer) NAME_LOOKUP.get(name);
	}

	@FunctionalInterface
	public interface UnderwaterBlockReplacer {
		BlockState getReplacement(int var1, int var2, BlockState var3, Random var4);
	}
}
