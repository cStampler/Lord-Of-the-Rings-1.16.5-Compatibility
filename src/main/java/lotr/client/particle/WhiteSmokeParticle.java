package lotr.client.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SmokeParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;

public class WhiteSmokeParticle extends SmokeParticle {
	protected WhiteSmokeParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float scale, IAnimatedSprite spriteWithAge) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed, scale, spriteWithAge);
		float grey = MathHelper.nextFloat(random, 0.7F, 1.0F);
		rCol = gCol = bCol = grey;
	}

	public static class Factory implements IParticleFactory {
		private final IAnimatedSprite spriteSet;

		public Factory(IAnimatedSprite sprites) {
			spriteSet = sprites;
		}

		@Override
		public Particle createParticle(IParticleData typeIn, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new WhiteSmokeParticle(world, x, y, z, xSpeed, ySpeed, zSpeed, 1.0F, spriteSet);
		}
	}
}
