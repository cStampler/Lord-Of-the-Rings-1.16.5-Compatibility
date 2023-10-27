package lotr.common.world.gen.tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;

import lotr.common.block.MirkOakLeavesBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class MirkOakLeavesGrowthDecorator extends TreeDecorator {
	public static final Codec CODEC = Codec.unit(MirkOakLeavesGrowthDecorator::new);

	@Override
	public void place(ISeedReader world, Random rand, List trunk, List leaves, Set decoSet, MutableBoundingBox bb) {
		Set addedLeavesPositions = new HashSet();
		Iterator var8 = leaves.iterator();

		while (true) {
			BlockPos leavesPos;
			int leavesBelow;
			do {
				if (!var8.hasNext()) {
					leaves.addAll(addedLeavesPositions);
					decoSet.addAll(addedLeavesPositions);
					var8 = leaves.iterator();

					while (var8.hasNext()) {
						leavesPos = (BlockPos) var8.next();
						BlockState leafState = world.getBlockState(leavesPos);
						if (leafState.getBlock() instanceof MirkOakLeavesBlock) {
							BlockPos belowLeavesPos = leavesPos.below();
							leafState = leafState.updateShape(Direction.DOWN, world.getBlockState(belowLeavesPos), world, leavesPos, belowLeavesPos);
							world.setBlock(leavesPos, leafState, 3);
						}
					}

					return;
				}

				leavesPos = (BlockPos) var8.next();
				leavesBelow = 0;
				if (world.isEmptyBlock(leavesPos.below()) && rand.nextInt(3) == 0) {
					++leavesBelow;
				}
			} while (leavesBelow <= 0);

			BlockState leafState = world.getBlockState(leavesPos);
			Mutable hangingPos = new Mutable().set(leavesPos);

			for (int i = 0; i < leavesBelow; ++i) {
				hangingPos.move(Direction.DOWN);
				if (TreeFeature.validTreePos(world, hangingPos)) {
					setBlock(world, hangingPos.immutable(), leafState, addedLeavesPositions, bb);
				}
			}
		}
	}

	@Override
	protected TreeDecoratorType type() {
		return LOTRTreeDecorators.MIRK_OAK_LEAVES_GROWTH;
	}
}
