package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.*;

public class AspenFoliagePlacer extends FoliagePlacer {
	public static final Codec<AspenFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("trunk_height").forGetter(foliage -> foliage.trunkHeightSpread)).apply(instance, AspenFoliagePlacer::new));

	private final FeatureSpread trunkHeightSpread;

	public AspenFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread trunkHeightSpread) {
		super(radius, offset);
		this.trunkHeightSpread = trunkHeightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int leafTop = foliageOffset;
		int leafBottom = foliageOffset - foliageHeight;

		for (int y = foliageOffset; y >= leafBottom; --y) {
			int baseLayerWidth = foliageMaxWidth;
			if (y >= leafTop - 1) {
				baseLayerWidth = foliageMaxWidth - 2;
			} else if (y >= leafTop - 3 || y <= leafBottom + 1 || rand.nextInt(4) == 0) {
				baseLayerWidth = foliageMaxWidth - 1;
			}

			int layerWidth = baseLayerWidth + foliage.radiusOffset();
			int branches = 4 + rand.nextInt(5);

			for (int b = 0; b < branches; ++b) {
				Mutable movingPos = new Mutable().setWithOffset(foliage.foliagePos(), 0, y, 0);
				int origX = movingPos.getX();
				int origZ = movingPos.getZ();
				int length = 4 + rand.nextInt(8);

				for (int l = 0; l < length && Math.abs(origX - movingPos.getX()) <= layerWidth && Math.abs(origZ - movingPos.getZ()) <= layerWidth; ++l) {
					doPlaceLeafBlock(world, rand, config, movingPos, leaves, bb);
					Direction randDir = Plane.HORIZONTAL.getRandomDirection(rand);
					movingPos.move(randDir);
				}
			}
		}

	}

	private void doPlaceLeafBlock(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, Mutable movingPos, Set leaves, MutableBoundingBox bb) {
		if (TreeFeature.validTreePos(world, movingPos)) {
			world.setBlock(movingPos, config.leavesProvider.getState(rand, movingPos), 19);
			bb.expand(new MutableBoundingBox(movingPos, movingPos));
			leaves.add(movingPos.immutable());
		}

	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return Math.max(4, trunkHeight - trunkHeightSpread.sample(rand));
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return super.foliageRadius(rand, trunkHeight);
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		return false;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.ASPEN_FOLIAGE;
	}
}
