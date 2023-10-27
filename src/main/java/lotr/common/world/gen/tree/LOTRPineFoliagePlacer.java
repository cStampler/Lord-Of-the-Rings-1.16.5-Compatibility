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

public class LOTRPineFoliagePlacer extends FoliagePlacer {
	public static final Codec<LOTRPineFoliagePlacer> CODEC = RecordCodecBuilder.create(instance -> foliagePlacerParts(instance).and(FeatureSpread.codec(0, 16, 8).fieldOf("foliage_height").forGetter(foliage -> foliage.heightSpread)).apply(instance, LOTRPineFoliagePlacer::new));
	private final FeatureSpread heightSpread;

	public LOTRPineFoliagePlacer(FeatureSpread radius, FeatureSpread offset, FeatureSpread heightSpread) {
		super(radius, offset);
		this.heightSpread = heightSpread;
	}

	@Override
	protected void createFoliage(IWorldGenerationReader world, Random rand, BaseTreeFeatureConfig config, int p_230372_4_, Foliage foliage, int foliageHeight, int foliageMaxWidth, Set leaves, int foliageOffset, MutableBoundingBox bb) {
		int baseFoliageWidth = foliage.radiusOffset();
		placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth, leaves, foliageOffset, foliage.doubleTrunk(), bb);
		placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + 1, leaves, foliageOffset - 1, foliage.doubleTrunk(), bb);
		int y = foliageOffset - 3;

		while (y > foliageOffset - foliageHeight) {
			int layerType = rand.nextInt(3);
			switch (layerType) {
			case 0:
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + 1, leaves, y, foliage.doubleTrunk(), bb);
				y -= 2;
				break;
			case 1:
				--y;
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + foliageMaxWidth - 2, leaves, y + 1, foliage.doubleTrunk(), bb);
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + foliageMaxWidth - 1, leaves, y, foliage.doubleTrunk(), bb);
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + foliageMaxWidth - 2, leaves, y - 1, foliage.doubleTrunk(), bb);
				y -= 3;
				break;
			case 2:
				--y;
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + foliageMaxWidth - 1, leaves, y + 1, foliage.doubleTrunk(), bb);
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + foliageMaxWidth, leaves, y, foliage.doubleTrunk(), bb);
				placeLeavesRow(world, rand, config, foliage.foliagePos(), baseFoliageWidth + foliageMaxWidth - 1, leaves, y - 1, foliage.doubleTrunk(), bb);
				y -= 3;
				break;
			default:
				break;
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
		return absX + absZ > layerWidth && layerWidth > 0;
	}

	@Override
	protected FoliagePlacerType type() {
		return LOTRFoliagePlacers.PINE_FOLIAGE;
	}
}
