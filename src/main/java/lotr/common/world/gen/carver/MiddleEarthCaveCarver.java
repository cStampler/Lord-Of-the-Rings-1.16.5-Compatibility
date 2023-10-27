package lotr.common.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.mojang.serialization.Codec;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.carver.CaveWorldCarver;

public class MiddleEarthCaveCarver extends CaveWorldCarver {
	private Set landOnlyCarvables;

	public MiddleEarthCaveCarver(Codec codec, int height) {
		super(codec, height);
		replaceableBlocks = LOTRWorldCarvers.listCarvableBlocks();
		landOnlyCarvables = LOTRWorldCarvers.listLandOnlyCarvableBlocks();
	}

	@Override
	protected boolean canReplaceBlock(BlockState state, BlockState aboveState) {
		Block block = state.getBlock();
		return this.canReplaceBlock(state) || landOnlyCarvables.contains(block) && !aboveState.getFluidState().is(FluidTags.WATER);
	}

	@Override
	protected boolean carveBlock(IChunk chunk, Function biomeGetter, BitSet carvingMask, Random rand, Mutable movingPos, Mutable movingPosAbove, Mutable movingPosBelow, int seaLevel, int chunkX, int chunkZ, int x, int z, int xInChunk, int y, int zInChunk, MutableBoolean isSurface) {
		boolean flag = super.carveBlock(chunk, biomeGetter, carvingMask, rand, movingPos, movingPosAbove, movingPosBelow, seaLevel, chunkX, chunkZ, x, z, xInChunk, y, zInChunk, isSurface);
		if (flag) {
			changeOtherBlocksAboveAndBelow(chunk, movingPos, movingPosAbove, movingPosBelow);
		}

		return flag;
	}

	protected static void changeOtherBlocksAboveAndBelow(IChunk chunk, Mutable movingPos, Mutable movingPosAbove, Mutable movingPosBelow) {
		movingPosBelow.set(movingPos).move(Direction.DOWN);
		if (chunk.getBlockState(movingPosBelow).getBlock() == LOTRBlocks.DIRTY_CHALK.get()) {
			chunk.setBlockState(movingPosBelow, ((Block) LOTRBlocks.CHALK.get()).defaultBlockState(), false);
		}

	}
}
