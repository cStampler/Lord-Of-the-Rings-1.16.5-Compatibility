package lotr.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.ConfiguredFeature;

public class WeightedFeature {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(ConfiguredFeature.DIRECT_CODEC.fieldOf("feature").forGetter(config -> ((WeightedFeature) config).feature), Codec.INT.fieldOf("weight").orElse(1).forGetter(config -> ((WeightedFeature) config).weight)).apply(instance, WeightedFeature::make));
	public final ConfiguredFeature feature;
	public final int weight;

	private WeightedFeature(ConfiguredFeature feat, int w) {
		feature = feat;
		weight = w;
	}

	public static WeightedFeature make(ConfiguredFeature feature, int weight) {
		return new WeightedFeature(feature, weight);
	}
}
