package lotr.common.world.gen.feature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MordorBasaltFeatureConfig implements IFeatureConfig {
	public static final Codec<MordorBasaltFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(BlockState.CODEC.listOf().fieldOf("surface_blocks").forGetter(config ->
							((List<BlockState>) config.surfaceBlocks.stream().map(hummel -> ((BlockState) hummel).getBlockState()).collect(Collectors.toList()))
					),
					FeatureSpread.codec(1, 16, 15).fieldOf("radius").forGetter(config -> config.radius),
					FeatureSpread.codec(1, 6, 5).fieldOf("depth").forGetter(config -> config.depth),
					Codec.floatRange(0.0F, 1.0F).fieldOf("min_density").orElse(0.4F).forGetter(config -> config.minDensity),
					Codec.floatRange(0.0F, 1.0F).fieldOf("max_density").orElse(0.95F).forGetter(config -> config.minDensity),
					Codec.floatRange(0.0F, 1.0F).fieldOf("min_prominence").orElse(0.0F).forGetter(config -> config.minProminence),
					Codec.floatRange(0.0F, 1.0F).fieldOf("max_prominence").orElse(0.3F).forGetter(config -> config.maxProminence),
					Codec.floatRange(0.0F, 1.0F).fieldOf("lava_chance").orElse(0.2F).forGetter(config -> config.lavaChance)
			).apply(instance, (blocks, radius, depth, minDensity, maxDensity, minProminence, maxProminence, lavaChance) ->
					new MordorBasaltFeatureConfig(new HashSet<>(blocks), radius, depth, minDensity, maxDensity, minProminence, maxProminence, lavaChance)
			)
	);

	public final Set<BlockState> surfaceBlocks;
	public final FeatureSpread radius;
	public final FeatureSpread depth;
	public final float minDensity;
	public final float maxDensity;
	public final float minProminence;
	public final float maxProminence;
	public final float lavaChance;

	public MordorBasaltFeatureConfig(Set<BlockState> surfaceBlocks, FeatureSpread radius, FeatureSpread depth, float minDensity, float maxDensity, float minProminence, float maxProminence, float lavaChance) {
		this.surfaceBlocks = surfaceBlocks;
		this.radius = radius;
		this.depth = depth;
		this.minDensity = minDensity;
		this.maxDensity = maxDensity;
		this.minProminence = minProminence;
		this.maxProminence = maxProminence;
		this.lavaChance = lavaChance;
	}
}
