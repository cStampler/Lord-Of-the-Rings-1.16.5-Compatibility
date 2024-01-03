package lotr.common.world.gen.feature;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class WeightedFeature<FC extends IFeatureConfig> {
	public static final Codec<WeightedFeature<IFeatureConfig>> CODEC= RecordCodecBuilder.create(instance -> instance.group(
			  ConfiguredFeature.CODEC.fieldOf("feature").forGetter(property -> property.feature), 
	  		Codec.INT.fieldOf("weight").orElse(Integer.valueOf(1)).forGetter(property -> property.weight)).apply(instance, WeightedFeature::make));
	public final Supplier<ConfiguredFeature<?, ?>> feature;
	public final int weight;

	private WeightedFeature(ConfiguredFeature<FC, ?> feat, int w) {
		this.feature = () -> feat;
		weight = w;
	}

	public static <FC extends IFeatureConfig> WeightedFeature<FC> make(Supplier<ConfiguredFeature<?, ?>> feature, int weight) {
	    return new WeightedFeature(feature.get(), weight);
	  }
}
