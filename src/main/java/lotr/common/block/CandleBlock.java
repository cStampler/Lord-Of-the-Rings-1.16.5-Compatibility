package lotr.common.block;

import java.util.Random;

import lotr.common.util.LOTRUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CandleBlock extends LOTRTorchBlock {
	public static final IntegerProperty NUM_CANDLES;
	private static final VoxelShape ONE_CANDLE_SHAPE;
	private static final VoxelShape MULTI_CANDLE_SHAPE;

	static {
		NUM_CANDLES = LOTRBlockStates.CANDLES_1_4;
		ONE_CANDLE_SHAPE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);
		MULTI_CANDLE_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 10.0D, 14.0D);
	}

	public CandleBlock(int lightBase, int lightStep) {
		super(state -> {
			int candles = ((BlockState) state).getValue(NUM_CANDLES);
			return lightBase + (candles - 1) * lightStep;
		}, SoundType.WOOD);
		registerDefaultState(defaultBlockState().setValue(NUM_CANDLES, 1));
		setParticles(() -> ParticleTypes.FLAME);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		int candles = state.getValue(NUM_CANDLES);
		switch (candles) {
		case 1:
			animateTorch(world, pos, rand, 0.5D, 0.7D, 0.5D);
			break;
		case 2:
			animateTorch(world, pos, rand, 0.3125D, 0.575D, 0.3125D);
			animateTorch(world, pos, rand, 0.6875D, 0.7D, 0.6875D);
			break;
		case 3:
			animateTorch(world, pos, rand, 0.4375D, 0.575D, 0.25D);
			animateTorch(world, pos, rand, 0.25D, 0.575D, 0.625D);
			animateTorch(world, pos, rand, 0.75D, 0.7D, 0.6875D);
			break;
		case 4:
			animateTorch(world, pos, rand, 0.3125D, 0.6375D, 0.3125D);
			animateTorch(world, pos, rand, 0.75D, 0.575D, 0.25D);
			animateTorch(world, pos, rand, 0.25D, 0.575D, 0.75D);
			animateTorch(world, pos, rand, 0.75D, 0.7D, 0.6875D);
			break;
		default:
			break;
		}

	}

	@Override
	public boolean canBeReplaced(BlockState state, BlockItemUseContext useContext) {
		if (useContext.getItemInHand().getItem() == asItem()) {
			int candles = state.getValue(NUM_CANDLES);
			if (candles < 4) {
				BlockState oneMoreCandle = state.setValue(NUM_CANDLES, candles + 1);
				if (oneMoreCandle.canSurvive(useContext.getLevel(), useContext.getClickedPos())) {
					return true;
				}
			}
		}

		return super.canBeReplaced(state, useContext);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		int candles = state.getValue(NUM_CANDLES);
		return candles > 1 ? LOTRUtil.hasSolidSide(world, pos.below(), Direction.UP) : super.canSurvive(state, world, pos);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(NUM_CANDLES);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		int candles = state.getValue(NUM_CANDLES);
		return candles > 1 ? MULTI_CANDLE_SHAPE : ONE_CANDLE_SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = context.getLevel().getBlockState(context.getClickedPos());
		if (state.getBlock() == this) {
			int candles = state.getValue(NUM_CANDLES);
			return state.setValue(NUM_CANDLES, Math.min(4, candles + 1));
		}
		return super.getStateForPlacement(context);
	}
}
