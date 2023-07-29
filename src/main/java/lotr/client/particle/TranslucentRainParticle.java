package lotr.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class TranslucentRainParticle extends RainParticle {
	protected TranslucentRainParticle(ClientWorld world, double x, double y, double z) {
		super(world, x, y, z);
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
			TranslucentRainParticle particle = new TranslucentRainParticle(world, x, y, z);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
