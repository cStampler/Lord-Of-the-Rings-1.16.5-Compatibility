package lotr.common.world.gen.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer.Foliage;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class MirkOakTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<MirkOakTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new MirkOakTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public MirkOakTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState wood, BlockState branch) {
		this(baseHeight, heightRandA, heightRandB, Optional.of(new SimpleBlockStateProvider(wood)), Optional.empty(), Optional.of(new SimpleBlockStateProvider(branch)));
	}

	protected MirkOakTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	private void addLeafCanopy(IWorldGenerationReader world, Random rand, BlockPos pos, Set trunk, List foliage, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		int leafStart = 2;
		int leafTop = leafStart + 3;
		int addMaxRange = rand.nextInt(2);
		foliage.add(new Foliage(pos.above(leafTop), addMaxRange, false));
		List woodPositions = new ArrayList();

		for (int l = 0; l <= leafStart; ++l) {
			BlockPos layerPos = pos.above(l);
			woodPositions.add(layerPos);
			addLateralOffsets(woodPositions, layerPos, l + 1);
			if (l > 0) {
				addDiagonalOffsets(woodPositions, layerPos, l);
				if (l == leafStart && addMaxRange > 0) {
					addLateralOffsets(woodPositions, layerPos, l + 2);
					addDiagonalOffsets(woodPositions, layerPos, l + 1);
				}
			}
		}

		woodPositions.forEach(woodPos -> {
			try {
				placeWood(world, rand, (BlockPos) woodPos, trunk, bb, config, Axis.Y);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		setDirtAt(world, basePos.below());

		for (int y = 0; y < trunkHeight; ++y) {
			placeLog(world, rand, basePos.above(y), trunk, bb, config);
		}

		List foliage = new ArrayList();
		addLeafCanopy(world, rand, basePos.above(trunkHeight - 1), trunk, foliage, bb, config);
		int roots = 4 + rand.nextInt(1);

		for (int l = 0; l < roots; ++l) {
			Mutable rootPos = new Mutable().set(basePos).move(Direction.UP, 1 + rand.nextInt(1));
			int rootLength = 4 + rand.nextInt(4);
			Direction rootDir = Plane.HORIZONTAL.getRandomDirection(rand);
			rootPos.move(rootDir, 1);
			try {
				growRootsDownBranchingOut(world, rand, rootPos, rootLength, rootDir, 3, trunk, bb, config);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return foliage;
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.MIRK_OAK_TRUNK_PLACER;
	}

	private static void addDiagonalOffsets(List list, BlockPos midPos, int offset) {
		list.add(midPos.offset(-offset, 0, -offset));
		list.add(midPos.offset(-offset, 0, offset));
		list.add(midPos.offset(offset, 0, -offset));
		list.add(midPos.offset(offset, 0, offset));
	}

	private static void addLateralOffsets(List list, BlockPos midPos, int offset) {
		Plane.HORIZONTAL.forEach(dir -> {
			list.add(midPos.relative(dir, offset));
		});
	}
}
