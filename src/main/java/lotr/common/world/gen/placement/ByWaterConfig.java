package lotr.common.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.gen.placement.IPlacementConfig;

public class ByWaterConfig implements IPlacementConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("range").forGetter(config -> ((ByWaterConfig) config).range), Codec.INT.fieldOf("tries").forGetter(config -> ((ByWaterConfig) config).tries)).apply(instance, ByWaterConfig::new));
	public final int range;
	public final int tries;

	public ByWaterConfig(int range, int tries) {
		this.range = range;
		this.tries = tries;
	}
}
