package lotr.common.world.gen.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer.Foliage;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class FangornTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<FangornTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new FangornTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public FangornTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState wood, BlockState strippedLog) {
		this(baseHeight, heightRandA, heightRandB, Optional.of(new SimpleBlockStateProvider(wood)), Optional.of(new SimpleBlockStateProvider(strippedLog)), Optional.empty());
	}

	protected FangornTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		int trunkRadiusMin = (int) (trunkHeight * 0.125F);
		int trunkRadiusMax = trunkRadiusMin + 4;
		int xSlope = MathHelper.nextInt(rand, 4, 10) * (rand.nextBoolean() ? -1 : 1);
		int zSlope = MathHelper.nextInt(rand, 4, 10) * (rand.nextBoolean() ? -1 : 1);
		List foliage = new ArrayList();
		Mutable offsetCentrePos = new Mutable().set(basePos);
		Set strippedLogTrunkPositions = new HashSet();
		Mutable movingPos = new Mutable();

		int woodBelow;
		int maxWoodBelow;
		for (int y = 0; y < trunkHeight; ++y) {
			float heightF = (float) y / (float) trunkHeight;
			int width = trunkRadiusMax - (int) (heightF * (trunkRadiusMax - trunkRadiusMin));

			for (int x = -width; x <= width; ++x) {
				for (int z = -width; z <= width; ++z) {
					movingPos.set(offsetCentrePos).move(x, y, z);
					if (x * x + z * z < width * width) {
						try {
							if (placeStrippedLog(world, rand, movingPos, trunk, bb, config, Axis.Y)) {
								strippedLogTrunkPositions.add(movingPos.immutable());
							}
						} catch (Throwable e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (y == 0) {
							LOTRTrunkPlacers.setGrassToDirt(world, movingPos.below());
							Mutable woodBelowPos = new Mutable().set(movingPos.below());
							woodBelow = 0;
							maxWoodBelow = 6 + rand.nextInt(5);

							try {
								while (woodBelowPos.getY() >= 0 && placeStrippedLog(world, rand, woodBelowPos, trunk, bb, config, Axis.Y)) {
									strippedLogTrunkPositions.add(woodBelowPos.immutable());
									LOTRTrunkPlacers.setGrassToDirt(world, woodBelowPos.below());
									woodBelowPos.move(Direction.DOWN);
									++woodBelow;
									if (woodBelow > maxWoodBelow) {
										break;
									}
								}
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}

			if (y % xSlope == 0) {
				if (xSlope > 0) {
					offsetCentrePos.move(Direction.EAST);
				} else if (xSlope < 0) {
					offsetCentrePos.move(Direction.WEST);
				}
			}

			if (y % zSlope == 0) {
				if (zSlope > 0) {
					offsetCentrePos.move(Direction.SOUTH);
				} else if (zSlope < 0) {
					offsetCentrePos.move(Direction.NORTH);
				}
			}
		}

		Predicate notWood = state -> !((AbstractBlockState) state).is(BlockTags.LOGS);
		Iterator var42 = strippedLogTrunkPositions.iterator();

		while (true) {
			int boughLength;
			while (var42.hasNext()) {
				BlockPos strippedPos = (BlockPos) var42.next();
				Direction[] var46 = Direction.values();
				int z = var46.length;

				for (boughLength = 0; boughLength < z; ++boughLength) {
					Direction checkDir = var46[boughLength];
					if (world.isStateAtPosition(strippedPos.relative(checkDir), notWood)) {
						world.setBlock(strippedPos, ((BlockStateProvider) woodProvider.get()).getState(rand, strippedPos), 19);
						break;
					}
				}
			}

			int angle = 0;

			while (angle < 360) {
				angle += 10 + rand.nextInt(20);
				float angleR = (float) Math.toRadians(angle);
				float sin = MathHelper.sin(angleR);
				float cos = MathHelper.cos(angleR);
				boughLength = 12 + rand.nextInt(10);
				woodBelow = Math.round(boughLength / 25.0F * 1.5F);
				maxWoodBelow = MathHelper.floor(trunkHeight * (0.9F + rand.nextFloat() * 0.1F));
				int boughHeight = 3 + rand.nextInt(4);

				for (int l = 0; l < boughLength; ++l) {
					int x = Math.round(cos * l);
					int z = Math.round(sin * l);
					int y = maxWoodBelow + Math.round((float) l / (float) boughLength * boughHeight);
					int range = woodBelow - Math.round((float) l / (float) boughLength * woodBelow * 0.5F);

					int x1;
					for (x1 = -range; x1 <= range; ++x1) {
						for (int y1 = -range; y1 <= range; ++y1) {
							for (int z1 = -range; z1 <= range; ++z1) {
								movingPos.set(offsetCentrePos).move(x + x1, y + y1, z + z1);
								try {
									placeWood(world, rand, movingPos, trunk, bb, config, Axis.Y);
								} catch (Throwable e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}

					x1 = angle + rand.nextInt(360);
					float branch_angleR = (float) Math.toRadians(x1);
					float branch_sin = MathHelper.sin(branch_angleR);
					float branch_cos = MathHelper.cos(branch_angleR);
					int branchLength = 7 + rand.nextInt(6);
					int branchHeight = rand.nextInt(6);

					for (int l1 = 0; l1 < branchLength; ++l1) {
						int x11 = x + Math.round(branch_cos * l1);
						int z1 = z + Math.round(branch_sin * l1);
						int y1 = y + Math.round((float) l1 / (float) branchLength * branchHeight);

						for (int y2 = 0; y2 >= -1; --y2) {
							movingPos.set(offsetCentrePos).move(x11, y1 + y2, z1);
							try {
								placeWood(world, rand, movingPos, trunk, bb, config, Axis.Y);
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						if (l1 == branchLength - 1) {
							BlockPos foliagePos = offsetCentrePos.immutable().offset(x11, y1, z1);
							foliage.add(new Foliage(foliagePos, 0, false));
						}
					}
				}
			}

			return foliage;
		}
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.FANGORN_TRUNK_PLACER;
	}
}
