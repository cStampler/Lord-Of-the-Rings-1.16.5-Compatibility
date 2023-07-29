package lotr.common.block.trees;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.server.ServerWorld;

public abstract class PartyableCrossBigTree extends CrossBigTree {
	private final PartyTreeLogic partyTree = new PartyTreeLogic(this::getPartyTreeFeature);

	protected abstract ConfiguredFeature getPartyTreeFeature(Random var1);

	@Override
	public boolean growTree(ServerWorld world, ChunkGenerator chunkGen, BlockPos pos, BlockState sapling, Random rand) {
		return partyTree.attemptGrowPartyTree(world, chunkGen, pos, sapling, rand) ? true : super.growTree(world, chunkGen, pos, sapling, rand);
	}
}
