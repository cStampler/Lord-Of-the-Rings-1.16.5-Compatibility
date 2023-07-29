package lotr.common.world.gen.tree;

import java.util.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.FoliagePlacer.Foliage;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;

public class PartyTrunkPlacer extends ExtendedTrunkPlacer {
	protected static final Codec<PartyTrunkPlacer> CODEC = RecordCodecBuilder.create(instance -> baseCodecWithWood(instance).apply(instance, (h1, h2, h3, h4, h5, h6) -> new PartyTrunkPlacer((int) h1, (int) h2, (int) h3, (Optional) h4, (Optional) h5, (Optional) h6)));

	public PartyTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, BlockState wood, BlockState branch) {
		this(baseHeight, heightRandA, heightRandB, Optional.of(new SimpleBlockStateProvider(wood)), Optional.empty(), Optional.of(new SimpleBlockStateProvider(branch)));
	}

	protected PartyTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB, woodProvider, strippedLogProvider, branchProvider);
	}

	private BlockPos getOffsetCentrePos(BlockPos basePos, int trunkHeightHere, float trunkPitch, float trunkYaw) {
		float upFrac = MathHelper.sin(trunkPitch) * trunkHeightHere;
		float outFrac = MathHelper.cos(trunkPitch) * trunkHeightHere;
		int offX = Math.round(outFrac * MathHelper.cos(trunkYaw));
		int offZ = Math.round(outFrac * MathHelper.sin(trunkYaw));
		return basePos.offset(offX, Math.round(upFrac), offZ);
	}

	@Override
	public List placeTrunk(IWorldGenerationReader world, Random rand, int trunkHeight, BlockPos basePos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) {
		List foliage = new ArrayList();
		int trunkWidth = 1;
		float trunkPitch = (float) Math.toRadians(MathHelper.nextFloat(rand, 65.0F, 90.0F));
		float trunkYaw = (float) Math.toRadians(rand.nextFloat() * 360.0F);
		Mutable movingPos = new Mutable();

		int angle;
		int rootUpY;
		Mutable rootPos;
		int woodBelow;
		int maxWoodBelow;
		for (angle = 0; angle < trunkHeight; ++angle) {
			BlockPos offsetCentrePos = getOffsetCentrePos(basePos, angle, trunkPitch, trunkYaw);

			for (int x = -trunkWidth; x <= trunkWidth; ++x) {
				for (rootUpY = -trunkWidth; rootUpY <= trunkWidth; ++rootUpY) {
					movingPos.setWithOffset(offsetCentrePos, x, 0, rootUpY);
					try {
						placeWood(world, rand, movingPos, trunk, bb, config, Axis.Y);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (angle == 0) {
						LOTRTrunkPlacers.setGrassToDirt(world, movingPos.below());
						rootPos = new Mutable().set(movingPos.below());
						woodBelow = 0;
						maxWoodBelow = 6 + rand.nextInt(3);

						try {
							while (rootPos.getY() >= 0 && placeWood(world, rand, rootPos, trunk, bb, config, Axis.Y)) {
								LOTRTrunkPlacers.setGrassToDirt(world, rootPos.below());
								rootPos.move(Direction.DOWN);
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

		angle = 0;

		int l1;
		int woodDropped;
		while (angle < 360) {
			angle += 30 + rand.nextInt(35);
			float angleR = (float) Math.toRadians(angle);
			float sin = MathHelper.sin(angleR);
			float cos = MathHelper.cos(angleR);
			int boughLength = 6 + rand.nextInt(6);
			woodBelow = Math.round(boughLength * 0.05F);
			maxWoodBelow = MathHelper.floor(MathHelper.nextFloat(rand, 0.65F, 0.95F) * trunkHeight);
			l1 = 2 + rand.nextInt(4);
			BlockPos offsetCentrePos = getOffsetCentrePos(basePos, maxWoodBelow, trunkPitch, trunkYaw);

			for (woodDropped = 0; woodDropped < boughLength; ++woodDropped) {
				int x = Math.round(cos * woodDropped);
				int z = Math.round(sin * woodDropped);
				int y = Math.round((float) woodDropped / (float) boughLength * l1);
				int range = woodBelow - Math.round((float) woodDropped / (float) boughLength * woodBelow * 0.5F);

				int branch_angle;
				for (branch_angle = -range; branch_angle <= range; ++branch_angle) {
					for (int y1 = -range; y1 <= range; ++y1) {
						for (int z1 = -range; z1 <= range; ++z1) {
							movingPos.set(offsetCentrePos).move(x + branch_angle, y + y1, z + z1);
							try {
								placeWood(world, rand, movingPos, trunk, bb, config, Axis.Y);
							} catch (Throwable e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

				branch_angle = angle + MathHelper.nextInt(rand, -60, 60);
				float branch_angleR = (float) Math.toRadians(branch_angle);
				float branch_sin = MathHelper.sin(branch_angleR);
				float branch_cos = MathHelper.cos(branch_angleR);
				int branchLength = 5 + rand.nextInt(4);
				int branchHeight = rand.nextInt(4);

				for (int l11 = 0; l11 < branchLength; ++l11) {
					int x1 = x + Math.round(branch_cos * l11);
					int z1 = z + Math.round(branch_sin * l11);
					int y1 = y + Math.round((float) l11 / (float) branchLength * branchHeight);

					int size;
					for (size = 0; size >= 0; --size) {
						movingPos.set(offsetCentrePos).move(x1, y1 + size, z1);
						placeLogWithAxis(world, rand, movingPos, trunk, bb, config, Direction.fromYRot(branch_angle).getClockWise().getAxis());
					}

					if (l11 == branchLength - 1 || rand.nextInt(6) == 0) {
						size = rand.nextInt(6) == 0 ? 1 : 0;
						foliage.add(new Foliage(offsetCentrePos.immutable().offset(x1, y1 + 1, z1), size, false));
					}
				}
			}
		}

		foliage.add(new Foliage(getOffsetCentrePos(basePos, trunkHeight, trunkPitch, trunkYaw), 1, false));
		int roots = 5 + rand.nextInt(5);

		for (int x = 0; x < roots; ++x) {
			rootUpY = 0 + rand.nextInt(4);
			rootPos = new Mutable().set(getOffsetCentrePos(basePos, rootUpY, trunkPitch, trunkYaw));
			woodBelow = 2 + rand.nextInt(4);
			Direction rootDir = Plane.HORIZONTAL.getRandomDirection(rand);
			rootPos.move(rootDir, trunkWidth + 1);
			rootPos.move(rootDir.getClockWise(), MathHelper.nextInt(rand, -trunkWidth, trunkWidth));

			for (l1 = 0; l1 < woodBelow; ++l1) {
				Mutable dropDownPos = new Mutable().set(rootPos);
				woodDropped = 0;
				while (dropDownPos.getY() >= 0) {
					BlockPos checkAbovePos = dropDownPos.above();
					boolean branch = woodDropped <= 1 && !world.isStateAtPosition(checkAbovePos, AbstractBlockState::canOcclude);
					boolean placedBlock = false;
					try {
						placedBlock = branch ? placeBranch(world, rand, dropDownPos, trunk, bb, config) : placeWood(world, rand, dropDownPos, trunk, bb, config, Axis.Y);
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (!placedBlock) {
						break;
					}

					if (!branch) {
						LOTRTrunkPlacers.setGrassToDirt(world, dropDownPos.below());
					}

					dropDownPos.move(Direction.DOWN);
					++woodDropped;
					if (woodDropped > 5) {
						break;
					}
				}

				rootPos.move(Direction.DOWN);
				if (rand.nextBoolean()) {
					rootPos.move(rootDir);
				}
			}
		}

		return foliage;
	}

	@Override
	protected TrunkPlacerType type() {
		return LOTRTrunkPlacers.PARTY_TRUNK_PLACER;
	}
}
