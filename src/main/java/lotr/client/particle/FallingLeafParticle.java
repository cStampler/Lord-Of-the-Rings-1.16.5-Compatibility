package lotr.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FallingLeafParticle extends SpriteTexturedParticle {
	private FallingLeafParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(world, x, y, z);
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		quadSize = MathHelper.nextFloat(random, 0.075F, 0.25F);
		lifetime = MathHelper.nextInt(random, 100, 600);
	}

	// $FF: synthetic method
	FallingLeafParticle(ClientWorld x0, double x1, double x2, double x3, double x4, double x5, double x6, Object x7) {
		this(x0, x1, x2, x3, x4, x5, x6);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		++age;
		if (age < lifetime && !onGround) {
			alpha = 1.0F;
			int fadeAge = (int) (lifetime * 0.75F);
			if (age >= fadeAge) {
				alpha = MathHelper.lerp((float) (age - fadeAge) / (float) fadeAge, 1.0F, 0.0F);
			}

			move(xd, yd, zd);
		} else {
			remove();
		}

	}

	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprites) {
			spriteSet = sprites;
		}

		@Override
		public Particle createParticle(IParticleData type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			FallingLeafParticle particle = new FallingLeafParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.setAlpha(1.0F);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
