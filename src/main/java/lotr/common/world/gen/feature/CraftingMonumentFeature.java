package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.block.DoubleTorchBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;

public class CraftingMonumentFeature extends Feature {
	public CraftingMonumentFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		CraftingMonumentFeatureConfig config = (CraftingMonumentFeatureConfig) confi;
		if (!LOTRFeatures.isSurfaceBlock(world, pos.below())) {
			return false;
		}
		int up = 2 + rand.nextInt(3);
		BlockPos tablePos = pos.above(up);
		world.setBlock(tablePos, config.craftingTable, 2);
		Mutable movingPos = new Mutable();
		Mutable brickPos = new Mutable();
		int maxSteps = 8;
		int decoLayer = 2;

		for (int l = 1; l <= maxSteps; ++l) {
			boolean setAnyThisLayer = false;
			int y = -l;

			for (int x = -l; x <= l; ++x) {
				for (int z = -l; z <= l; ++z) {
					movingPos.set(tablePos).move(x, y, z);
					int maxY = movingPos.getY();
					int minY = maxY;
					if (l == maxSteps) {
						minY = 0;
					}

					brickPos.set(movingPos);

					BlockState post;
					for (int y1 = maxY; y1 >= minY; --y1) {
						brickPos.setY(y1);
						post = world.getBlockState(brickPos);
						if (post.isSolidRender(world, brickPos)) {
							break;
						}

						BlockState newState = LOTRFeatures.getBlockStateInContext(config.baseBlockProvider.getState(rand, brickPos), world, brickPos);
						world.setBlock(brickPos, newState, 2);
						LOTRFeatures.setGrassToDirtBelow(world, brickPos);
						setAnyThisLayer = true;
					}

					if (l == decoLayer && Math.abs(x) == decoLayer && Math.abs(z) == decoLayer) {
						BlockPos postPos = movingPos.above();
						post = config.postProvider.getState(rand, postPos);
						world.setBlock(postPos, post, 2);
						world.setBlock(postPos.above(), post, 2);
						BlockPos torchPos = postPos.above(2);
						BlockState torch = config.torchProvider.getState(rand, torchPos);
						if (torch.getBlock() instanceof DoubleTorchBlock) {
							((DoubleTorchBlock) torch.getBlock()).placeTorchAt(world, torchPos, 2);
						} else {
							world.setBlock(torchPos, torch, 2);
						}
					}
				}
			}

			if (!setAnyThisLayer && l >= decoLayer) {
				break;
			}
		}

		return true;
	}
}
