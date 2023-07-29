package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer.Foliage;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class DesertTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<DesertTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new DesertTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public DesertTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState wood) {
		this(baseHeight, heightRandA, heightRandB, Optional.of(new SimpleBlockStateProvider(wood)), Optional.empty(), Optional.empty());
	}

	protected DesertTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		setDirtAt(world, basePos.below());

		for (int y = 0; y < trunkHeight; ++y) {
			placeLog(world, rand, basePos.above(y), trunk, bb, config);
		}

		List foliage = new ArrayList();
		int trunkTopOffset = 0;

		Mutable branchPos;
		for (Iterator var11 = Plane.HORIZONTAL.iterator(); var11.hasNext(); foliage.add(new Foliage(branchPos.above(), 0, false))) {
			Direction branchDir = (Direction) var11.next();
			int branchLength = 1 + rand.nextInt(3);
			int branchHeight = trunkHeight - trunkTopOffset - 1 - rand.nextInt(2);
			branchPos = new Mutable().setWithOffset(basePos, 0, branchHeight, 0);

			for (int l = 0; l < branchLength; ++l) {
				if (rand.nextInt(3) != 0) {
					branchPos.move(Direction.UP);
				}

				if (rand.nextInt(3) != 0) {
					branchPos.move(branchDir);
				}

				if (!placeLog(world, rand, branchPos, trunk, bb, config)) {
					break;
				}
			}
		}

		return foliage;
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.DESERT_TRUNK_PLACER;
	}
}
