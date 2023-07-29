package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.*;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.*;

public class FirFoliagePlacer extends FoliagePlacer {
	public static final Codec<FirFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("foliage_height").forGetter(foliage -> foliage.heightSpread)).apply(instance, FirFoliagePlacer::new));
	private final FeatureSpread heightSpread;

	public FirFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread heightSpread) {
		super(radius, offset);
		this.heightSpread = heightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int baseFoliageWidth = foliage.radiusOffset();
		placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth, leaves, foliageOffset, foliage.doubleTrunk(), bb);
		placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth, leaves, foliageOffset - 1, foliage.doubleTrunk(), bb);
		int leafBottom = foliageOffset - foliageHeight;
		int topY = foliageOffset - 2;
		int leafLayers = topY - leafBottom + 1;
		int sectionHeight = MathHelper.ceil((float) leafLayers / (float) foliageMaxWidth);
		int curSectionWidth = 1;
		int curSectionHeight = 0;

		for (int y = topY; y >= leafBottom; --y) {
			placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + curSectionWidth, leaves, y, foliage.doubleTrunk(), bb);
			++curSectionHeight;
			if (curSectionHeight >= sectionHeight) {
				++curSectionWidth;
				curSectionHeight = 0;
			}
		}

	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return Math.min(heightSpread.sample(rand), trunkHeight - 1);
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return super.foliageRadius(rand, trunkHeight);
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		int taxicab = absX + absZ;
		return taxicab > layerWidth && layerWidth > 0;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.FIR_FOLIAGE;
	}
}
