package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.*;

public class ChandelierBlock extends Block {
	private static final VoxelShape CHANDELIER_SHAPE = Block.box(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D);

	public ChandelierBlock() {
		this(Properties.of(Material.DECORATION).noCollission().strength(0.0F, 2.0F).lightLevel(LOTRBlocks.constantLight(14)).sound(SoundType.CHAIN));
	}

	public ChandelierBlock(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World world, BlockPos pos, Random rand) {
		double minW = 0.21875D;
		double maxW = 1.0D - minW;
		double h = 0.6875D;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		doChandelierParticles(world, rand, x + minW, y + h, z + minW);
		doChandelierParticles(world, rand, x + maxW, y + h, z + minW);
		doChandelierParticles(world, rand, x + minW, y + h, z + maxW);
		doChandelierParticles(world, rand, x + maxW, y + h, z + maxW);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return canSupportCenter(world, pos.above(), Direction.DOWN);
	}

	protected void doChandelierParticles(World world, Random rand, double x, double y, double z) {
		world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
		world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return CHANDELIER_SHAPE;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.UP && !canSurvive(state, world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}
