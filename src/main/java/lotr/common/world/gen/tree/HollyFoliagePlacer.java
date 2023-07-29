package lotr.common.world.gen.tree;

import java.util.*;

import com.google.common.math.IntMath;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.*;

public class HollyFoliagePlacer extends FoliagePlacer {
	public static final Codec<HollyFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("trunk_height").forGetter(foliage -> foliage.trunkHeightSpread)).apply(instance, HollyFoliagePlacer::new));
	private final FeatureSpread trunkHeightSpread;

	public HollyFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread trunkHeightSpread) {
		super(radius, offset);
		this.trunkHeightSpread = trunkHeightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {

		for (int y = foliageOffset; y >= foliageOffset - foliageHeight; --y) {
			int layerWidth;
			if (y == foliageOffset) {
				layerWidth = 0;
			} else if (y < foliageOffset - 2 && y != foliageOffset - foliageHeight) {
				layerWidth = foliageMaxWidth;
			} else {
				layerWidth = 1;
			}

			placeLeavesRow(world, rand, config, foliage.foliagePos(), layerWidth + foliage.radiusOffset(), leaves, y, foliage.doubleTrunk(), bb);
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
		if (layerWidth > 0 && IntMath.mod(layerY, 2) == 1) {
			return absX == layerWidth && absZ == layerWidth;
		}
		return false;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.HOLLY_FOLIAGE;
	}
}
