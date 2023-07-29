package lotr.common.block;

import java.util.Random;
import java.util.function.Supplier;

import lotr.common.event.CompostingHelper;
import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRLeavesBlock extends LeavesBlock implements IForgeBlockState {
	private Supplier fallingParticle;
	private int fallingChance;

	public LOTRLeavesBlock() {
		this(Material.LEAVES.getColor());
	}

	public LOTRLeavesBlock(MaterialColor materialColor) {
		super(Properties.of(Material.LEAVES, materialColor).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().harvestTool(ToolType.HOE).isValidSpawn(LOTRBlocks::allowSpawnOnLeaves).isSuffocating(LOTRBlocks::posPredicateFalse).isViewBlocking(LOTRBlocks::posPredicateFalse));
		CompostingHelper.prepareCompostable(this, 0.3F);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		super.animateTick(state, world, pos, rand);
		if (fallingParticle != null && rand.nextInt(fallingChance) == 0) {
			IParticleData particle = (IParticleData) fallingParticle.get();
			double x = pos.getX() + rand.nextFloat();
			double y = pos.getY() - 0.05D;
			double z = pos.getZ() + rand.nextFloat();
			double xSpeed = MathHelper.nextFloat(rand, -0.1F, 0.1F);
			double ySpeed = MathHelper.nextFloat(rand, -0.03F, -0.01F);
			double zSpeed = MathHelper.nextFloat(rand, -0.1F, 0.1F);
			world.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
		}

	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 30;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	public LOTRLeavesBlock setFallingParticle(Supplier particle, int chance) {
		fallingParticle = particle;
		fallingChance = chance;
		return this;
	}
}
