package lotr.client.particle;

import com.mojang.blaze3d.platform.GlStateManager.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.*;

public class ElvenGlowParticle extends SpriteTexturedParticle {
	private static IParticleRenderType PARTICLE_SHEET_PROPER_TRANSLUCENT = new IParticleRenderType() {
		@Override
		public void begin(BufferBuilder buf, TextureManager texMgr) {
			RenderSystem.depthMask(true);
			texMgr.bind(AtlasTexture.LOCATION_PARTICLES);
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			RenderSystem.alphaFunc(516, 0.0F);
			buf.begin(7, DefaultVertexFormats.PARTICLE);
		}

		@Override
		public void end(Tessellator tess) {
			tess.end();
		}

		@Override
		public String toString() {
			return "PARTICLE_SHEET_PROPER_TRANSLUCENT";
		}
	};

	private ElvenGlowParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		super(world, x, y, z, xSpeed, ySpeed, zSpeed);
		xd = xd * 0.009999999776482582D + xSpeed;
		yd = yd * 0.009999999776482582D + ySpeed;
		zd = zd * 0.009999999776482582D + zSpeed;
		this.x += (random.nextFloat() - random.nextFloat()) * 0.05F;
		this.y += (random.nextFloat() - random.nextFloat()) * 0.05F;
		this.z += (random.nextFloat() - random.nextFloat()) * 0.05F;
		lifetime = (int) (8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
		lifetime *= 3;
	}

	// $FF: synthetic method
	ElvenGlowParticle(ClientWorld x0, double x1, double x2, double x3, double x4, double x5, double x6, Object x7) {
		this(x0, x1, x2, x3, x4, x5, x6);
	}

	@Override
	public int getLightColor(float pTick) {
		float f = (lifetime - (age + pTick)) / lifetime;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		int worldLight = super.getLightColor(pTick);
		int lx = worldLight & 255;
		int ly = worldLight >> 16 & 255;
		lx += (int) (f * 15.0F * 16.0F);
		if (lx > 240) {
			lx = 240;
		}

		return lx | ly << 16;
	}

	@Override
	public float getQuadSize(float pTick) {
		return 0.25F + 0.002F * MathHelper.sin((age + pTick - 1.0F) * 0.25F * 3.1415927F);
	}

	@Override
	public IParticleRenderType getRenderType() {
		return PARTICLE_SHEET_PROPER_TRANSLUCENT;
	}

	@Override
	public void move(double x, double y, double z) {
		setBoundingBox(getBoundingBox().move(x, y, z));
		setLocationFromBoundingbox();
	}

	@Override
	public void render(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		super.render(buffer, renderInfo, partialTicks);
	}

	@Override
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		++age;
		alpha = 0.9F - (age - 1.0F) * 0.02F;
		if (age < lifetime && alpha > 0.1F) {
			move(xd, yd, zd);
			xd *= 0.96D;
			yd *= 0.96D;
			zd *= 0.96D;
			if (onGround) {
				xd *= 0.7D;
				zd *= 0.7D;
			}
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
			ElvenGlowParticle particle = new ElvenGlowParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
			particle.pickSprite(spriteSet);
			return particle;
		}
	}
}
