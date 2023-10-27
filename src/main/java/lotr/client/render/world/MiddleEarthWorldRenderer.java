package lotr.client.render.world;

import lotr.common.dim.LOTRDimensionType;
import lotr.common.init.LOTRParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;

public class MiddleEarthWorldRenderer extends WorldRenderer {
	private final Minecraft theMinecraft;

	public MiddleEarthWorldRenderer(Minecraft minecraft, RenderTypeBuffers buffers) {
		super(minecraft, buffers);
		theMinecraft = minecraft;
	}

	@Override
	public void addParticle(IParticleData particleData, boolean ignoreRange, boolean minimizeLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		/*
		 * if (particleData == ParticleTypes.DRIPPING_WATER &&
		 * this.isMiddleEarthDimension()) { particleData =
		 * (IParticleData)LOTRParticles.DRIPPING_WATER.get(); }
		 *
		 * if (particleData == ParticleTypes.FALLING_WATER &&
		 * this.isMiddleEarthDimension()) { particleData =
		 * (IParticleData)LOTRParticles.FALLING_WATER.get(); }
		 */

		if (particleData == ParticleTypes.SPLASH && isMiddleEarthDimension()) {
			particleData = (IParticleData) LOTRParticles.SPLASH.get();
		}

		super.addParticle(particleData, ignoreRange, minimizeLevel, x, y, z, xSpeed, ySpeed, zSpeed);
	}

	private final boolean isMiddleEarthDimension() {
		return theMinecraft.level.dimensionType() instanceof LOTRDimensionType;
	}
}
