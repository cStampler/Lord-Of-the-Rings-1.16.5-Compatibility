package lotr.common.world.gen.feature;

import java.util.Map;
import java.util.function.IntToDoubleFunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.util.LOTRUtil;
import lotr.common.world.map.*;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.gen.feature.*;

public class LatitudeBasedFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(ConfiguredFeature.DIRECT_CODEC.fieldOf("feature").forGetter(config -> ((LatitudeBasedFeatureConfig) config).feature), LatitudeBasedFeatureConfig.LatitudeConfiguration.CODEC.fieldOf("latitude_config").forGetter(config -> ((LatitudeBasedFeatureConfig) config).latitudeConfig)).apply(instance, (h1, h2) -> new LatitudeBasedFeatureConfig((ConfiguredFeature) h1, (LatitudeConfiguration) h2)));
	public final ConfiguredFeature feature;
	public final LatitudeBasedFeatureConfig.LatitudeConfiguration latitudeConfig;

	public LatitudeBasedFeatureConfig(ConfiguredFeature cf, LatitudeBasedFeatureConfig.LatitudeConfiguration lat) {
		feature = cf;
		latitudeConfig = lat;
	}

	public static class LatitudeConfiguration {
		public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(LatitudeBasedFeatureConfig.LatitudeValuesType.CODEC.fieldOf("latitude_type").forGetter(config -> ((LatitudeConfiguration) config).type), Codec.BOOL.fieldOf("invert").orElse(false).forGetter(config -> ((LatitudeConfiguration) config).invert), Codec.floatRange(0.0F, 1.0F).fieldOf("proportional_min").orElse(0.0F).forGetter(config -> ((LatitudeConfiguration) config).min), Codec.floatRange(0.0F, 1.0F).fieldOf("proportional_max").orElse(1.0F).forGetter(config -> ((LatitudeConfiguration) config).max)).apply(instance, (h1, h2, h3, h4) -> new LatitudeConfiguration((LatitudeValuesType) h1, (boolean) h2, (float) h3, (float) h4)));
		public final LatitudeBasedFeatureConfig.LatitudeValuesType type;
		public final boolean invert;
		public final float min;
		public final float max;

		private LatitudeConfiguration(LatitudeBasedFeatureConfig.LatitudeValuesType type, boolean invert, float min, float max) {
			this.type = type;
			this.invert = invert;
			this.min = min;
			this.max = max;
		}

		public LatitudeBasedFeatureConfig.LatitudeConfiguration max(float newMax) {
			return new LatitudeBasedFeatureConfig.LatitudeConfiguration(type, invert, min, newMax);
		}

		public LatitudeBasedFeatureConfig.LatitudeConfiguration min(float newMin) {
			return new LatitudeBasedFeatureConfig.LatitudeConfiguration(type, invert, newMin, max);
		}

		public static LatitudeBasedFeatureConfig.LatitudeConfiguration of(LatitudeBasedFeatureConfig.LatitudeValuesType type) {
			return of(type, false);
		}

		public static LatitudeBasedFeatureConfig.LatitudeConfiguration of(LatitudeBasedFeatureConfig.LatitudeValuesType type, boolean invert) {
			return new LatitudeBasedFeatureConfig.LatitudeConfiguration(type, invert, 0.0F, 1.0F);
		}

		public static LatitudeBasedFeatureConfig.LatitudeConfiguration ofInverted(LatitudeBasedFeatureConfig.LatitudeValuesType type) {
			return of(type, true);
		}
	}

	public enum LatitudeValuesType implements IStringSerializable {
		ICE("ice", z -> ((double) waterSettings().getIceCoverageForLatitude(z))), SAND("sand", z -> ((double) waterSettings().getSandCoverageForLatitude(z))), CORAL("coral", z -> ((double) waterSettings().getCoralForLatitude(z)));

		public static final Codec CODEC = IStringSerializable.fromEnum(LatitudeBasedFeatureConfig.LatitudeValuesType::values, LatitudeBasedFeatureConfig.LatitudeValuesType::forName);
		private static final Map NAME_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), hummel -> ((LatitudeBasedFeatureConfig.LatitudeValuesType) hummel).getSerializedName());
		private final String code;
		private final IntToDoubleFunction zProgressGetter;

		LatitudeValuesType(String s, IntToDoubleFunction zProgress) {
			code = s;
			zProgressGetter = zProgress;
		}

		public double getLatitudeProgress(int z) {
			return zProgressGetter.applyAsDouble(z);
		}

		@Override
		public String getSerializedName() {
			return code;
		}

		public static LatitudeBasedFeatureConfig.LatitudeValuesType forName(String name) {
			return (LatitudeBasedFeatureConfig.LatitudeValuesType) NAME_LOOKUP.get(name);
		}

		private static BothWaterLatitudeSettings waterSettings() {
			return MapSettingsManager.serverInstance().getCurrentLoadedMap().getWaterLatitudes();
		}
	}
}
