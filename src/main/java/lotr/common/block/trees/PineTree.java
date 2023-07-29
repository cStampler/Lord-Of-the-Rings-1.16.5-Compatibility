package lotr.common.block.trees;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRBiomes;
import lotr.common.world.biome.*;
import net.minecraft.block.*;
import net.minecraft.block.trees.Tree;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PineTree extends Tree {
	@Override
	protected ConfiguredFeature getConfiguredFeature(Random rand, boolean bees) {
		return LOTRBiomeFeatures.pine();
	}

	protected ConfiguredFeature getTreeFeatureShire(Random rand, boolean bees) {
		return LOTRBiomeFeatures.shirePine();
	}

	@Override
	public boolean growTree(ServerWorld world, ChunkGenerator chunkGen, BlockPos pos, BlockState state, Random rand) {
		boolean nearbyFlowers = false;

		try {
			nearbyFlowers = (Boolean) ObfuscationReflectionHelper.findMethod(Tree.class, "hasFlowers", IWorld.class, BlockPos.class).invoke(this, world, pos);
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var10) {
			LOTRLog.error("Error determining nearby flowers for Pine tree growth");
			var10.printStackTrace();
		}

		Biome biome = world.getBiome(pos);
		boolean isShire = LOTRBiomes.getWrapperFor(biome, world) instanceof ShireBiome;
		ConfiguredFeature treeGen = isShire ? getTreeFeatureShire(rand, nearbyFlowers) : getConfiguredFeature(rand, nearbyFlowers);
		if (treeGen == null) {
			return false;
		}
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 4);
		((BaseTreeFeatureConfig) treeGen.config).setFromSapling();
		if (treeGen.place(world, chunkGen, rand, pos)) {
			return true;
		}
		world.setBlock(pos, state, 4);
		return false;
	}
}
