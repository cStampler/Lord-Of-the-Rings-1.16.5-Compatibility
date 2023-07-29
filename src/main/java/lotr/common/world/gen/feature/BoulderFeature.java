package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.world.map.RoadPointCache;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.*;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.*;

public class BoulderFeature extends Feature {
	public BoulderFeature(Codec codec) {
		super(codec);
	}

	private boolean isSurfaceBlock(IWorld world, BlockPos pos) {
		return LOTRFeatures.isSurfaceBlock(world, pos);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		BoulderFeatureConfig config = (BoulderFeatureConfig) confi;
		world.getBiome(pos);
		int boulderWidth = MathHelper.nextInt(rand, config.minWidth, config.maxWidth);
		if (!RoadPointCache.checkNotGeneratingWithinRangeOfRoad(world, pos, boulderWidth)) {
			return false;
		}
		int highestHeight = pos.getY();
		int lowestHeight = highestHeight;

		int spheres;
		int heightValue;
		for (int i = -boulderWidth; i <= boulderWidth; ++i) {
			for (spheres = -boulderWidth; spheres <= boulderWidth; ++spheres) {
				BlockPos heightPos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, pos.offset(i, 0, spheres));
				if (!isSurfaceBlock(world, heightPos.below())) {
					return false;
				}

				heightValue = heightPos.getY();
				if (heightValue > highestHeight) {
					highestHeight = heightValue;
				}

				if (heightValue < lowestHeight) {
					lowestHeight = heightValue;
				}
			}
		}

		if (highestHeight - lowestHeight > config.heightCheck) {
			return false;
		}
		Mutable movingPos = new Mutable();
		spheres = MathHelper.nextInt(rand, 1, Math.max(1, boulderWidth));

		for (int l = 0; l < spheres; ++l) {
			heightValue = MathHelper.nextInt(rand, -boulderWidth, boulderWidth);
			int zOffset = MathHelper.nextInt(rand, -boulderWidth, boulderWidth);
			BlockPos boulderPos = world.getHeightmapPos(Type.WORLD_SURFACE_WG, pos.offset(heightValue, 0, zOffset));
			int sphereWidth = MathHelper.nextInt(rand, config.minWidth, config.maxWidth);

			for (int i = -sphereWidth; i <= sphereWidth; ++i) {
				for (int j = -sphereWidth; j <= sphereWidth; ++j) {
					for (int k = -sphereWidth; k <= sphereWidth; ++k) {
						int dist = i * i + j * j + k * k;
						if (dist < sphereWidth * sphereWidth || dist < (sphereWidth + 1) * (sphereWidth + 1) && rand.nextInt(3) == 0) {
							movingPos.set(boulderPos.offset(i, j, k));

							for (BlockPos below = movingPos.below(); movingPos.getY() >= 0 && !world.getBlockState(below).isSolidRender(world, below); below = below.below()) {
								movingPos.set(below);
							}

							world.setBlock(movingPos, config.stateProvider.getState(rand, movingPos), 3);
							LOTRFeatures.setGrassToDirtBelow(world, movingPos);
						}
					}
				}
			}
		}

		return true;
	}
}
