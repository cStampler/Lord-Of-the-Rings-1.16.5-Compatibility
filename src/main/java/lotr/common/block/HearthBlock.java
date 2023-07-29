package lotr.common.block;

import java.util.Random;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.*;

public class HearthBlock extends Block {
	public static final BooleanProperty LIT;

	static {
		LIT = BlockStateProperties.LIT;
	}

	public HearthBlock() {
		this(Properties.of(Material.STONE, MaterialColor.COLOR_RED).requiresCorrectToolForDrops().strength(1.0F, 5.0F).sound(SoundType.STONE));
	}

	public HearthBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(LIT, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (state.getValue(LIT)) {
			int smokeHeight = 3;

			for (int j = 1; j <= smokeHeight; ++j) {
				BlockPos upPos = pos.above(j);
				if (world.getBlockState(upPos).canOcclude()) {
					break;
				}

				for (int l = 0; l < 2; ++l) {
					double d = upPos.getX() + rand.nextFloat();
					double d1 = upPos.getY() + rand.nextFloat();
					double d2 = upPos.getZ() + rand.nextFloat();
					if (rand.nextInt(3) == 0) {
						world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, d, d1, d2, 0.0D, 0.05D, 0.0D);
					} else {
						world.addParticle(ParticleTypes.LARGE_SMOKE, d, d1, d2, 0.0D, 0.0D, 0.0D);
					}
				}
			}
		}

	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(LIT);
	}

	protected Block getFireBlock() {
		return Blocks.FIRE;
	}

	@Override
	public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return side == Direction.UP;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (facing != Direction.UP) {
			return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		}
		Block block = facingState.getBlock();
		return state.setValue(LIT, block == getFireBlock());
	}
}
