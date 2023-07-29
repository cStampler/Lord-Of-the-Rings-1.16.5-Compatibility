package lotr.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class BoulderFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(config -> ((BoulderFeatureConfig) config).stateProvider), Codec.INT.fieldOf("min_width").orElse(1).forGetter(config -> ((BoulderFeatureConfig) config).minWidth), Codec.INT.fieldOf("max_width").orElse(3).forGetter(config -> ((BoulderFeatureConfig) config).maxWidth), Codec.INT.fieldOf("height_check").orElse(3).forGetter(config -> ((BoulderFeatureConfig) config).heightCheck)).apply(instance, BoulderFeatureConfig::new));
	public final BlockStateProvider stateProvider;
	public final int minWidth;
	public final int maxWidth;
	public final int heightCheck;

	public BoulderFeatureConfig(BlockStateProvider blockProv, int min, int max) {
		this(blockProv, min, max, 3);
	}

	public BoulderFeatureConfig(BlockStateProvider blockProv, int min, int max, int hCheck) {
		stateProvider = blockProv;
		minWidth = min;
		maxWidth = max;
		heightCheck = hCheck;
	}
}
