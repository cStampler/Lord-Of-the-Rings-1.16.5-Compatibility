package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.*;

public class BoughsFoliagePlacer extends FoliagePlacer {
	public static final Codec<BoughsFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(Codec.intRange(0, 16).fieldOf("foliage_height").forGetter(foliage -> foliage.foliageHeight)).apply(instance, BoughsFoliagePlacer::new));
	private final int foliageHeight;

	public BoughsFoliagePlacer(FeatureSpread radius, FeatureSpread offset, int height) {
		super(radius, offset);
		foliageHeight = height;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		for (int y = foliageOffset; y >= foliageOffset - foliageHeight; --y) {
			int leafRangeAdd = -2;
			if (y >= -2) {
				leafRangeAdd -= y;
			}

			int layerWidth = foliageMaxWidth + leafRangeAdd + foliage.radiusOffset();
			placeLeavesRow(world, rand, config, foliage.foliagePos(), layerWidth, leaves, y, foliage.doubleTrunk(), bb);
		}

	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return foliageHeight;
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return super.foliageRadius(rand, trunkHeight);
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		int dSq = absX * absX + absZ * absZ;
		int dCh = absX + Math.abs(layerY - -2) + absZ;
		if (dSq < layerWidth * layerWidth && dCh <= 7) {
			return (absX == layerWidth - 1 || absZ == layerWidth - 1) && rand.nextInt(4) == 0;
		}
		return true;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.BOUGHS_FOLIAGE;
	}
}
