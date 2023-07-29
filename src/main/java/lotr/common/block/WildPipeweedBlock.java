package lotr.common.block;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;

public class WildPipeweedBlock extends FlowerLikeBlock {
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (rand.nextInt(4) == 0) {
			Vector3d offset = state.getOffset(world, pos);
			double x = pos.getX() + offset.x;
			double y = pos.getY() + offset.y;
			double z = pos.getZ() + offset.z;
			x += MathHelper.nextFloat(rand, 0.1F, 0.9F);
			y += MathHelper.nextFloat(rand, 0.5F, 0.75F);
			z += MathHelper.nextFloat(rand, 0.1F, 0.9F);
			world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}
}
