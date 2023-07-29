package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.init.LOTRTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.*;

public class MordorMossFeature extends Feature {
	public MordorMossFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		MordorMossFeatureConfig config = (MordorMossFeatureConfig) confi;
		int numberOfMoss = MathHelper.nextInt(rand, config.minSize, config.maxSize);
		float randAngle = rand.nextFloat() * 3.1415927F;
		int x = pos.getX();
		int z = pos.getZ();
		int y = pos.getY();
		Mutable movingPos = new Mutable().set(pos);
		double d = x + 8 + MathHelper.sin(randAngle) * numberOfMoss / 8.0F;
		double d1 = x + 8 - MathHelper.sin(randAngle) * numberOfMoss / 8.0F;
		double d2 = z + 8 + MathHelper.cos(randAngle) * numberOfMoss / 8.0F;
		double d3 = z + 8 - MathHelper.cos(randAngle) * numberOfMoss / 8.0F;

		for (int l = 0; l <= numberOfMoss; ++l) {
			double d5 = d + (d1 - d) * l / numberOfMoss;
			double d6 = d2 + (d3 - d2) * l / numberOfMoss;
			double d7 = rand.nextDouble() * numberOfMoss / 16.0D;
			double d8 = (MathHelper.sin(l * 3.1415927F / numberOfMoss) + 1.0F) * d7 + 1.0D;
			int i1 = MathHelper.floor(d5 - d8 / 2.0D);
			int k1 = MathHelper.floor(d6 - d8 / 2.0D);
			int i2 = MathHelper.floor(d5 + d8 / 2.0D);
			int k2 = MathHelper.floor(d6 + d8 / 2.0D);

			for (int i3 = i1; i3 <= i2; ++i3) {
				double d9 = (i3 + 0.5D - d5) / (d8 / 2.0D);
				if (d9 * d9 < 1.0D) {
					for (int k3 = k1; k3 <= k2; ++k3) {
						int j1 = world.getHeight(Type.WORLD_SURFACE_WG, i3, k3);
						if (j1 == y) {
							double d10 = (k3 + 0.5D - d6) / (d8 / 2.0D);
							if (d9 * d9 + d10 * d10 < 1.0D) {
								movingPos.set(i3, j1, k3);
								BlockPos below = movingPos.below();
								if (world.isEmptyBlock(movingPos) && world.getBlockState(below).is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES)) {
									world.setBlock(movingPos, config.blockState, 2);
									world.setBlock(below, world.getBlockState(below).updateShape(Direction.UP, config.blockState, world, below, movingPos), 2);
								}
							}
						}
					}
				}
			}
		}

		return true;
	}
}
