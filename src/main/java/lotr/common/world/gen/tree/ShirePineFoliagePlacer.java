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

public class ShirePineFoliagePlacer extends FoliagePlacer {
	public static final Codec<ShirePineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("foliage_height").forGetter(foliage -> foliage.heightSpread)).apply(instance, ShirePineFoliagePlacer::new));
	private final FeatureSpread heightSpread;

	public ShirePineFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread heightSpread) {
		super(radius, offset);
		this.heightSpread = heightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int layerWidth = rand.nextInt(2);
		int nextMaxLayerWidth = 1;
		int nextStartingLayerWidth = 0;

		for (int y = foliageOffset; y >= foliageOffset - foliageHeight; --y) {
			placeLeavesRow(world, rand, config, foliage.foliagePos(), layerWidth, leaves, y, foliage.doubleTrunk(), bb);
			if (layerWidth >= nextMaxLayerWidth) {
				layerWidth = nextStartingLayerWidth;
				nextStartingLayerWidth = 1;
				nextMaxLayerWidth = Math.min(nextMaxLayerWidth + 1, foliageMaxWidth);
			} else {
				++layerWidth;
			}
		}

	}

	@Override
	public int foliageHeight(Random rand, int trunkHeight, BaseTreeFeatureConfig config) {
		return heightSpread.sample(rand);
	}

	@Override
	public int foliageRadius(Random rand, int trunkHeight) {
		return super.foliageRadius(rand, trunkHeight);
	}

	@Override
	protected boolean shouldSkipLocation(Random rand, int absX, int layerY, int absZ, int layerWidth, boolean bool6) {
		return absX == layerWidth && absZ == layerWidth && layerWidth > 0;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.SHIRE_PINE_FOLIAGE;
	}
}
