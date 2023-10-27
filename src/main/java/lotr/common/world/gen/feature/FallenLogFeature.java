package lotr.common.world.gen.feature;

import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.block.RottenLogBlock;
import lotr.common.init.LOTRBlocks;
import lotr.common.world.map.RoadPointCache;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class FallenLogFeature extends Feature {
	public FallenLogFeature(Codec codec) {
		super(codec);
	}

	private boolean isSuitablePositionForLog(IWorld world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		if (!world.getBlockState(belowPos).isFaceSturdy(world, belowPos, Direction.UP)) {
			return false;
		}
		BlockState replacingState = world.getBlockState(pos);
		return replacingState.isAir(world, pos) || replacingState.getMaterial() == Material.WATER;
	}

	@Override
	public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, IFeatureConfig confi) {
		FallenLogFeatureConfig config = (FallenLogFeatureConfig) confi;
		if (!isSuitablePositionForLog(world, pos) || !RoadPointCache.checkNotGeneratingOnRoad(world, pos)) {
			return false;
		}
		Direction dir;
		int length;
		if (!config.horizontalOnly && rand.nextInt(3) == 0) {
			dir = Direction.UP;
			length = MathHelper.nextInt(rand, 1, 2);
		} else {
			dir = Plane.HORIZONTAL.getRandomDirection(rand);
			length = MathHelper.nextInt(rand, 2, 7);
		}

		Block logBlock = config.isStripped ? (Block) LOTRBlocks.STRIPPED_ROTTEN_LOG.get() : (Block) LOTRBlocks.ROTTEN_LOG.get();
		BlockState logAxisState = logBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, dir.getAxis());
		Mutable movingPos = new Mutable().set(pos);

		for (int i = 0; i < length && isSuitablePositionForLog(world, movingPos); ++i) {
			boolean waterlogged = world.getFluidState(movingPos).getType() == Fluids.WATER;
			BlockState placeState = logAxisState.setValue(RottenLogBlock.WATERLOGGED, waterlogged);
			world.setBlock(movingPos, placeState, 2);
			if (dir.getAxis() != Axis.Y) {
				LOTRFeatures.setGrassToDirtBelow(world, movingPos);
			}

			movingPos.move(dir);
		}

		return true;
	}
}
