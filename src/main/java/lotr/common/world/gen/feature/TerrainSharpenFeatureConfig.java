package lotr.common.world.gen.feature;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TerrainSharpenFeatureConfig implements IFeatureConfig {
	public static final Codec<TerrainSharpenFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockState.CODEC.listOf().fieldOf("block_targets").forGetter(config -> ImmutableList.copyOf(config.targetStates)),
			Codec.INT.fieldOf("min_height").orElse(1).forGetter(config -> config.minHeight),
			Codec.INT.fieldOf("max_height").orElse(3).forGetter(config -> config.maxHeight)
	).apply(instance, TerrainSharpenFeatureConfig::new));

	public final List<BlockState> targetStates;
	public final int minHeight;
	public final int maxHeight;

	public TerrainSharpenFeatureConfig(List<BlockState> states, int min, int max) {
		targetStates = states;
		minHeight = min;
		maxHeight = max;
	}
}
