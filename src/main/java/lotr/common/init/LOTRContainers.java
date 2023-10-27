package lotr.common.init;

import lotr.common.inv.AlloyForgeContainer;
import lotr.common.inv.FactionCraftingContainer;
import lotr.common.inv.KegContainer;
import lotr.common.inv.PouchContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRContainers {
	public static final DeferredRegister<ContainerType<?>> CONTAINERS;
	public static final RegistryObject<ContainerType<FactionCraftingContainer>> FACTION_CRAFTING;
	public static final RegistryObject<ContainerType<AlloyForgeContainer>> ALLOY_FORGE;
	public static final RegistryObject<ContainerType<KegContainer>> KEG;
	public static final RegistryObject<ContainerType<PouchContainer>> POUCH;

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
