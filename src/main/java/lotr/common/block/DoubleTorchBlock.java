package lotr.common.block;

import java.util.Random;
import java.util.function.ToIntFunction;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DoubleTorchBlock extends Block {
	public static final EnumProperty HALF;
	private static final VoxelShape LOWER_SHAPE;
	private static final VoxelShape UPPER_SHAPE;

	static {
		HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
		LOWER_SHAPE = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
		UPPER_SHAPE = Block.box(7.0D, 0.0D, 7.0D, 9.0D, 9.0D, 9.0D);
	}

	public DoubleTorchBlock(int light) {
		this(Properties.of(Material.DECORATION).noCollission().strength(0.0F).lightLevel(getDoubleTorchLightLevel(light)).sound(SoundType.WOOD));
	}

	public DoubleTorchBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		DoubleBlockHalf half = (DoubleBlockHalf) state.getValue(HALF);
		if (half == DoubleBlockHalf.UPPER) {
			double d0 = pos.getX() + 0.5D;
			double d1 = pos.getY() + 0.6D;
			double d2 = pos.getZ() + 0.5D;
			world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
			return canSupportCenter(world, pos.below(), Direction.UP);
		}
		boolean isPresent = state.getBlock() == this;
		if (!isPresent) {
			return true;
		}
		BlockState belowState = world.getBlockState(pos.below());
		return belowState.getBlock() == this && belowState.getValue(HALF) == DoubleBlockHalf.LOWER;
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(HALF);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		DoubleBlockHalf half = (DoubleBlockHalf) state.getValue(HALF);
		return half == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
	}

	@Override
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos pos = context.getClickedPos();
		return pos.getY() < context.getLevel().dimensionType().logicalHeight() - 1 && context.getLevel().getBlockState(pos.above()).canBeReplaced(context) ? super.getStateForPlacement(context) : null;
	}

	public void placeTorchAt(IWorld world, BlockPos pos, int flags) {
		world.setBlock(pos, defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER), flags);
		world.setBlock(pos.above(), defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), flags);
	}

	@Override
	public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!world.isClientSide && player.isCreative()) {
			LOTRDoubleGrassBlock.accessRemoveBottomHalf(world, pos, state, player);
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		world.setBlock(pos.above(), defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf half = (DoubleBlockHalf) state.getValue(HALF);
		if (facing.getAxis() != Axis.Y || half == DoubleBlockHalf.LOWER != (facing == Direction.UP) || facingState.getBlock() == this && facingState.getValue(HALF) != half) {
			if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(world, currentPos)) {
				return Blocks.AIR.defaultBlockState();
			}

			if (state.canSurvive(world, currentPos)) {
				return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
			}
		}

		return Blocks.AIR.defaultBlockState();
	}

	private static ToIntFunction getDoubleTorchLightLevel(int level) {
		return state -> {
			DoubleBlockHalf half = (DoubleBlockHalf) ((BlockState) state).getValue(HALF);
			return half == DoubleBlockHalf.UPPER ? level : 0;
		};
	}
}
