package lotr.common.world.gen.feature;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.block.DripstoneBlock;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class DripstoneFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockState.CODEC.optionalFieldOf("forced_blockstate").forGetter(config -> Optional.ofNullable(((DripstoneFeatureConfig) config).forcedBlockState)), Codec.INT.fieldOf("tries").orElse(64).forGetter(config -> ((DripstoneFeatureConfig) config).tries), Codec.INT.fieldOf("xspread").orElse(8).forGetter(config -> ((DripstoneFeatureConfig) config).xspread), Codec.INT.fieldOf("yspread").orElse(4).forGetter(config -> ((DripstoneFeatureConfig) config).yspread), Codec.INT.fieldOf("zspread").orElse(8).forGetter(config -> ((DripstoneFeatureConfig) config).zspread), Codec.FLOAT.fieldOf("doubleChance").orElse(0.33F).forGetter(config -> ((DripstoneFeatureConfig) config).doubleChance)).apply(instance, DripstoneFeatureConfig::new));
	public final BlockState forcedBlockState;
	public final int tries;
	public final int xspread;
	public final int yspread;
	public final int zspread;
	public final float doubleChance;

	public DripstoneFeatureConfig(BlockState state, int t, int x, int y, int z, float dc) {
		forcedBlockState = state;
		tries = t;
		xspread = x;
		yspread = y;
		zspread = z;
		doubleChance = dc;
	}

	public DripstoneFeatureConfig(int t, int x, int y, int z, float dc) {
		this((BlockState) null, t, x, y, z, dc);
	}

	private DripstoneFeatureConfig(Optional state, int t, int x, int y, int z, float dc) {
		this((BlockState) state.orElse((Object) null), t, x, y, z, dc);
	}

	public boolean hasForcedDripstoneState() {
		return forcedBlockState != null && forcedBlockState.getBlock() instanceof DripstoneBlock;
	}
}
