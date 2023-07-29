package lotr.common.block;

import java.util.*;

import lotr.common.entity.item.FallingTreasureBlockEntity;
import lotr.common.init.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.*;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.*;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.common.Tags.Blocks;
import net.minecraftforge.common.ToolType;

public class TreasurePileBlock extends FallingBlock implements IWaterLoggable {
	public static final IntegerProperty PILE_LEVEL;
	public static final int PILE_MAX_LEVEL = 8;
	public static final BooleanProperty WATERLOGGED;
	public static final BooleanProperty SUCH_WEALTH;
	private static final Map PILE_SHAPES;
	private static final VoxelShape COLLISION_FULL;
	private static final VoxelShape COLLISION_HALF;
	private static final VoxelShape COLLISION_MIN;

	static {
		PILE_LEVEL = LOTRBlockStates.TREASURE_PILE_LEVEL;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		SUCH_WEALTH = LOTRBlockStates.SUCH_WEALTH;
		PILE_SHAPES = new HashMap();
		COLLISION_FULL = VoxelShapes.block();
		COLLISION_HALF = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
		COLLISION_MIN = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
	}

	public TreasurePileBlock(MaterialColor color) {
		this(Properties.of(Material.DECORATION, color).strength(0.4F).harvestTool(ToolType.SHOVEL).noCollission().sound(LOTRBlocks.SOUND_TREASURE));
	}

	public TreasurePileBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(PILE_LEVEL, 1).setValue(WATERLOGGED, false).setValue(SUCH_WEALTH, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (world.random.nextInt(3) == 0) {
			doTreasureParticles(state, world, pos, rand);
		}

	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext context) {
		int level = state.getValue(PILE_LEVEL);
		if (context.getItemInHand().getItem() == asItem() && level < 8 && context.replacingClickedOnBlock()) {
			return context.getClickedFace() == Direction.UP;
		}
		return super.canBeReplaced(state, context);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		return Block.isFaceFull(belowState.getBlockSupportShape(world, belowPos), Direction.UP);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PILE_LEVEL, WATERLOGGED, SUCH_WEALTH);
	}

	@Override
	public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
		super.fallOn(world, pos, entity, fallDistance);
		spawnWalkingParticles(world, pos, 8);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		int level = state.getValue(PILE_LEVEL);
		if (level == 8) {
			return COLLISION_FULL;
		}
		return level >= 4 ? COLLISION_HALF : COLLISION_MIN;
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		int level = state.getValue(PILE_LEVEL);
		VoxelShape shape = (VoxelShape) PILE_SHAPES.get(level);
		if (shape == null) {
			shape = Block.box(0.0D, 0.0D, 0.0D, 16.0D, level / 8.0D * 16.0D, 16.0D);
			PILE_SHAPES.put(level, shape);
		}

		return shape;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		IWorld world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		boolean water = world.getFluidState(pos).getType() == Fluids.WATER;
		BlockState placeState = defaultBlockState();
		BlockState currentState = world.getBlockState(pos);
		if (currentState.getBlock() == this) {
			int level = currentState.getValue(PILE_LEVEL);
			placeState = currentState.setValue(PILE_LEVEL, Math.min(level + 1, 8));
		}

		return placeState.setValue(WATERLOGGED, water).setValue(SUCH_WEALTH, isSuchWealth(world, pos));
	}

	@Override
	public boolean isPathfindable(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
		return type == PathType.LAND && state.getValue(PILE_LEVEL) < 4 ? true : super.isPathfindable(state, world, pos, type);
	}

	private boolean isSuchWealth(IWorldReader world, BlockPos pos) {
		return world.getBlockState(pos.below()).is(Blocks.DIRT);
	}

	public void onEndFallingTreasure(World world, BlockPos pos, BlockState fallingState, BlockState hitState) {
		BlockState updatedState = fallingState.setValue(SUCH_WEALTH, isSuchWealth(world, pos));
		world.setBlock(pos, updatedState, 3);
	}

	@Override
	public void onLand(World world, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
		onEndFallingTreasure(world, pos, fallingState, hitState);
	}

	private void spawnWalkingParticles(World world, BlockPos pos, int num) {
		BlockState state = world.getBlockState(pos);

		for (int l = 0; l < num; ++l) {
			double x = pos.getX() + world.random.nextFloat();
			double y = pos.getY() + state.getShape(world, pos).max(Axis.Y);
			double z = pos.getZ() + world.random.nextFloat();
			double velX = MathHelper.nextFloat(world.random, -0.15F, 0.15F);
			double velY = MathHelper.nextFloat(world.random, 0.1F, 0.4F);
			double velZ = MathHelper.nextFloat(world.random, -0.15F, 0.15F);
			world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, state), x, y, z, velX, velY, velZ);
		}

	}

	@Override
	public void stepOn(World world, BlockPos pos, Entity entity) {
		double speedSq = entity.getDeltaMovement().lengthSqr();
		if (speedSq > 0.01D && !entity.isSteppingCarefully()) {
			spawnWalkingParticles(world, pos, 1 + world.random.nextInt(2));
		}

	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		BlockPos belowPos = pos.below();
		BlockState belowState = world.getBlockState(belowPos);
		if (world.isEmptyBlock(pos.below()) || (FallingBlock.isFree(belowState) || !canSurvive(state, world, pos)) && pos.getY() >= 0) {
			FallingTreasureBlockEntity fallingBlock = new FallingTreasureBlockEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, world.getBlockState(pos));
			world.addFreshEntity(fallingBlock);
		}

	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		if (facing == Direction.DOWN) {
			state = state.setValue(SUCH_WEALTH, isSuchWealth(world, currentPos));
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	public static void doTreasureParticles(BlockState state, World world, BlockPos pos, Random rand) {
		Direction face = Direction.getRandom(rand);
		BlockPos facePos = pos.relative(face);
		if (!world.getBlockState(facePos).isFaceSturdy(world, facePos, face.getOpposite())) {
			double x = 0.5D;
			double z = 0.5D;
			double y = 0.0D;
			double outside = 0.02D;
			double width = 0.5D + outside;
			double minY = state.getShape(world, pos).min(Axis.Y) - outside;
			double maxY = state.getShape(world, pos).max(Axis.Y) + outside;
			if (face.getAxis() == Axis.X) {
				x += face.getStepX() * width;
				y = MathHelper.nextDouble(rand, minY, maxY);
				z += MathHelper.nextDouble(rand, -width, width);
			} else if (face.getAxis() == Axis.Y) {
				x += MathHelper.nextDouble(rand, -width, width);
				if (face == Direction.DOWN) {
					y = minY;
				} else if (face == Direction.UP) {
					y = maxY;
				}

				z += MathHelper.nextDouble(rand, -width, width);
			} else if (face.getAxis() == Axis.Z) {
				x += MathHelper.nextDouble(rand, -width, width);
				y = MathHelper.nextDouble(rand, minY, maxY);
				z += face.getStepZ() * width;
			}

			x += pos.getX();
			y += pos.getY();
			z += pos.getZ();
			world.addParticle((IParticleData) LOTRParticles.GLITTER.get(), x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}
}
