package lotr.client.gui.inv;

import lotr.common.init.LOTRContainers;
import lotr.common.inv.AbstractAlloyForgeContainer;
import lotr.common.inv.FactionCraftingContainer;
import lotr.common.inv.KegContainer;
import lotr.common.inv.PouchContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.inventory.container.ContainerType;

public class ContainerScreenHelper {
	public static void registerScreens() {
		IScreenFactory c1 = (s1, s2, s3) -> new FactionCraftingScreen((FactionCraftingContainer) s1, s2, s3);
		IScreenFactory c2 = (s1, s2, s3) -> new AlloyForgeScreen((AbstractAlloyForgeContainer) s1, s2, s3);
		IScreenFactory c3 = (s1, s2, s3) -> new KegScreen((KegContainer) s1, s2, s3);
		IScreenFactory c4 = (s1, s2, s3) -> new PouchScreen((PouchContainer) s1, s2, s3);
		ScreenManager.register((ContainerType) LOTRContainers.FACTION_CRAFTING.get(), c1);
		ScreenManager.register((ContainerType) LOTRContainers.ALLOY_FORGE.get(), c2);
		ScreenManager.register((ContainerType) LOTRContainers.KEG.get(), c3);
		ScreenManager.register((ContainerType) LOTRContainers.POUCH.get(), c4);
	}
}
