package lotr.common.init;

import net.minecraftforge.registries.DeferredRegister;

public class RegistryOrderHelper {
	  public static <T> T preRegObject(DeferredRegister<? super T> registry, String name, T object) {
	    registry.register(name, () -> object);
	    return object;
	  }
	}
