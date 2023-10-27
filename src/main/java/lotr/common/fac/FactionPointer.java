package lotr.common.fac;

import java.util.Optional;

import lotr.common.LOTRLog;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldReader;

public class FactionPointer {
	private final ResourceLocation name;

	private FactionPointer(ResourceLocation name) {
		this.name = name;
	}

	public ResourceLocation getName() {
		return name;
	}

	public String getNameString() {
		return getName().toString();
	}

	public boolean matches(Faction fac) {
		return getName().equals(fac.getName());
	}

	public Optional<Faction> resolveFaction(FactionSettings currentSettings) {
		return Optional.ofNullable(currentSettings.getFactionByPointer(this));
	}

	public Optional<Faction> resolveFaction(IWorldReader world) {
		FactionSettings currentSettings = FactionSettingsManager.sidedInstance(world).getCurrentLoadedFactions();
		if (currentSettings == null) {
			LOTRLog.error("Tried to call a FactionPointer (%s) outside the context of a currently loaded faction list");
			return Optional.empty();
		}
		return this.resolveFaction(currentSettings);
	}

	public static FactionPointer of(ResourceLocation name) {
		return new FactionPointer(name);
	}

	public static FactionPointer of(String namespace, String path) {
		return of(new ResourceLocation(namespace, path));
	}
}
