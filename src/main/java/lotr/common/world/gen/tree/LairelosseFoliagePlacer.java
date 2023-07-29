package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.*;

public class LairelosseFoliagePlacer extends FoliagePlacer {
	public static final Codec<LairelosseFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("trunk_height").forGetter(foliage -> foliage.trunkHeightSpread)).apply(instance, LairelosseFoliagePlacer::new));
	private final FeatureSpread trunkHeightSpread;

	public LairelosseFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread trunkHeightSpread) {
		super(radius, offset);
		this.trunkHeightSpread = trunkHeightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int layerWidth = 0;

		for (int y = foliageOffset; y >= foliageOffset - foliageHeight; --y) {
			if (y >= foliageOffset - 1) {
				layerWidth = 0;
			} else {
				++layerWidth;
				if (layerWidth > foliageMaxWidth) {
					layerWidth = 1;
				}
			}

			placeLeavesRow(world, rand, config, foliage.foliagePos(), layerWidth + foliage.radiusOffset(), leaves, y, foliage.doubleTrunk(), bb);
		}

	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return Math.max(3, trunkHeight - trunkHeightSpread.sample(rand));
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return super.foliageRadius(rand, trunkHeight);
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		return absX + absZ > layerWidth;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.LAIRELOSSE_FOLIAGE;
	}
}
