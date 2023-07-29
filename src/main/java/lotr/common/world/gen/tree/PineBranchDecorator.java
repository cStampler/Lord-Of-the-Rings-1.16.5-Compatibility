package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.block.*;
import net.minecraft.block.*;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.blockstateprovider.*;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.treedecorator.*;

public class PineBranchDecorator extends TreeDecorator {
	public static final Codec<PineBranchDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockStateProvider.CODEC.fieldOf("wood_provider").forGetter(deco -> deco.woodProvider), Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(deco -> deco.prob)).apply(instance, PineBranchDecorator::new));
	private final BlockStateProvider woodProvider;
	private final float prob;

	public PineBranchDecorator(BlockState log, float f) {
		this(new SimpleBlockStateProvider(log), f);
	}

	public PineBranchDecorator(BlockStateProvider log, float f) {
		woodProvider = log;
		prob = f;
	}

	@Override
	public void place(ISeedReader world, Random rand, List trunk, List leaves, Set decoSet, MutableBoundingBox bb) {
		int baseY = ((BlockPos) trunk.get(0)).getY();
		int trunkHeight = trunk.size();
		Direction[] lastDir = new Direction[1];
		trunk.stream().filter(pos -> {
			int diff = ((Vector3i) pos).getY() - baseY;
			return diff >= 3 && diff < trunkHeight - 3;
		}).forEach(pos -> {
			if (rand.nextFloat() < prob) {
				Direction dir = Plane.HORIZONTAL.getRandomDirection(rand);
				if (dir != lastDir[0]) {
					lastDir[0] = dir;
					BlockPos branchPos = ((BlockPos) pos).offset(dir.getStepX(), 0, dir.getStepZ());
					if (Feature.isAir(world, branchPos)) {
						BlockState blockstate = woodProvider.getState(rand, branchPos);
						if (blockstate.getBlock() instanceof RotatedPillarBlock) {
							blockstate = blockstate.setValue(RotatedPillarBlock.AXIS, dir.getAxis());
						} else if (blockstate.getBlock() instanceof AxialSlabBlock && blockstate.hasProperty(LOTRBlockStates.SLAB_AXIS)) {
							blockstate = blockstate.setValue(LOTRBlockStates.SLAB_AXIS, dir.getAxis()).setValue(SlabBlock.TYPE, dir.getAxisDirection() == AxisDirection.NEGATIVE ? SlabType.TOP : SlabType.BOTTOM);
						}

						setBlock(world, branchPos, blockstate, decoSet, bb);
					}
				}
			}

		});
	}

	@Override
	protected TreeDecoratorType type() {
		return LOTRTreeDecorators.PINE_BRANCH;
	}
}
