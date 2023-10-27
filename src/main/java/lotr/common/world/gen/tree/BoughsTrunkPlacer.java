package lotr.common.world.gen.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer.Foliage;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class BoughsTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<BoughsTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new BoughsTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public BoughsTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState wood, BlockState branch) {
		this(baseHeight, heightRandA, heightRandB, Optional.of(new SimpleBlockStateProvider(wood)), Optional.empty(), Optional.of(new SimpleBlockStateProvider(branch)));
	}

	protected BoughsTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		setDirtAt(world, basePos.below());

		int branchMinHeight;
		for (branchMinHeight = 0; branchMinHeight < trunkHeight; ++branchMinHeight) {
			placeLog(world, rand, basePos.above(branchMinHeight), trunk, bb, config);
		}

		branchMinHeight = (int) (trunkHeight * 0.6F);
		int branchMaxHeight = trunkHeight - 1;
		try {
			placeWood(world, rand, basePos.above(branchMaxHeight), trunk, bb, config, Axis.Y);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List foliage = new ArrayList();
		int deg = 0;

		int rootUp;
		for (int y = branchMaxHeight; y >= branchMinHeight; --y) {
			int branches = 1 + rand.nextInt(2);

			for (rootUp = 0; rootUp < branches; ++rootUp) {
				deg += 50 + rand.nextInt(70);
				float angle = (float) Math.toRadians(deg);
				float cos = MathHelper.cos(angle);
				float sin = MathHelper.sin(angle);
				float angleY = rand.nextFloat() * (float) Math.toRadians(40.0D);
				MathHelper.cos(angleY);
				float sinY = MathHelper.sin(angleY);
				int length = 4 + rand.nextInt(6);
				Mutable branchPos = new Mutable().setWithOffset(basePos, 0, y, 0);
				Axis branchAxis = Direction.fromYRot(deg + 90).getAxis();

				for (int l = 0; l < length; ++l) {
					if (Math.floor(cos * l) != Math.floor(cos * (l - 1))) {
						branchPos.move((int) Math.signum(cos), 0, 0);
					}

					if (Math.floor(sin * l) != Math.floor(sin * (l - 1))) {
						branchPos.move(0, 0, (int) Math.signum(sin));
					}

					if (Math.floor(sinY * l) != Math.floor(sinY * (l - 1))) {
						branchPos.move(0, (int) Math.signum(sinY), 0);
					}

					if (branchPos.getX() != basePos.getX() || branchPos.getZ() != basePos.getZ() || branchPos.getY() > basePos.getY() + branchMaxHeight) {
						if (!TreeFeature.validTreePos(world, branchPos)) {
							break;
						}

						try {
							placeWood(world, rand, branchPos, trunk, bb, config, branchAxis);
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				foliage.add(new Foliage(branchPos.above(2), rand.nextInt(2), false));
			}
		}

		for (Direction dir : Plane.HORIZONTAL) {
			rootUp = rand.nextInt(3);
			int rootLength = 3 + rootUp + rand.nextInt(3);
			int maxOut = 1;
			if (rootUp >= 2 && rand.nextBoolean()) {
				++maxOut;
			}

			Mutable rootPos = new Mutable().setWithOffset(basePos, dir.getStepX(), rootUp, dir.getStepZ());
			try {
				growRootsDownAndThenOut(world, rand, rootPos, rootLength, dir, maxOut, trunk, bb, config);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return foliage;
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.BOUGHS_TRUNK_PLACER;
	}
}
