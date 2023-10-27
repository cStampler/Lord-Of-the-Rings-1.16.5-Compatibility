package lotr.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;

public class GlitterParticle extends SpriteTexturedParticle {
	private final IAnimatedSprite spriteSet;

	private GlitterParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite sprites) {
		super(world, x, y, z);
		lifetime = 20 + random.nextInt(50);
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
		spriteSet = sprites;
		setSpriteFromAge(spriteSet);
		setParticleAlpha();
	}

	// $FF: synthetic method
	GlitterParticle(ClientWorld x0, double x1, double x2, double x3, double x4, double x5, double x6, IAnimatedSprite x7, Object x8) {
		this(x0, x1, x2, x3, x4, x5, x6, x7);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	private void setParticleAlpha() {
		float ageF = (float) age / (float) lifetime;
		alpha = MathHelper.clamp(MathHelper.sin(ageF * 3.1415927F), 0.02F, 1.0F);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		++age;
		if (age < lifetime) {
			move(xd, yd, zd);
			setParticleAlpha();
			setSpriteFromAge(spriteSet);
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
			return new GlitterParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet);
		}
	}
}
