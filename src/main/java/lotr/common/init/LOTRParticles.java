package lotr.common.init;

import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class LOTRParticles {
	public static final DeferredRegister PARTICLES;
	public static final RegistryObject WATERFALL;
	public static final RegistryObject BLUE_ELVEN_GLOW;
	public static final RegistryObject GREEN_ELVEN_GLOW;
	public static final RegistryObject GOLD_ELVEN_GLOW;
	public static final RegistryObject SILVER_ELVEN_GLOW;
	public static final RegistryObject MALLORN_LEAF;
	public static final RegistryObject MIRK_OAK_LEAF;
	public static final RegistryObject GREEN_OAK_LEAF;
	public static final RegistryObject RED_OAK_LEAF;
	public static final RegistryObject GLITTER;
	public static final RegistryObject RAIN;
	//public static final RegistryObject DRIPPING_WATER;
	//public static final RegistryObject FALLING_WATER;
	public static final RegistryObject SPLASH;
	public static final RegistryObject WHITE_SMOKE;
	//public static final RegistryObject MORGUL_WATER_EFFECT;
	//public static final RegistryObject MIRKWOOD_WATER_EFFECT;
	public static final RegistryObject NPC_SPEECH;
	public static final RegistryObject NPC_QUESTION;
	public static final RegistryObject NPC_EXCLAMATION;
	public static final RegistryObject CULUMALDA_POLLEN;
	public static final RegistryObject CULUMALD;

	static {
		PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "lotr");
		WATERFALL = PARTICLES.register("waterfall", () -> new BasicParticleType(false));
		BLUE_ELVEN_GLOW = PARTICLES.register("blue_elven_glow", () -> new BasicParticleType(false));
		GREEN_ELVEN_GLOW = PARTICLES.register("green_elven_glow", () -> new BasicParticleType(false));
		GOLD_ELVEN_GLOW = PARTICLES.register("gold_elven_glow", () -> new BasicParticleType(false));
		SILVER_ELVEN_GLOW = PARTICLES.register("silver_elven_glow", () -> new BasicParticleType(false));
		MALLORN_LEAF = PARTICLES.register("mallorn_leaf", () -> new BasicParticleType(false));
		MIRK_OAK_LEAF = PARTICLES.register("mirk_oak_leaf", () -> new BasicParticleType(false));
		GREEN_OAK_LEAF = PARTICLES.register("green_oak_leaf", () -> new BasicParticleType(false));
		RED_OAK_LEAF = PARTICLES.register("red_oak_leaf", () -> new BasicParticleType(false));
		GLITTER = PARTICLES.register("glitter", () -> new BasicParticleType(false));
		RAIN = PARTICLES.register("rain", () -> new BasicParticleType(false));
		//DRIPPING_WATER = PARTICLES.register("dripping_water", () -> new BasicParticleType(false));
		//FALLING_WATER = PARTICLES.register("falling_water", () -> new BasicParticleType(false));
		SPLASH = PARTICLES.register("splash", () -> new BasicParticleType(false));
		WHITE_SMOKE = PARTICLES.register("white_smoke", () -> new BasicParticleType(false));
		//MORGUL_WATER_EFFECT = PARTICLES.register("morgul_water_effect", () -> new BasicParticleType(false));
		//MIRKWOOD_WATER_EFFECT = PARTICLES.register("mirkwood_water_effect", () -> new BasicParticleType(false));
		NPC_SPEECH = PARTICLES.register("npc_speech", () -> new BasicParticleType(false));
		NPC_QUESTION = PARTICLES.register("npc_question", () -> new BasicParticleType(false));
		NPC_EXCLAMATION = PARTICLES.register("npc_exclamation", () -> new BasicParticleType(false));
		CULUMALDA_POLLEN = PARTICLES.register("culumalda_pollen", () -> new BasicParticleType(false));
		CULUMALD = PARTICLES.register("culumald", () -> new BasicParticleType(false));
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		PARTICLES.register(bus);
	}
}
