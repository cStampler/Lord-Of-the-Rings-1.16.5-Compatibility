package lotr.common.world.gen.feature;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.block.CrystalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CrystalFeature extends Feature {
	public CrystalFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		CrystalFeatureConfig config = (CrystalFeatureConfig) confi;
		Mutable movingPos = new Mutable();

		for (int l = 0; l < config.tries; ++l) {
			int x = pos.getX() - rand.nextInt(config.xspread) + rand.nextInt(config.xspread);
			int y = pos.getY() - rand.nextInt(config.yspread) + rand.nextInt(config.yspread);
			int z = pos.getZ() - rand.nextInt(config.zspread) + rand.nextInt(config.zspread);
			movingPos.set(x, y, z);
			if (world.isEmptyBlock(movingPos)) {
				BlockState baseState = config.blockProvider.getState(rand, movingPos);
				List dirs = Arrays.asList(Direction.values());
				Collections.shuffle(dirs, rand);
				Iterator var13 = dirs.iterator();

				while (var13.hasNext()) {
					Direction dir = (Direction) var13.next();
					BlockState placeState = baseState.setValue(CrystalBlock.CRYSTAL_FACING, dir);
					if (placeState.canSurvive(world, movingPos)) {
						world.setBlock(movingPos, placeState, 2);
						break;
					}
				}
			}
		}

		return true;
	}
}
