package lotr.client;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.IntMath;

import lotr.client.gui.MiddleEarthMasterMenuScreen;
import lotr.common.data.AlignmentDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionRegion;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.init.LOTRDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class LOTRKeyHandler {
	private static final String KEY_CATEGORY_MOD = String.format("key.categories.mod.%s", "lotr");
	public static final KeyBinding KEY_BIND_MENU;
	public static final KeyBinding KEY_BIND_MAP_TELEPORT;
	public static final KeyBinding KEY_BIND_ALIGNMENT_PREVIOUS;
	public static final KeyBinding KEY_BIND_ALIGNMENT_NEXT;
	public static final KeyBinding KEY_BIND_ALIGNMENT_GROUP_PREVIOUS;
	public static final KeyBinding KEY_BIND_ALIGNMENT_GROUP_NEXT;
	private static final Map<KeyBinding, AlignmentKeyAction> ALIGNMENT_KEY_ACTIONS;
	private static int alignmentChangeTick;
	static {
		KEY_BIND_MENU = new KeyBinding(keybindName("menu"), 77, KEY_CATEGORY_MOD);
		KEY_BIND_MAP_TELEPORT = new KeyBinding(keybindName("map_teleport"), 257, KEY_CATEGORY_MOD);
		KEY_BIND_ALIGNMENT_PREVIOUS = new KeyBinding(keybindName("alignment_previous"), 263, KEY_CATEGORY_MOD);
		KEY_BIND_ALIGNMENT_NEXT = new KeyBinding(keybindName("alignment_next"), 262, KEY_CATEGORY_MOD);
		KEY_BIND_ALIGNMENT_GROUP_PREVIOUS = new KeyBinding(keybindName("alignment_group_previous"), 265, KEY_CATEGORY_MOD);
		KEY_BIND_ALIGNMENT_GROUP_NEXT = new KeyBinding(keybindName("alignment_group_next"), 264, KEY_CATEGORY_MOD);
		ALIGNMENT_KEY_ACTIONS = ImmutableMap.of(KEY_BIND_ALIGNMENT_PREVIOUS, new LOTRKeyHandler.AlignmentKeyAction(-1, 0), KEY_BIND_ALIGNMENT_NEXT, new LOTRKeyHandler.AlignmentKeyAction(1, 0), KEY_BIND_ALIGNMENT_GROUP_PREVIOUS, new LOTRKeyHandler.AlignmentKeyAction(0, -1), KEY_BIND_ALIGNMENT_GROUP_NEXT, new LOTRKeyHandler.AlignmentKeyAction(0, 1));
	}

	public LOTRKeyHandler() {
		MinecraftForge.EVENT_BUS.register(this);
		registerKeys();
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		int key = event.getKey();
		int scancode = event.getScanCode();
		int action = event.getAction();
		Minecraft mc = Minecraft.getInstance();
		if (KEY_BIND_MENU.matches(key, scancode) && mc.screen == null && mc.player != null) {
			Screen menuScreen = MiddleEarthMasterMenuScreen.openMenu(mc.player);
			if (menuScreen != null) {
				mc.setScreen(menuScreen);
			}
		}

		if (mc.screen == null && mc.player != null && (action == 1 || action == 2) && alignmentChangeTick <= 0) {
			Optional<AlignmentKeyAction> optAlignmentAction = ALIGNMENT_KEY_ACTIONS.entrySet().stream().filter(e -> (e.getKey()).matches(key, scancode)).map(e -> e.getValue()).findFirst();

			optAlignmentAction.ifPresent(alignmentAction -> {
				FactionSettings factionSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
				AlignmentDataModule alignData = LOTRLevelData.clientInstance().getData(mc.player).getAlignmentData();
				boolean changedAlignmentView = false;
				int factionShift = alignmentAction.factionShift;
				int groupShift = alignmentAction.groupShift;
				RegistryKey<World> currentDimension = LOTRDimensions.getCurrentLOTRDimensionOrFallback(mc.level);
				Faction currentFaction = alignData.getCurrentViewedFaction();
				if (currentFaction != null) {
					FactionRegion currentRegion = currentFaction.getRegion();
					List<FactionRegion> regionList = factionSettings.getRegionsForDimension(currentDimension);
					List<Faction> factionList = factionSettings.getFactionsForRegion(currentRegion);
					int i;
					if (factionShift != 0) {
						i = factionList.indexOf(currentFaction);
						i += factionShift;
						i = IntMath.mod(i, factionList.size());
						alignData.setCurrentViewedFaction((Faction) factionList.get(i));
						changedAlignmentView = true;
					}

					if (groupShift != 0 && regionList != null && currentRegion != null) {
						alignData.setRegionLastViewedFaction(currentRegion, currentFaction);
						i = regionList.indexOf(currentRegion);
						i += groupShift;
						i = IntMath.mod(i, regionList.size());
						alignData.setCurrentViewedFaction(alignData.getRegionLastViewedFaction((FactionRegion) regionList.get(i)));
						changedAlignmentView = true;
					}
				}

				if (changedAlignmentView) {
					alignData.sendViewedFactionsToServer();
					alignmentChangeTick = 2;
				}

			});
		}

	}

	private void registerKeys() {
		ClientRegistry.registerKeyBinding(KEY_BIND_MENU);
		ClientRegistry.registerKeyBinding(KEY_BIND_MAP_TELEPORT);
		ClientRegistry.registerKeyBinding(KEY_BIND_ALIGNMENT_PREVIOUS);
		ClientRegistry.registerKeyBinding(KEY_BIND_ALIGNMENT_NEXT);
		ClientRegistry.registerKeyBinding(KEY_BIND_ALIGNMENT_GROUP_PREVIOUS);
		ClientRegistry.registerKeyBinding(KEY_BIND_ALIGNMENT_GROUP_NEXT);
	}

	public static KeyBinding getFastTravelKey(Minecraft mc) {
		return mc.options.keySwapOffhand;
	}

	private static final String keybindName(String name) {
		return String.format("key.%s.%s", "lotr", name);
	}

	public static void updateAlignmentChange() {
		if (alignmentChangeTick > 0) {
			--alignmentChangeTick;
		}

	}

	private static final class AlignmentKeyAction {
		public final int factionShift;
		public final int groupShift;

		private AlignmentKeyAction(int faction, int group) {
			factionShift = faction;
			groupShift = group;
		}
	}
}
