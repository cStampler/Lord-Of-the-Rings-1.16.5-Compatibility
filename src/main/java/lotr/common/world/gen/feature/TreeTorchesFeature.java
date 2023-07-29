package lotr.common.world.gen.feature;

import java.util.*;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.*;

public class TreeTorchesFeature extends Feature {
	public TreeTorchesFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		BlockStateProvidingFeatureConfig config = (BlockStateProvidingFeatureConfig) confi;
		if (!world.isEmptyBlock(pos.below()) || !world.isEmptyBlock(pos) || !world.isEmptyBlock(pos.above())) {
			return false;
		}
		BlockState torch = config.stateProvider.getState(rand, pos);
		List shuffledDirections = Lists.newArrayList(Plane.HORIZONTAL);
		Collections.shuffle(shuffledDirections, rand);
		Iterator var8 = shuffledDirections.iterator();

		while (var8.hasNext()) {
			Direction dir = (Direction) var8.next();
			BlockPos offsetPos = pos.relative(dir);
			BlockState offsetState = world.getBlockState(offsetPos);
			if (offsetState.is(BlockTags.LOGS)) {
				BlockPos adjPos1 = pos.relative(dir.getClockWise());
				BlockPos adjPos2 = pos.relative(dir.getCounterClockWise());
				if (world.isEmptyBlock(adjPos1) && world.isEmptyBlock(adjPos2)) {
					world.setBlock(pos, torch.setValue(WallTorchBlock.FACING, dir.getOpposite()), 2);
					return true;
				}
			}
		}

		return false;
	}
}
