package lotr.client.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import lotr.client.gui.LOTRMainMenuScreen;
import lotr.client.gui.inv.KegScreen;
import lotr.client.gui.inv.PouchScreen;
import lotr.client.gui.widget.button.PouchRestockButton;
import lotr.common.config.LOTRConfig;
import lotr.common.inv.ShulkerBoxContainerFix;
import lotr.common.network.CPacketRestockPouches;
import lotr.common.network.LOTRPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LOTRGuiHandler {
	public static final Map<Class<? extends ContainerScreen>, PouchRestockButtonPositioner> pouchRestockPositionerByScreen = new HashMap<>();
	public static final Set<Class<? extends ContainerScreen>> pouchRestockExclusionScreens = new HashSet<>();
	public static final LOTRGuiHandler.PouchRestockButtonPositioner DEFAULT_ABOVE_TOP_RIGHT_SLOT = (topLeftPlayerSlot, topRightPlayerSlot) -> Pair.of(topRightPlayerSlot.x + 7, topRightPlayerSlot.y - 14);
	public static final LOTRGuiHandler.PouchRestockButtonPositioner RIGHT_FROM_TOP_RIGHT_SLOT = (topLeftPlayerSlot, topRightPlayerSlot) -> Pair.of(topRightPlayerSlot.x + 21, topRightPlayerSlot.y - 1);
	public static final LOTRGuiHandler.PouchRestockButtonPositioner ABOVE_TOP_LEFT_SLOT = (topLeftPlayerSlot, topRightPlayerSlot) -> Pair.of(topLeftPlayerSlot.x - 1, topLeftPlayerSlot.y - 14);
	public static final LOTRGuiHandler.PouchRestockButtonPositioner FIX_HUMMEL = (topLeftPlayerSlot, topRightPlayerSlot) -> Pair.of(topLeftPlayerSlot.x + 25, topLeftPlayerSlot.y - 1);
	static {
		pouchRestockPositionerByScreen.put(AnvilScreen.class, FIX_HUMMEL);
		pouchRestockPositionerByScreen.put(KegScreen.class, RIGHT_FROM_TOP_RIGHT_SLOT);
		pouchRestockExclusionScreens.add(PouchScreen.class);
	}

	private final Minecraft MC;

	public LOTRGuiHandler(Minecraft mc) {
		MC = mc;
		MinecraftForge.EVENT_BUS.register(this);
	}

	private PouchRestockButton constructPouchRestockButton(Screen gui) {
		if (gui instanceof ContainerScreen && !pouchRestockExclusionScreens.contains(gui.getClass())) {
			ContainerScreen containerScreen = (ContainerScreen) gui;
			LOTRGuiHandler.PouchRestockButtonPositioner positioner = (LOTRGuiHandler.PouchRestockButtonPositioner) pouchRestockPositionerByScreen.getOrDefault(gui.getClass(), DEFAULT_ABOVE_TOP_RIGHT_SLOT);
			Optional<Pair<Integer, Integer>> optButtonCoords = PouchRestockButton.getRestockButtonPosition(MC, containerScreen, positioner);
			if (optButtonCoords.isPresent()) {
				Pair<Integer, Integer> buttonCoords = optButtonCoords.get();
				int buttonX = (Integer) buttonCoords.getLeft();
				int buttonY = (Integer) buttonCoords.getRight();
				return new PouchRestockButton(containerScreen, buttonX, buttonY, positioner, b -> {
					LOTRPacketHandler.sendToServer(new CPacketRestockPouches());
				});
			}
		}

		return null;
	}

	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
		Screen gui = event.getGui();
		if ((Boolean) LOTRConfig.CLIENT.modMainMenu.get() && gui != null && gui.getClass() == MainMenuScreen.class) {
			gui = new LOTRMainMenuScreen();
			event.setGui(gui);
		}

		if (gui instanceof ShulkerBoxScreen) {
			ShulkerBoxContainerFix.fixContainerSlots(((ShulkerBoxScreen) gui).getMenu(), MC.player);
		}

	}

	@SubscribeEvent
	public void postInitGui(Post event) {
		Screen gui = event.getGui();
		PouchRestockButton restockButton = constructPouchRestockButton(gui);
		if (restockButton != null) {
			event.addWidget(restockButton);
		}

	}

	@FunctionalInterface
	public interface PouchRestockButtonPositioner {
		Pair<Integer, Integer> getButtonPosition(Slot var1, Slot var2);
	}
}
