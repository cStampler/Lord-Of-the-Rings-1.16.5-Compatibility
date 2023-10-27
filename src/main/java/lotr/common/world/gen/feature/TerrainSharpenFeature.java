package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class TerrainSharpenFeature extends Feature {
	public TerrainSharpenFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		TerrainSharpenFeatureConfig config = (TerrainSharpenFeatureConfig) confi;
		BlockState state = world.getBlockState(pos.below());
		if (!config.targetStates.contains(state)) {
			return false;
		}
		int height = MathHelper.nextInt(rand, config.minHeight, config.maxHeight);
		Mutable movingPos = new Mutable();

		for (int y = 0; y < height; ++y) {
			movingPos.set(pos).move(0, y, 0);
			if (world.getBlockState(movingPos).isSolidRender(world, movingPos)) {
				break;
			}

			world.setBlock(movingPos, state, 3);
		}

		return true;
	}
}
