package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;

public class LatitudeBasedFeature extends Feature {
	public LatitudeBasedFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		LatitudeBasedFeatureConfig config = (LatitudeBasedFeatureConfig) confi;
		LatitudeBasedFeatureConfig.LatitudeConfiguration latConfig = config.latitudeConfig;
		int z = pos.getZ();
		double latitudeProgressF = latConfig.type.getLatitudeProgress(z);
		if (latConfig.invert) {
			latitudeProgressF = 1.0D - latitudeProgressF;
		}

		if (latitudeProgressF > latConfig.max || latitudeProgressF < latConfig.min || rand.nextFloat() >= latitudeProgressF) {
			return false;
		}
		ConfiguredFeature feature = config.feature;
		return feature.place(world, generator, rand, pos);
	}
}
