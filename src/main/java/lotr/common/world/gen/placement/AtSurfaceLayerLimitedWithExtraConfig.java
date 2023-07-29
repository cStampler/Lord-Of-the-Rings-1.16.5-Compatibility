package lotr.common.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class AtSurfaceLayerLimitedWithExtraConfig implements IPlacementConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("count").forGetter(config -> ((AtSurfaceLayerLimitedWithExtraConfig) config).count), Codec.FLOAT.fieldOf("extra_chance").forGetter(config -> ((AtSurfaceLayerLimitedWithExtraConfig) config).extraChance), Codec.INT.fieldOf("extra_count").forGetter(config -> ((AtSurfaceLayerLimitedWithExtraConfig) config).extraCount), Codec.INT.fieldOf("layer_limit").forGetter(config -> ((AtSurfaceLayerLimitedWithExtraConfig) config).layerLimit), Codec.BOOL.fieldOf("is_upper_limit").orElse(true).forGetter(config -> ((AtSurfaceLayerLimitedWithExtraConfig) config).isLayerUpperLimit)).apply(instance, AtSurfaceLayerLimitedWithExtraConfig::new));
	public final int count;
	public final float extraChance;
	public final int extraCount;
	public final int layerLimit;
	public final boolean isLayerUpperLimit;

	public AtSurfaceLayerLimitedWithExtraConfig(int n, float chance, int extra, int layer, boolean isUpper) {
		count = n;
		extraChance = chance;
		extraCount = extra;
		layerLimit = layer;
		isLayerUpperLimit = isUpper;
	}
}
