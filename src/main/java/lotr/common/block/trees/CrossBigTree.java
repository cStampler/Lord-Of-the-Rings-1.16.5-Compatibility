package lotr.common.block.trees;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public abstract class CrossBigTree extends Tree {
	protected abstract ConfiguredFeature getCrossTreeFeature(Random var1);

	private boolean growCrossTree(ServerWorld world, ChunkGenerator chunkGen, BlockPos pos, BlockState sapling, Random rand, int xOffset, int zOffset) {
		ConfiguredFeature crossTree = getCrossTreeFeature(rand);
		if (crossTree == null) {
			return false;
		}
		BlockState air = Blocks.AIR.defaultBlockState();
		world.setBlock(pos.offset(xOffset, 0, zOffset), air, 4);
		world.setBlock(pos.offset(xOffset - 1, 0, zOffset), air, 4);
		world.setBlock(pos.offset(xOffset + 1, 0, zOffset), air, 4);
		world.setBlock(pos.offset(xOffset, 0, zOffset - 1), air, 4);
		world.setBlock(pos.offset(xOffset, 0, zOffset + 1), air, 4);
		if (crossTree.place(world, chunkGen, rand, pos.offset(xOffset, 0, zOffset))) {
			return true;
		}
		world.setBlock(pos.offset(xOffset, 0, zOffset), sapling, 4);
		world.setBlock(pos.offset(xOffset - 1, 0, zOffset), sapling, 4);
		world.setBlock(pos.offset(xOffset + 1, 0, zOffset), sapling, 4);
		world.setBlock(pos.offset(xOffset, 0, zOffset - 1), sapling, 4);
		world.setBlock(pos.offset(xOffset, 0, zOffset + 1), sapling, 4);
		return false;
	}

	@Override
	public boolean growTree(ServerWorld world, ChunkGenerator chunkGen, BlockPos pos, BlockState sapling, Random rand) {
		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {
				if ((x == 0 || z == 0) && canCrossTreeSpawnAt(sapling, world, pos.offset(x, 0, z))) {
					return growCrossTree(world, chunkGen, pos, sapling, rand, x, z);
				}
			}
		}

		return super.growTree(world, chunkGen, pos, sapling, rand);
	}

	public static boolean canCrossTreeSpawnAt(BlockState sapling, IBlockReader world, BlockPos pos) {
		Block block = sapling.getBlock();

		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				if ((x == 0 || z == 0) && block != world.getBlockState(pos.offset(x, 0, z)).getBlock()) {
					return false;
				}
			}
		}

		return true;
	}
}
