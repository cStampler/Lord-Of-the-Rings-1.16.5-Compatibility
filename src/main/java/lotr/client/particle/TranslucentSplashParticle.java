package lotr.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class TranslucentSplashParticle extends TranslucentRainParticle {
	private TranslucentSplashParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(world, x, y, z);
		gravity = 0.04F;
		if (ySpeed == 0.0D && (xSpeed != 0.0D || zSpeed != 0.0D)) {
			xd = xSpeed;
			yd = 0.1D;
			zd = zSpeed;
		}

	}

	// $FF: synthetic method
	TranslucentSplashParticle(ClientWorld x0, double x1, double x2, double x3, double x4, double x5, double x6, Object x7) {
		this(x0, x1, x2, x3, x4, x5, x6);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public static class Factory implements IParticleFactory {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprites) {
			spriteSet = sprites;
		}

		@Override
		public Particle createParticle(IParticleData type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			TranslucentSplashParticle particle = new TranslucentSplashParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
