package lotr.common.world.gen.tree;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.mojang.datafixers.Products.P6;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;

import lotr.common.world.gen.feature.LOTRFeatures;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;

public abstract class ExtendedTrunkPlacer extends AbstractTrunkPlacer {
	protected final Optional woodProvider;
	protected final Optional strippedLogProvider;
	protected final Optional branchProvider;

	protected ExtendedTrunkPlacer(int baseHeight, int heightRandA, int heightRandB, Optional woodProvider, Optional strippedLogProvider, Optional branchProvider) {
		super(baseHeight, heightRandA, heightRandB);
		this.woodProvider = woodProvider;
		this.strippedLogProvider = strippedLogProvider;
		this.branchProvider = branchProvider;
	}

	protected void growRootsDown(IWorldGenerationReader world, Random rand, Mutable rootPos, int rootLength, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) throws Throwable {
		growRootsDownBranchingOut(world, rand, rootPos, rootLength, (Direction) null, 0, trunk, bb, config);
	}

	protected void growRootsDownAndThenOut(IWorldGenerationReader world, Random rand, Mutable rootPos, int rootLength, Direction outwardsDir, int maxOut, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) throws Throwable {
		int roots = 0;
		int numOut = 0;

		while (rootPos.getY() >= 0 && TreeFeature.validTreePos(world, rootPos) && placeBranch(world, rand, rootPos, trunk, bb, config)) {
			if (world.isStateAtPosition(rootPos.below(), AbstractBlockState::canOcclude)) {
				rootPos.move(outwardsDir);
				++numOut;
				if (numOut > maxOut) {
					break;
				}
			} else {
				rootPos.move(Direction.DOWN);
			}

			++roots;
			if (roots > rootLength) {
				break;
			}
		}

	}

	protected void growRootsDownBranchingOut(IWorldGenerationReader world, Random rand, Mutable rootPos, int rootLength, Direction outwardsDir, int outwardsInterval, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) throws Throwable {
		int roots = 0;
		int outwardsStartAt = outwardsInterval > 0 ? rand.nextInt(outwardsInterval) : 0;

		while (rootPos.getY() >= 0 && TreeFeature.validTreePos(world, rootPos) && placeBranch(world, rand, rootPos, trunk, bb, config)) {
			if (outwardsDir != null && outwardsInterval > 0 && roots % outwardsInterval == outwardsStartAt) {
				rootPos.move(outwardsDir);
			} else {
				rootPos.move(Direction.DOWN);
			}

			++roots;
			if (roots > rootLength) {
				break;
			}
		}

	}

	protected boolean placeBranch(IWorldGenerationReader world, Random rand, BlockPos pos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config) throws Throwable {
		if (!TreeFeature.validTreePos(world, pos)) {
			return false;
		}
		BlockState placeState = ((BlockStateProvider) branchProvider.orElseThrow(() -> new IllegalStateException("Branch blockstate provider is not set!"))).getState(rand, pos);
		if (placeState.hasProperty(BlockStateProperties.WATERLOGGED)) {
			placeState = placeState.setValue(BlockStateProperties.WATERLOGGED, world.isStateAtPosition(pos, state -> (state.getFluidState().getType() == Fluids.WATER)));
		}

		IWorld worldProper;
		if (world instanceof IWorld) {
			worldProper = (IWorld) world;
			placeState = LOTRFeatures.getBlockStateInContext(placeState, worldProper, pos);
		}

		setBlock(world, pos, placeState, bb);
		trunk.add(pos.immutable());
		if (world instanceof IWorld) {
			worldProper = (IWorld) world;
			updateNeighboursAfterGeneration(worldProper, pos);
		}

		return true;
	}

	protected boolean placeLogWithAxis(IWorldGenerationReader world, Random rand, BlockPos pos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config, Axis axis) {
		if (!TreeFeature.validTreePos(world, pos)) {
			return false;
		}
		BlockState logState = config.trunkProvider.getState(rand, pos);
		if (logState.hasProperty(RotatedPillarBlock.AXIS)) {
			logState = logState.setValue(RotatedPillarBlock.AXIS, axis);
		}

		setBlock(world, pos, logState, bb);
		trunk.add(pos.immutable());
		return true;
	}

	protected boolean placeStrippedLog(IWorldGenerationReader world, Random rand, BlockPos pos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config, Axis axis) throws Throwable {
		if (!TreeFeature.validTreePos(world, pos)) {
			return false;
		}
		BlockState strippedLogState = ((BlockStateProvider) strippedLogProvider.orElseThrow(() -> new IllegalStateException("Stripped log blockstate provider is not set!"))).getState(rand, pos);
		if (strippedLogState.hasProperty(RotatedPillarBlock.AXIS)) {
			strippedLogState = strippedLogState.setValue(RotatedPillarBlock.AXIS, axis);
		}

		setBlock(world, pos, strippedLogState, bb);
		trunk.add(pos.immutable());
		return true;
	}

	protected boolean placeWood(IWorldGenerationReader world, Random rand, BlockPos pos, Set trunk, MutableBoundingBox bb, BaseTreeFeatureConfig config, Axis axis) throws Throwable {
		if (!TreeFeature.validTreePos(world, pos)) {
			return false;
		}
		BlockState woodState = ((BlockStateProvider) woodProvider.orElseThrow(() -> new IllegalStateException("Wood blockstate provider is not set!"))).getState(rand, pos);
		if (woodState.hasProperty(RotatedPillarBlock.AXIS)) {
			woodState = woodState.setValue(RotatedPillarBlock.AXIS, axis);
		}

		setBlock(world, pos, woodState, bb);
		trunk.add(pos.immutable());
		return true;
	}

	private void updateNeighboursAfterGeneration(IWorld world, BlockPos pos) {
		Direction[] var3 = Direction.values();
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			Direction dir = var3[var5];
			BlockPos adjacentPos = pos.relative(dir);
			BlockState state = world.getBlockState(adjacentPos);
			BlockState updatedState = LOTRFeatures.getBlockStateInContext(state, world, adjacentPos);
			world.setBlock(adjacentPos, updatedState, 19);
		}

	}

	protected static P6 baseCodecWithWood(Instance instance) {
		return trunkPlacerParts(instance).and(instance.group(BlockStateProvider.CODEC.optionalFieldOf("wood_provider").forGetter(trunk -> ((ExtendedTrunkPlacer) trunk).woodProvider), BlockStateProvider.CODEC.optionalFieldOf("stripped_log_provider").forGetter(trunk -> ((ExtendedTrunkPlacer) trunk).strippedLogProvider), BlockStateProvider.CODEC.optionalFieldOf("branch_provider").forGetter(trunk -> ((ExtendedTrunkPlacer) trunk).branchProvider)));
	}
}
