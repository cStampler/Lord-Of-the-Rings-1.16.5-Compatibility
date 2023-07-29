package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;

public class CobwebFeature extends Feature {
	public CobwebFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		CobwebFeatureConfig config = (CobwebFeatureConfig) confi;
		Mutable movingPos = new Mutable();
		Mutable adjPos = new Mutable();

		for (int l = 0; l < config.tries; ++l) {
			int x = pos.getX() - rand.nextInt(config.xspread) + rand.nextInt(config.xspread);
			int y = pos.getY() - rand.nextInt(config.yspread) + rand.nextInt(config.yspread);
			int z = pos.getZ() - rand.nextInt(config.zspread) + rand.nextInt(config.zspread);
			movingPos.set(x, y, z);
			if (world.isEmptyBlock(movingPos)) {
				boolean anyStoneAdj = false;
				Direction[] var13 = Direction.values();
				int var14 = var13.length;

				for (int var15 = 0; var15 < var14; ++var15) {
					Direction dir = var13[var15];
					adjPos.set(movingPos.relative(dir, 1));
					BlockState adjState = world.getBlockState(adjPos);
					if (adjState.isSolidRender(world, adjPos) && adjState.getMaterial() == Material.STONE) {
						anyStoneAdj = true;
						break;
					}
				}

				if (anyStoneAdj) {
					world.setBlock(movingPos, config.blockProvider.getState(rand, movingPos), 2);
				}
			}
		}

		return true;
	}
}
