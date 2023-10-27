package lotr.common.world.gen.tree;

import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;

public class EmptyFoliagePlacer extends FoliagePlacer {
	public static final Codec CODEC = Codec.unit(EmptyFoliagePlacer::new);

	public EmptyFoliagePlacer() {
		super(FeatureSpread.fixed(0), FeatureSpread.fixed(0));
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int par4, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return 0;
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return 0;
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		return false;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.EMPTY_FOLIAGE;
	}
}
