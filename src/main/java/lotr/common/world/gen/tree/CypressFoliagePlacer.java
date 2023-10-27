package lotr.common.world.gen.tree;

import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer;
import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;

public class CypressFoliagePlacer extends FoliagePlacer {
	public static final Codec<CypressFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("trunk_height").forGetter(foliage -> foliage.trunkHeightSpread)).apply(instance, CypressFoliagePlacer::new));
	private final FeatureSpread trunkHeightSpread;

	public CypressFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread trunkHeightSpread) {
		super(radius, offset);
		this.trunkHeightSpread = trunkHeightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int topCrossY = foliageOffset - 2;
		int bottomCrossesHighestY = foliageOffset - foliageHeight + rand.nextInt(3);

		for (int y = foliageOffset; y >= foliageOffset - foliageHeight; --y) {
			int layerWidth = foliage.radiusOffset();
			if (y < topCrossY && y > bottomCrossesHighestY) {
				++layerWidth;
			}

			placeLeavesRow(world, rand, config, foliage.foliagePos(), layerWidth, leaves, y, foliage.doubleTrunk(), bb);
			if (y == topCrossY || y <= bottomCrossesHighestY) {
				for (Direction dir : Plane.HORIZONTAL) {
					placeLeavesRow(world, rand, config, foliage.foliagePos().relative(dir), layerWidth, leaves, y, foliage.doubleTrunk(), bb);
				}
			}
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
	protected boolean shouldSkipLocation(Random rand, int x, int layerY, int z, int layerWidth, boolean bool6) {
		return false;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.CYPRESS_FOLIAGE;
	}
}
