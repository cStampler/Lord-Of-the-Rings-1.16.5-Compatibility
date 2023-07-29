package lotr.client.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraftforge.api.distmarker.*;

public class WaterfallParticle extends SpriteTexturedParticle {
	private WaterfallParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(world, x, y, z);
		scale(2.5F);
		setSize(0.25F, 0.25F);
		lifetime = 20 + random.nextInt(16);
		xd = xSpeed;
		yd = ySpeed;
		zd = zSpeed;
	}

	// $FF: synthetic method
	WaterfallParticle(ClientWorld x0, double x1, double x2, double x3, double x4, double x5, double x6, Object x7) {
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
		if (age < lifetime && alpha > 0.0F) {
			move(xd, yd, zd);
			alpha -= 1.0F / lifetime;
			alpha = Math.max(alpha, 0.0F);
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
			WaterfallParticle particle = new WaterfallParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.setAlpha(0.75F);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
