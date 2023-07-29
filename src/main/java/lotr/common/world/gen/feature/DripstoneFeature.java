package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.block.DripstoneBlock;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.*;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;

public class DripstoneFeature extends Feature {
	public DripstoneFeature(Codec codec) {
		super(codec);
	}

	private boolean canDripstoneReplace(BlockState state, IWorld world, BlockPos pos) {
		if (world.isEmptyBlock(pos)) {
			return true;
		}
		if (world.getBlockState(pos).getBlock() != Blocks.WATER) {
			return false;
		}
		Block block = state.getBlock();
		return block instanceof DripstoneBlock && ((DripstoneBlock) block).isWaterloggable;
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		DripstoneFeatureConfig config = (DripstoneFeatureConfig) confi;
		boolean placedAny = false;
		Mutable movingPos = new Mutable();

		for (int l = 0; l < config.tries; ++l) {
			int x = pos.getX() - rand.nextInt(config.xspread) + rand.nextInt(config.xspread);
			int y = pos.getY() - rand.nextInt(config.yspread) + rand.nextInt(config.yspread);
			int z = pos.getZ() - rand.nextInt(config.zspread) + rand.nextInt(config.zspread);
			movingPos.set(x, y, z);
			Block above = world.getBlockState(movingPos.above()).getBlock();
			Block below = world.getBlockState(movingPos.below()).getBlock();
			boolean waterlogged = world.getFluidState(movingPos).getType() == Fluids.WATER;
			boolean isForcedBlock = config.hasForcedDripstoneState();
			boolean canPlaceStalac = isForcedBlock ? config.forcedBlockState.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE).canSurvive(world, movingPos) : DripstoneBlock.BLOCK_TO_DRIPSTONE.containsKey(above);
			boolean canPlaceStalag = isForcedBlock ? config.forcedBlockState.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE).canSurvive(world, movingPos) : DripstoneBlock.BLOCK_TO_DRIPSTONE.containsKey(below);
			boolean placed = false;
			BlockState stalag;
			BlockState placeState;
			BlockState placeStateUp;
			boolean waterloggedUp;
			if (canPlaceStalac) {
				stalag = isForcedBlock ? config.forcedBlockState : ((DripstoneBlock) DripstoneBlock.BLOCK_TO_DRIPSTONE.get(above)).defaultBlockState();
				if (canDripstoneReplace(stalag, world, movingPos)) {
					if (rand.nextFloat() < config.doubleChance && canDripstoneReplace(stalag, world, movingPos.below())) {
						waterloggedUp = world.getFluidState(movingPos.below()).getType() == Fluids.WATER;
						placeState = waterlogIfApplicable(stalag.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE_DOUBLE_BASE), waterlogged);
						placeStateUp = waterlogIfApplicable(stalag.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE_DOUBLE_POINT), waterloggedUp);
						world.setBlock(movingPos, placeState, 2);
						world.setBlock(movingPos.below(), placeStateUp, 2);
					} else {
						placeState = waterlogIfApplicable(stalag.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALACTITE), waterlogged);
						world.setBlock(movingPos, placeState, 2);
					}
					placed = true;
				}
			}

			if (!placed && canPlaceStalag) {
				stalag = isForcedBlock ? config.forcedBlockState : ((DripstoneBlock) DripstoneBlock.BLOCK_TO_DRIPSTONE.get(below)).defaultBlockState();
				if (canDripstoneReplace(stalag, world, movingPos)) {
					if (rand.nextFloat() < config.doubleChance && canDripstoneReplace(stalag, world, movingPos.above())) {
						waterloggedUp = world.getFluidState(movingPos.above()).getType() == Fluids.WATER;
						placeState = waterlogIfApplicable(stalag.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE_DOUBLE_BASE), waterlogged);
						placeStateUp = waterlogIfApplicable(stalag.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE_DOUBLE_POINT), waterloggedUp);
						world.setBlock(movingPos, placeState, 2);
						world.setBlock(movingPos.above(), placeStateUp, 2);
					} else {
						placeState = waterlogIfApplicable(stalag.setValue(DripstoneBlock.DRIPSTONE_TYPE, DripstoneBlock.Type.STALAGMITE), waterlogged);
						world.setBlock(movingPos, placeState, 2);
					}
					placed = true;
				}
			}

			placedAny |= placed;
		}

		return placedAny;
	}

	private BlockState waterlogIfApplicable(BlockState state, boolean waterlogged) {
		Block block = state.getBlock();
		if (block instanceof DripstoneBlock && ((DripstoneBlock) block).isWaterloggable) {
			state = state.setValue(DripstoneBlock.WATERLOGGED, waterlogged);
		}

		return state;
	}
}
