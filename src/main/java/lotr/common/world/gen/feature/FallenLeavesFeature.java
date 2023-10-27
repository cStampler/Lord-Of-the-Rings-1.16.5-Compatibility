package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.block.FallenLeavesBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class FallenLeavesFeature extends Feature {
	public FallenLeavesFeature(Codec codec) {
		super(codec);
	}

	private boolean canFallenLeavesReplaceBlockAt(ISeedReader world, Mutable movingPos) {
		BlockState currentState = world.getBlockState(movingPos);
		return currentState.getMaterial().isReplaceable() && currentState.getFluidState().isEmpty();
	}

	private void checkForFloatingTopHalfBlocksAbove(ISeedReader world, Mutable movingPos) {
		BlockPos abovePos = movingPos.above();
		BlockState aboveState = world.getBlockState(abovePos);
		if (!aboveState.canSurvive(world, abovePos)) {
			world.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 3);
		}

	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		BlockState fallenLeavesState = null;
		int searchTries = 40;
		int searchXRange = 6;
		int searchYRangeUp = 12;
		int searchZRange = 6;
		Mutable movingPos = new Mutable();

		for (int i = 0; i < searchTries; ++i) {
			int x = MathHelper.nextInt(rand, -searchXRange, searchXRange);
			int y = rand.nextInt(searchYRangeUp + 1);
			int z = MathHelper.nextInt(rand, -searchZRange, searchZRange);
			movingPos.set(pos).move(x, y, z);
			BlockState state = world.getBlockState(movingPos);
			Block fallenLeavesBlock = FallenLeavesBlock.getFallenLeavesFor(state.getBlock());
			if (fallenLeavesBlock != null) {
				fallenLeavesState = fallenLeavesBlock.defaultBlockState();
				break;
			}
		}

		if (fallenLeavesState == null) {
			return false;
		}
		int placeTries = 64;
		int placeXRange = 5;
		int placeYRange = 3;
		int placeZRange = 5;

		for (int i = 0; i < placeTries; ++i) {
			int x = MathHelper.nextInt(rand, -placeXRange, placeXRange);
			int y = MathHelper.nextInt(rand, -placeYRange, placeYRange);
			int z = MathHelper.nextInt(rand, -placeZRange, placeZRange);
			movingPos.set(pos).move(x, y, z);
			if (fallenLeavesState.canSurvive(world, movingPos) && canFallenLeavesReplaceBlockAt(world, movingPos)) {
				world.setBlock(movingPos, fallenLeavesState, 2);
				checkForFloatingTopHalfBlocksAbove(world, movingPos);
			}
		}

		return true;
	}
}
