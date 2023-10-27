package lotr.common.world.gen.tree;

import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;

public class MirkOakFoliagePlacer extends FoliagePlacer {
	public static final Codec<MirkOakFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(Codec.intRange(0, 16).fieldOf("foliage_height").forGetter(foliage -> foliage.foliageHeight)).apply(instance, MirkOakFoliagePlacer::new));
	private final int foliageHeight;

	public MirkOakFoliagePlacer(FeatureSpread radius, FeatureSpread offset, int height) {
		super(radius, offset);
		foliageHeight = height;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		for (int y = foliageOffset; y >= foliageOffset - foliageHeight; --y) {
			int layerWidth = foliageMaxWidth + foliage.radiusOffset() - y;
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
		if (dSq <= (layerWidth - 1) * (layerWidth - 1)) {
			return false;
		}
		return dSq > layerWidth * layerWidth || rand.nextInt(4) == 0;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.MIRK_OAK_FOLIAGE;
	}
}
