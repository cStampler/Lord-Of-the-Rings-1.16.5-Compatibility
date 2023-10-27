package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.LOTRLog;
import lotr.common.block.ReedsBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ReedsFeature extends Feature {
	public ReedsFeature(Codec codec) {
		super(codec);
	}

	private boolean canReedsReplaceAt(IWorld world, BlockPos pos) {
		BlockState existingState = world.getBlockState(pos);
		return existingState.isAir(world, pos) || existingState.getMaterial().isReplaceable() || existingState.getBlock() == Blocks.WATER;
	}

	private boolean isWaterlogged(IWorld world, BlockPos pos) {
		return world.getFluidState(pos).getType() == Fluids.WATER;
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		ReedsFeatureConfig config = (ReedsFeatureConfig) confi;
		Mutable movingPos = new Mutable();

		for (int l = 0; l < config.tries; ++l) {
			int x = pos.getX() - rand.nextInt(config.xspread) + rand.nextInt(config.xspread);
			int y = pos.getY() - rand.nextInt(config.yspread) + rand.nextInt(config.yspread);
			int z = pos.getZ() - rand.nextInt(config.zspread) + rand.nextInt(config.zspread);
			movingPos.set(x, y, z);
			Block reedBlock = config.blockProvider.getState(rand, movingPos).getBlock();
			if (!(reedBlock instanceof ReedsBlock)) {
				LOTRLog.warn("Attempted to generate non-reed block in a reeds feature (block: %s, position: [%d %d %d])", reedBlock.getRegistryName(), x, y, z);
			} else {
				BlockState baseState = reedBlock.defaultBlockState();
				baseState = baseState.setValue(ReedsBlock.WATERLOGGED, isWaterlogged(world, movingPos));
				if (canReedsReplaceAt(world, movingPos) && baseState.canSurvive(world, movingPos)) {
					boolean threeHigh = rand.nextFloat() < config.fullyGrownChance;
					boolean placedThreeHigh = false;
					BlockPos abovePos;
					if (threeHigh) {
						abovePos = movingPos.above();
						BlockPos twoAbovePos = abovePos.above();
						if ((world.isEmptyBlock(abovePos) || world.getFluidState(abovePos).getType() == Fluids.WATER) && world.isEmptyBlock(twoAbovePos)) {
							placeAppropriateReedState(world, movingPos, baseState, ReedsBlock.Type.THREE_BOTTOM);
							placeAppropriateReedState(world, abovePos, baseState, ReedsBlock.Type.THREE_MIDDLE);
							placeAppropriateReedState(world, twoAbovePos, baseState, ReedsBlock.Type.THREE_TOP);
							placedThreeHigh = true;
						}
					}

					if (!placedThreeHigh) {
						abovePos = movingPos.above();
						if (world.isEmptyBlock(abovePos) || world.getFluidState(abovePos).getType() == Fluids.WATER) {
							placeAppropriateReedState(world, movingPos, baseState, ReedsBlock.Type.TWO_BOTTOM);
							placeAppropriateReedState(world, abovePos, baseState, ReedsBlock.Type.TWO_TOP);
						}
					}
				}
			}
		}

		return true;
	}

	private void placeAppropriateReedState(IWorld world, BlockPos pos, BlockState baseState, ReedsBlock.Type type) {
		world.setBlock(pos, baseState.setValue(ReedsBlock.REEDS_TYPE, type).setValue(ReedsBlock.WATERLOGGED, isWaterlogged(world, pos)), 2);
	}
}
