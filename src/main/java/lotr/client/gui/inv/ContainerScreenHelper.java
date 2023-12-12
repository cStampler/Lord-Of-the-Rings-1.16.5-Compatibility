package lotr.client.gui.inv;

import lotr.common.init.LOTRContainers;
import net.minecraft.client.gui.ScreenManager;

public class ContainerScreenHelper {
	public static void registerScreens() {
		ScreenManager.register(LOTRContainers.FACTION_CRAFTING.get(), FactionCraftingScreen::new);
	    ScreenManager.register(LOTRContainers.ALLOY_FORGE.get(), AlloyForgeScreen::new);
	    ScreenManager.register(LOTRContainers.KEG.get(), KegScreen::new);
	    ScreenManager.register(LOTRContainers.POUCH.get(), PouchScreen::new);
	}
}
