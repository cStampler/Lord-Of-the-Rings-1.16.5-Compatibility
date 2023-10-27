package lotr.common.block.trees;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public class PartyTreeLogic {
	private final PartyTreeLogic.PartyTreeProvider partyTreeProvider;

	public PartyTreeLogic(PartyTreeLogic.PartyTreeProvider provider) {
		partyTreeProvider = provider;
	}

	protected boolean attemptGrowPartyTree(ServerWorld world, ChunkGenerator chunkGen, BlockPos pos, BlockState sapling, Random rand) {
		int searchRange = 2;

		for (int x = searchRange; x >= -searchRange; --x) {
			for (int z = searchRange; z >= -searchRange; --z) {
				if (canPartyTreeSpawnAt(sapling, world, pos, x, z)) {
					return growPartyTree(world, chunkGen, pos, sapling, rand, x, z);
				}
			}
		}

		return false;
	}

	private boolean canPartyTreeSpawnAt(BlockState sapling, IBlockReader world, BlockPos pos, int xOffset, int zOffset) {
		Block saplingBlock = sapling.getBlock();
		BlockPos offsetPos = pos.offset(xOffset, 0, zOffset);
		AtomicBoolean anyNotSaplings = new AtomicBoolean(false);
		doForSaplingGrid(offsetPos, saplingPos -> {
			if (!anyNotSaplings.get() && world.getBlockState((BlockPos) saplingPos).getBlock() != saplingBlock) {
				anyNotSaplings.set(true);
			}

		});
		return !anyNotSaplings.get();
	}

	private void doForSaplingGrid(BlockPos centralPos, Consumer action) {
		Mutable movingPos = new Mutable();

		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				movingPos.setWithOffset(centralPos, x, 0, z);
				action.accept(movingPos);
			}
		}

	}

	private boolean growPartyTree(ServerWorld world, ChunkGenerator chunkGen, BlockPos pos, BlockState sapling, Random rand, int xOffset, int zOffset) {
		ConfiguredFeature partyTree = partyTreeProvider.getPartyTreeFeature(rand);
		if (partyTree == null) {
			return false;
		}
		((BaseTreeFeatureConfig) partyTree.config).setFromSapling();
		BlockPos offsetPos = pos.offset(xOffset, 0, zOffset);
		BlockState air = Blocks.AIR.defaultBlockState();
		doForSaplingGrid(offsetPos, saplingPos -> {
			world.setBlock((BlockPos) saplingPos, air, 4);
		});
		if (partyTree.place(world, chunkGen, rand, offsetPos)) {
			return true;
		}
		doForSaplingGrid(offsetPos, saplingPos -> {
			world.setBlock((BlockPos) saplingPos, sapling, 4);
		});
		return false;
	}

	@FunctionalInterface
	public interface PartyTreeProvider {
		ConfiguredFeature getPartyTreeFeature(Random var1);
	}
}
