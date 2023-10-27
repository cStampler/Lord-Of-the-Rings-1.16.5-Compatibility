package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class WrappedTreeFeature extends Feature {
	public WrappedTreeFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		WrappedTreeFeatureConfig config = (WrappedTreeFeatureConfig) confi;
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		boolean setAlternativeSoil = config.alternativeSoilType.testTerrain(belowState);
		if (setAlternativeSoil) {
			world.setBlock(belowPos, Blocks.DIRT.defaultBlockState(), 1);
		}

		ConfiguredFeature tree = Feature.TREE.configured(config.treeConfig);
		boolean generated = tree.place(world, generator, rand, pos);
		if (setAlternativeSoil) {
			world.setBlock(belowPos, belowState, 1);
		}

		return generated;
	}
}
