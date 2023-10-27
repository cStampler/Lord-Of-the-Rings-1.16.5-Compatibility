package lotr.common.world.gen.feature;

import java.util.Iterator;
import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class MordorBasaltFeature extends Feature {
	public MordorBasaltFeature(Codec codec) {
		super(codec);
	}

	private boolean canPlaceLavaHere(IWorld world, BlockPos pos) {
		if (!world.getFluidState(pos.above()).isEmpty()) {
			return false;
		}
		Iterator var3 = Plane.HORIZONTAL.iterator();

		Direction dir;
		do {
			if (!var3.hasNext()) {
				return true;
			}

			dir = (Direction) var3.next();
		} while (world.getBlockState(pos.relative(dir)).canOcclude());

		return false;
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		MordorBasaltFeatureConfig config = (MordorBasaltFeatureConfig) confi;
		int radius = config.radius.sample(rand);
		float density = MathHelper.nextFloat(rand, config.minDensity, config.maxDensity);
		float prominence = MathHelper.nextFloat(rand, config.minProminence, config.maxProminence);
		boolean lava = rand.nextFloat() < config.lavaChance;
		int numTries = (int) (radius * radius * 3.141592653589793D * density);
		Mutable movingPos = new Mutable().set(pos);

		label76: for (int l = 0; l < numTries; ++l) {
			int moveX = rand.nextFloat() < density ? 1 : 2;
			int moveZ = rand.nextFloat() < density ? 1 : 2;
			movingPos.move(MathHelper.nextInt(rand, -moveX, moveX), 0, MathHelper.nextInt(rand, -moveZ, moveZ));
			int dx = movingPos.getX() - pos.getX();
			int dz = movingPos.getZ() - pos.getZ();
			float rSq = (float) (dx * dx + dz * dz) / (float) (radius * radius);
			if (rSq > 1.0F) {
				movingPos.set(pos);
			}

			int topY = world.getHeight(Type.OCEAN_FLOOR_WG, movingPos.getX(), movingPos.getZ()) - 1;
			movingPos.setY(topY);
			boolean lavaHere = lava && rSq <= 0.25F && rand.nextInt(4) == 0 && canPlaceLavaHere(world, movingPos);
			int randDepth = config.depth.sample(rand);

			for (int d = 0; d < randDepth; ++d) {
				BlockState state = world.getBlockState(movingPos);
				if (!config.surfaceBlocks.contains(state.getBlock())) {
					if (d == 0) {
						continue label76;
					}
					break;
				}

				if (lavaHere && d == 0) {
					setBlock(world, movingPos, Blocks.LAVA.defaultBlockState());
				} else {
					setBlock(world, movingPos, Blocks.BASALT.defaultBlockState());
				}

				movingPos.move(Direction.DOWN);
			}

			if (!lavaHere && rand.nextFloat() < prominence) {
				BlockPos abovePos = new BlockPos(movingPos.getX(), topY + 1, movingPos.getZ());
				if (world.isEmptyBlock(abovePos)) {
					setBlock(world, abovePos, Blocks.BASALT.defaultBlockState());
				}
			}
		}

		return true;
	}
}
