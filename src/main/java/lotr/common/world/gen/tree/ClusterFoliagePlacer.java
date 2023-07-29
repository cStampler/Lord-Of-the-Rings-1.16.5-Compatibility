package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.*;

public class ClusterFoliagePlacer extends FoliagePlacer {
	public static final Codec<ClusterFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).apply(instance, ClusterFoliagePlacer::new));

	public ClusterFoliagePlacer(FeatureSpread radius, FeatureSpread offset) {
		super(radius, offset);
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int sphereWidth = foliageMaxWidth + foliage.radiusOffset();
		int leafBottom = -sphereWidth;

		for (int y = sphereWidth; y >= leafBottom; --y) {
			placeLeavesRow(world, rand, config, foliage.foliagePos(), sphereWidth, leaves, y, foliage.doubleTrunk(), bb);
		}

	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return 0;
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return super.foliageRadius(rand, trunkHeight);
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		int dSq = absX * absX + layerY * layerY + absZ * absZ;
		if (dSq < (layerWidth - 1) * (layerWidth - 1)) {
			return false;
		}
		return dSq >= layerWidth * layerWidth || rand.nextInt(3) == 0;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.CLUSTER_FOLIAGE;
	}
}
