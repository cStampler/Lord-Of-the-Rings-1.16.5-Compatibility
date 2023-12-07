package lotr.common.loot.modifiers;

import lotr.common.loot.modifiers.PolarBearBlubberModifier.Serializer;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRLootModifiers {
	public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS;
	public static final RegistryObject<GlobalLootModifierSerializer<PolarBearBlubberModifier>> POLAR_BEAR_BLUBBER;
	public static final RegistryObject<GlobalLootModifierSerializer<RemoveApplesFromOakLeavesModifier>> REMOVE_APPLES_FROM_OAK_LEAVES;

	static {
		LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, "lotr");
		POLAR_BEAR_BLUBBER = LOOT_MODIFIERS.register("polar_bear_blubber", Serializer::new);
		REMOVE_APPLES_FROM_OAK_LEAVES = LOOT_MODIFIERS.register("remove_apples_from_oak_leaves", RemoveApplesFromOakLeavesModifier.Serializer::new);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		LOOT_MODIFIERS.register(bus);
	}
}
