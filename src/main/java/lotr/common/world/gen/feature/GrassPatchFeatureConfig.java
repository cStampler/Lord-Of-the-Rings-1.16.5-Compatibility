package lotr.common.world.gen.feature;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class GrassPatchFeatureConfig implements IFeatureConfig {
	public static final Codec<GrassPatchFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockState.CODEC.listOf().fieldOf("block_targets").forGetter(config -> ImmutableList.copyOf(config.targetStates)),
			Codec.INT.fieldOf("min_radius").orElse(1).forGetter(config -> config.minRadius),
			Codec.INT.fieldOf("max_radius").orElse(5).forGetter(config -> config.maxRadius),
			Codec.INT.fieldOf("min_depth").orElse(4).forGetter(config -> config.minDepth),
			Codec.INT.fieldOf("max_depth").orElse(5).forGetter(config -> config.maxDepth)
	).apply(instance, GrassPatchFeatureConfig::new));

	public final List<BlockState> targetStates;
	public final int minRadius;
	public final int maxRadius;
	public final int minDepth;
	public final int maxDepth;

	public GrassPatchFeatureConfig(List<BlockState> states, int rMin, int rMax, int dMin, int dMax) {
		targetStates = states;
		minRadius = rMin;
		maxRadius = rMax;
		minDepth = dMin;
		maxDepth = dMax;
	}
}
