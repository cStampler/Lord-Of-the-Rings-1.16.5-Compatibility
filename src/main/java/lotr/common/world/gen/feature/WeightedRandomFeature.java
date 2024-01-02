package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class WeightedRandomFeature<FC extends IFeatureConfig> extends Feature<WeightedRandomFeatureConfig<FC>> {
	public WeightedRandomFeature(Codec<WeightedRandomFeatureConfig<FC>> codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, WeightedRandomFeatureConfig<FC> config) {
		ConfiguredFeature<FC, ?> selectedFeature = config.getRandomFeature(rand);
	    return selectedFeature.place(world, generator, rand, pos);
	}
}
