package lotr.common.block;

import java.util.Random;

import lotr.common.init.*;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.*;

public class MordorRockBlock extends LOTRStoneBlock implements IGrowable {
	public static final BooleanProperty MOSSY;

	static {
		MOSSY = LOTRBlockStates.MOSSY;
	}

	public MordorRockBlock(MaterialColor materialColor) {
		super(materialColor);
		registerDefaultState(stateDefinition.any().setValue(MOSSY, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (rand.nextInt(32) == 0 && !LOTRUtil.hasSolidSide(world, pos.above(), Direction.DOWN)) {
			VoxelShape shape = state.getShape(world, pos);
			double x = rand.nextDouble();
			double z = rand.nextDouble();
			if (x >= shape.min(Axis.X) && x <= shape.max(Axis.X) && z >= shape.min(Axis.Z) && z <= shape.max(Axis.Z)) {
				double topY = state.getShape(world, pos).max(Axis.Y, x, z);
				world.addParticle(ParticleTypes.SMOKE, pos.getX() + x, pos.getY() + topY, pos.getZ() + z, 0.0D, 0.0D, 0.0D);
			}
		}

	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(MOSSY);
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean isClient) {
		BlockPos abovePos = pos.above();
		return world.getBlockState(abovePos).isAir(world, abovePos);
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		growMordorPlants(world, rand, pos, state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP) {
			return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		}
		Block block = facingState.getBlock();
		return state.setValue(MOSSY, block == LOTRBlocks.MORDOR_MOSS.get());
	}

	private static BlockState createMordorGrowBlock(ServerWorld world, Random rand, BlockPos pos) {
		boolean isRock = world.getBlockState(pos.below()).getBlock() == LOTRBlocks.MORDOR_ROCK.get();
		if (rand.nextInt(60) == 0) {
			return ((Block) LOTRBlocks.MORGUL_SHROOM.get()).defaultBlockState();
		}
		if (rand.nextInt(16) == 0) {
			return ((Block) LOTRBlocks.MORDOR_THORN.get()).defaultBlockState();
		}
		return rand.nextInt(isRock ? 4 : 8) == 0 ? ((Block) LOTRBlocks.MORDOR_MOSS.get()).defaultBlockState() : ((Block) LOTRBlocks.MORDOR_GRASS.get()).defaultBlockState();
	}

	public static void growMordorPlants(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		BlockPos above = pos.above();
		int tries = 128;

		label32: for (int i = 0; i < tries; ++i) {
			BlockPos plantPos = above;

			for (int triesHere = 0; triesHere < i / 16; ++triesHere) {
				plantPos = plantPos.offset(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);
				if (!world.getBlockState(plantPos.below()).is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES) || world.getBlockState(plantPos).isCollisionShapeFullBlock(world, plantPos)) {
					continue label32;
				}
			}

			BlockState curBlock = world.getBlockState(plantPos);
			if (curBlock.isAir(world, plantPos)) {
				BlockState plant = createMordorGrowBlock(world, rand, plantPos);
				if (plant.canSurvive(world, plantPos)) {
					world.setBlock(plantPos, plant, 3);
				}
			}
		}

	}
}
