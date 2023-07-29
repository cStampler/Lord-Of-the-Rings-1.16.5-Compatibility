package lotr.common.init;

import lotr.common.entity.capabilities.*;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class LOTRCapabilities {
	public static void register() {
		CapabilityManager.INSTANCE.register(PlateFallingData.class, new PlateFallingDataStorage(), PlateFallingData::new);
	}
}
