package lotr.common.init;

import net.minecraftforge.registries.DeferredRegister;

public class RegistryOrderHelper {
	public static Object preRegObject(DeferredRegister registry, String name, Object object) {
		registry.register(name, () -> object);
		return object;
	}
}
