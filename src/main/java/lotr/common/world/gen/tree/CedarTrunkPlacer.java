package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer.Foliage;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class CedarTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<CedarTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new CedarTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public CedarTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState branch) {
		this(baseHeight, heightRandA, heightRandB, Optional.empty(), Optional.empty(), Optional.of(new SimpleBlockStateProvider(branch)));
	}

	protected CedarTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		setDirtAt(world, basePos.below());

		for (int y = 0; y < trunkHeight; ++y) {
			placeLog(world, rand, basePos.above(y), trunk, bb, config);
		}

		List foliage = new ArrayList();
		foliage.add(new Foliage(basePos.above(trunkHeight), 0, false));

		for (int x = trunkHeight - 1; x > trunkHeight / 2; x -= 1 + rand.nextInt(3)) {
			int z = 1 + rand.nextInt(3);

			label89: for (int b = 0; b < z; ++b) {
				float angle = rand.nextFloat() * 3.1415927F * 2.0F;
				int length = MathHelper.nextInt(rand, 4, 7);
				int leafLayerLessWidth = 1;
				Mutable branchPos = new Mutable();

				for (int l = 0; l < length; ++l) {
					int branchX = Math.round(0.5F + MathHelper.cos(angle) * (l + 1));
					int branchZ = Math.round(0.5F + MathHelper.sin(angle) * (l + 1));
					int branchY = x - 3 + l / 2;
					BlockPos prevBranchPos = branchPos.immutable();
					branchPos.setWithOffset(basePos, branchX, branchY, branchZ);
					if (!branchPos.equals(prevBranchPos)) {
						if (!placeLog(world, rand, branchPos, trunk, bb, config)) {
							continue label89;
						}

						if (l == length - 1 && leafLayerLessWidth <= 1) {
							Mutable woodPos = new Mutable();

							for (int x1 = -1; x1 <= 1; ++x1) {
								for (int z1 = -1; z1 <= 1; ++z1) {
									if ((x1 == 0 || z1 == 0) && x1 != z1) {
										woodPos.setWithOffset(branchPos, x1, 0, z1);
										placeLog(world, rand, woodPos, trunk, bb, config);
									}
								}
							}
						}
					}
				}

				foliage.add(new Foliage(branchPos.above(), -leafLayerLessWidth, false));
			}
		}

		for (int x = -1; x <= 1; ++x) {
			for (int z = -1; z <= 1; ++z) {
				if (Math.abs(x) != Math.abs(z)) {
					Mutable rootPos = new Mutable().setWithOffset(basePos, x, rand.nextInt(2), z);
					int rootLength = 4 + rand.nextInt(3);
					try {
						growRootsDown(world, rand, rootPos, rootLength, trunk, bb, config);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return foliage;
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.CEDAR_TRUNK_PLACER;
	}
}
