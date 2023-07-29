package lotr.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MordorMossFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockState.CODEC.fieldOf("state").forGetter(config -> ((MordorMossFeatureConfig) config).blockState), Codec.INT.fieldOf("min_size").orElse(32).forGetter(config -> ((MordorMossFeatureConfig) config).minSize), Codec.INT.fieldOf("max_size").orElse(80).forGetter(config -> ((MordorMossFeatureConfig) config).maxSize)).apply(instance, MordorMossFeatureConfig::new));
	public final BlockState blockState;
	public final int minSize;
	public final int maxSize;

	public MordorMossFeatureConfig(BlockState state, int min, int max) {
		blockState = state;
		minSize = min;
		maxSize = max;
	}
}
