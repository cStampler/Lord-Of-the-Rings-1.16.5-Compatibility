package lotr.common.init;

import java.util.function.Supplier;

import lotr.common.world.biome.LOTRBiomeBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

public class PreRegisteredLOTRBiome {
	private final String name;
	private final ResourceLocation fullRegistryName;
	private final LazyOptional<LOTRBiomeBase> biomeWrapper;
	private boolean initialisedWrapper;

	public PreRegisteredLOTRBiome(String name, NonNullSupplier<LOTRBiomeBase> biomeWrapperSupplier) {
		this.name = name;
		fullRegistryName = new ResourceLocation("lotr", name);
		biomeWrapper = LazyOptional.of(biomeWrapperSupplier);
		initialisedWrapper = false;
	}

	public Biome getInitialisedBiome() {
		return getInitialisedBiomeWrapper().getActualBiome();
	}

	public LOTRBiomeBase getInitialisedBiomeWrapper() {
		if (!initialisedWrapper) {
			throw new IllegalStateException("LOTR biome " + name + " is not yet initialised!");
		}
		return getOrCreateBiomeWrapper();
	}

	public String getName() {
		return name;
	}

	private LOTRBiomeBase getOrCreateBiomeWrapper() {
		initialisedWrapper = true;
		try {
			return (LOTRBiomeBase) biomeWrapper.orElseThrow(() -> new IllegalStateException("Could not supply LOTR biome " + name));
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ResourceLocation getRegistryName() {
		return fullRegistryName;
	}

	public LOTRBiomeBase initialiseAndReturnBiomeWrapper(ResourceLocation biomeName) {
		if (initialisedWrapper) {
			throw new IllegalStateException("LOTR biome " + name + " is already initialised!");
		}
		return getOrCreateBiomeWrapper().setBiomeName(biomeName);
	}

	public Supplier<Biome> supplyBiomeInitialiser() {
		return () -> getInitialisedBiomeWrapper().initialiseActualBiome();
	}

	public Supplier<Biome> supplyInitialisedBiome() {
		return this::getInitialisedBiome;
	}
}
