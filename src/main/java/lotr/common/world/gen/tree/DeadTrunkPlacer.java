package lotr.common.world.gen.tree;

import java.util.*;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class DeadTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<DeadTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new DeadTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public DeadTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState wood, BlockState branch) {
		this(baseHeight, heightRandA, heightRandB, Optional.of(new SimpleBlockStateProvider(wood)), Optional.empty(), Optional.of(new SimpleBlockStateProvider(branch)));
	}

	protected DeadTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	private boolean placeRandomSurroundingBranch(IWorldGenerationReader world, Random rand, BlockPos pos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		try {
			return placeBranch(world, rand, pos.relative(Plane.HORIZONTAL.getRandomDirection(rand)), trunk, bb, config);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		setDirtAt(world, basePos.below());

		for (int y = 0; y < trunkHeight; ++y) {
			BlockPos trunkPos = basePos.above(y);
			placeLog(world, rand, trunkPos, trunk, bb, config);
			if (rand.nextInt(6) == 0) {
				placeRandomSurroundingBranch(world, rand, trunkPos, trunk, bb, config);
			}
		}

		if (trunkHeight >= 3) {
			Mutable branchPos = new Mutable();
			for (Direction dir : Plane.HORIZONTAL) {
				int branchLength = 2 + rand.nextInt(4);
				int branchOut = 0;
				int branchUp = trunkHeight - rand.nextInt(3);

				for (int l = 0; l < branchLength; ++l) {
					if (rand.nextInt(4) == 0) {
						++branchOut;
					}

					if (l > 0 && rand.nextInt(3) != 0) {
						++branchUp;
					}

					if (branchOut > 2 && branchUp == 0) {
						++branchUp;
					}

					branchPos.set(basePos.above(branchUp).relative(dir, branchOut));
					try {
						placeWood(world, rand, branchPos, trunk, bb, config, Axis.Y);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (rand.nextInt(8) == 0 && world.isStateAtPosition(branchPos, AbstractBlockState::isAir)) {
						placeRandomSurroundingBranch(world, rand, branchPos, trunk, bb, config);
					}
				}
			}
		}

		return ImmutableList.of();
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.DEAD_TRUNK_PLACER;
	}
}
