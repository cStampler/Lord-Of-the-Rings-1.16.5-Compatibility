package lotr.common.init;

import lotr.common.inv.*;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

public class LOTRContainers {
	public static final DeferredRegister CONTAINERS;
	public static final RegistryObject FACTION_CRAFTING;
	public static final RegistryObject ALLOY_FORGE;
	public static final RegistryObject KEG;
	public static final RegistryObject POUCH;

	static {
		CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, "lotr");
		FACTION_CRAFTING = CONTAINERS.register("faction_crafting", () -> IForgeContainerType.create(FactionCraftingContainer::new));
		ALLOY_FORGE = CONTAINERS.register("alloy_forge", () -> IForgeContainerType.create(AlloyForgeContainer::new));
		KEG = CONTAINERS.register("keg", () -> IForgeContainerType.create(KegContainer::new));
		POUCH = CONTAINERS.register("pouch", () -> IForgeContainerType.create(PouchContainer::new));
	}

	public static void register() {
		CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
