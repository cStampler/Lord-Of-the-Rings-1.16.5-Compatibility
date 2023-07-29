package lotr.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CobwebFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(config -> ((CobwebFeatureConfig) config).blockProvider), Codec.INT.fieldOf("tries").orElse(64).forGetter(config -> ((CobwebFeatureConfig) config).tries), Codec.INT.fieldOf("xspread").orElse(6).forGetter(config -> ((CobwebFeatureConfig) config).xspread), Codec.INT.fieldOf("yspread").orElse(4).forGetter(config -> ((CobwebFeatureConfig) config).yspread), Codec.INT.fieldOf("zspread").orElse(6).forGetter(config -> ((CobwebFeatureConfig) config).zspread)).apply(instance, CobwebFeatureConfig::new));
	public final BlockStateProvider blockProvider;
	public final int tries;
	public final int xspread;
	public final int yspread;
	public final int zspread;

	public CobwebFeatureConfig(BlockStateProvider blp, int t, int x, int y, int z) {
		blockProvider = blp;
		tries = t;
		xspread = x;
		yspread = y;
		zspread = z;
	}
}
