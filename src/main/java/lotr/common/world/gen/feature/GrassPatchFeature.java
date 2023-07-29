package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.*;

public class GrassPatchFeature extends Feature {
	public GrassPatchFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		GrassPatchFeatureConfig config = (GrassPatchFeatureConfig) confi;
		BlockState below = world.getBlockState(pos.below());
		if (!config.targetStates.contains(below)) {
			return false;
		}
		int r = MathHelper.nextInt(rand, config.minRadius, config.maxRadius);
		int depth = MathHelper.nextInt(rand, config.minDepth, config.maxDepth);
		Mutable movingPos = new Mutable();

		for (int x = -r; x <= r; ++x) {
			for (int z = -r; z <= r; ++z) {
				movingPos.set(pos).move(x, 0, z);
				if (x * x + z * z < r * r) {
					BlockPos heightPos = world.getHeightmapPos(Type.MOTION_BLOCKING, movingPos);
					if (Math.abs(heightPos.getY() - pos.getY()) <= 3) {
						boolean coarse = rand.nextInt(5) == 0;

						for (int y = 0; y < depth; ++y) {
							movingPos.set(heightPos).move(0, -y - 1, 0);
							BlockState state = world.getBlockState(movingPos);
							if (!config.targetStates.contains(state)) {
								break;
							}

							if (coarse) {
								world.setBlock(movingPos, Blocks.COARSE_DIRT.defaultBlockState(), 3);
							} else if (y == 0) {
								world.setBlock(movingPos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
							} else {
								world.setBlock(movingPos, Blocks.DIRT.defaultBlockState(), 3);
							}
						}
					}
				}
			}
		}

		return true;
	}
}
