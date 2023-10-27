package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.IPlantable;

public class LeafBushesFeature extends Feature {
	public LeafBushesFeature(Codec codec) {
		super(codec);
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		BlockState leafBlockState = null;
		int searchTries = 40;
		int searchXRange = 6;
		int searchYRangeUp = 12;
		int searchZRange = 6;
		Mutable movingPos = new Mutable();

		int size;
		for (int i = 0; i < searchTries; ++i) {
			int x = MathHelper.nextInt(rand, -searchXRange, searchXRange);
			size = rand.nextInt(searchYRangeUp + 1);
			int z = MathHelper.nextInt(rand, -searchZRange, searchZRange);
			movingPos.set(pos).move(x, size, z);
			BlockState state = world.getBlockState(movingPos);
			if (state.is(BlockTags.LEAVES)) {
				leafBlockState = state.getBlock().defaultBlockState();
				if (leafBlockState.hasProperty(LeavesBlock.PERSISTENT)) {
					leafBlockState = leafBlockState.setValue(LeavesBlock.PERSISTENT, true);
				}
				break;
			}
		}

		if (leafBlockState != null) {
			BlockPos belowPos = pos.below();
			BlockState below = world.getBlockState(belowPos);
			if (below.canSustainPlant(world, belowPos, Direction.UP, (IPlantable) Blocks.OAK_SAPLING)) {
				size = 0;
				if (rand.nextInt(3) == 0) {
					++size;
				}

				int y = 0;

				for (int x = -size; x <= size; ++x) {
					for (int z = -size; z <= size; ++z) {
						if (size == 0 || Math.abs(x) != size || Math.abs(z) != size || rand.nextInt(3) == 0) {
							movingPos.set(pos).move(x, y, z);
							BlockState state = world.getBlockState(movingPos);
							if (!state.getMaterial().isLiquid() && state.getMaterial().isReplaceable()) {
								world.setBlock(movingPos, leafBlockState, 2);
							}
						}
					}
				}

				return true;
			}
		}

		return false;
	}
}
