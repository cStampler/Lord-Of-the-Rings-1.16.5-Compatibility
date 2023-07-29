package lotr.client.render;

import lotr.client.particle.*;
import lotr.common.init.LOTRParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;

public class LOTRClientParticles {
	public static void register(ParticleFactoryRegisterEvent event) {
		Minecraft mc = Minecraft.getInstance();
		ParticleManager particleMgr = mc.particleEngine;
		particleMgr.register((ParticleType) LOTRParticles.WATERFALL.get(), WaterfallParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.BLUE_ELVEN_GLOW.get(), ElvenGlowParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.GREEN_ELVEN_GLOW.get(), ElvenGlowParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.GOLD_ELVEN_GLOW.get(), ElvenGlowParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.SILVER_ELVEN_GLOW.get(), ElvenGlowParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.MALLORN_LEAF.get(), FallingLeafParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.MIRK_OAK_LEAF.get(), FallingLeafParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.GREEN_OAK_LEAF.get(), FallingLeafParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.RED_OAK_LEAF.get(), FallingLeafParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.GLITTER.get(), GlitterParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.RAIN.get(), TranslucentRainParticle.Factory::new);
		// particleMgr.register((ParticleType)LOTRParticles.DRIPPING_WATER.get(),
		// TranslucentDrippingParticle.Factory::new);
		// particleMgr.register((ParticleType)LOTRParticles.FALLING_WATER.get(),
		// TranslucentFallingLiquidParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.SPLASH.get(), TranslucentSplashParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.WHITE_SMOKE.get(), WhiteSmokeParticle.Factory::new);
		// particleMgr.register((ParticleType)LOTRParticles.MORGUL_WATER_EFFECT.get(),
		// MagicWaterEffectParticle.MorgulWaterFactory::new);
		// particleMgr.register((ParticleType)LOTRParticles.MIRKWOOD_WATER_EFFECT.get(),
		// MagicWaterEffectParticle.MirkwoodWaterFactory::new);
		particleMgr.register((ParticleType) LOTRParticles.NPC_SPEECH.get(), NPCSpeechParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.NPC_QUESTION.get(), NPCSpeechParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.NPC_EXCLAMATION.get(), NPCSpeechParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.CULUMALDA_POLLEN.get(), FallingLeafParticle.Factory::new);
		particleMgr.register((ParticleType) LOTRParticles.CULUMALD.get(), FallingLeafParticle.Factory::new);
	}
}
