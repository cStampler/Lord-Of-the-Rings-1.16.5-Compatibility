package lotr.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

public class NPCSpeechParticle extends SpriteTexturedParticle {
	private final IAnimatedSprite spriteSet;

	private NPCSpeechParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite sprites) {
		super(world, x, y, z);
		lifetime = 40 + random.nextInt(20);
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		spriteSet = sprites;
		setParticleSpriteAndAlpha();
	}

	// $FF: synthetic method
	NPCSpeechParticle(ClientWorld x0, double x1, double x2, double x3, double x4, double x5, double x6, IAnimatedSprite x7, Object x8) {
		this(x0, x1, x2, x3, x4, x5, x6, x7);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	private void setParticleSpriteAndAlpha() {
		setSprite(spriteSet.get(age % 30, 30));
		float ageF = (float) age / (float) lifetime;
		alpha = ageF < 0.75F ? 1.0F : (0.75F - ageF) / 0.25F;
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		++age;
		if (age < lifetime) {
			move(xd, yd, zd);
			xd *= 0.85D;
			zd *= 0.85D;
			yd *= 0.85D;
			setParticleSpriteAndAlpha();
		} else {
			remove();
		}

	}

	public static class Factory implements IParticleFactory {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprites) {
			spriteSet = sprites;
		}

		@Override
		public Particle createParticle(IParticleData type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new NPCSpeechParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
		}
	}
}
