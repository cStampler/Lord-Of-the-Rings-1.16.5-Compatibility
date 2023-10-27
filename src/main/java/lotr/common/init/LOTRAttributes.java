package lotr.common.init;

import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRAttributes {
	public static final DeferredRegister ATTRIBUTES;
	public static final RegistryObject NPC_RANGED_INACCURACY;
	public static final RegistryObject NPC_MOUNT_ATTACK_SPEED;
	public static final RegistryObject NPC_CONVERSATION_RANGE;

	static {
		ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "lotr");
		NPC_RANGED_INACCURACY = regRangedAttribute("npc.ranged_inaccuracy", 1.0D, 0.0D, 128.0D);
		NPC_MOUNT_ATTACK_SPEED = regRangedAttribute("npc.mount_attack_speed", 1.7D, 0.0D, 1024.0D);
		NPC_CONVERSATION_RANGE = regRangedAttribute("npc.conversation_range", 8.0D, 0.0D, 32.0D);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ATTRIBUTES.register(bus);
	}

	private static final RegistryObject regRangedAttribute(String name, double defaultValue, double min, double max) {
		return ATTRIBUTES.register(name, () -> new RangedAttribute(String.format("attribute.name.%s.%s", "lotr", name), defaultValue, min, max));
	}
}
