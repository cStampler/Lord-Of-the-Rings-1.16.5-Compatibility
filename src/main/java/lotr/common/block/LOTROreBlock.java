package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.common.ToolType;

public class LOTROreBlock extends OreBlock {
	private int oreHarvestLvl;

	public LOTROreBlock(Properties properties) {
		super(properties.requiresCorrectToolForDrops());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (this == LOTRBlocks.DURNOR_ORE.get() && rand.nextFloat() < 0.33F) {
			doDurnorParticles(world, pos, rand);
		}

	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return oreHarvestLvl;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return ToolType.PICKAXE;
	}

	public LOTROreBlock setOreLevel(int harvestLvl) {
		oreHarvestLvl = harvestLvl;
		return this;
	}

	@Override
	protected int xpOnDrop(Random rand) {
		if (this == LOTRBlocks.SULFUR_ORE.get() || this == LOTRBlocks.NITER_ORE.get() || this == LOTRBlocks.DURNOR_ORE.get()) {
			return MathHelper.nextInt(rand, 0, 2);
		}
		if (this == LOTRBlocks.GLOWSTONE_ORE.get()) {
			return MathHelper.nextInt(rand, 2, 4);
		}
		if (this == LOTRBlocks.EDHELVIR_ORE.get()) {
			return MathHelper.nextInt(rand, 2, 5);
		}
		return this != LOTRBlocks.GULDURIL_ORE_MORDOR.get() && this != LOTRBlocks.GULDURIL_ORE_STONE.get() ? super.xpOnDrop(rand) : MathHelper.nextInt(rand, 2, 5);
	}

	public static void doDurnorParticles(World world, BlockPos pos, Random rand) {
		Direction faceDir = Direction.getRandom(rand);
		BlockPos offsetPos = pos.relative(faceDir);
		if (!world.getBlockState(offsetPos).isSolidRender(world, offsetPos)) {
			double sideOffset = 0.55D;
			Vector3d vec = Vector3d.atCenterOf(pos).add(faceDir.getStepX() * sideOffset, faceDir.getStepY() * sideOffset, faceDir.getStepZ() * sideOffset);
			float f1 = MathHelper.nextFloat(rand, -0.5F, 0.5F);
			float f2 = MathHelper.nextFloat(rand, -0.5F, 0.5F);
			if (faceDir.getAxis() == Axis.X) {
				vec = vec.add(0.0D, f1, f2);
			} else if (faceDir.getAxis() == Axis.Y) {
				vec = vec.add(f1, 0.0D, f2);
			} else if (faceDir.getAxis() == Axis.Z) {
				vec = vec.add(f1, f2, 0.0D);
			}

			world.addParticle(ParticleTypes.FLAME, vec.x, vec.y, vec.z, 0.0D, 0.0D, 0.0D);
		}

	}
}
