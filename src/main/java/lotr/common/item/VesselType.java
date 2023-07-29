package lotr.common.item;

import java.util.Map;
import java.util.function.Supplier;

import lotr.common.init.LOTRItems;
import lotr.common.util.LOTRUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public enum VesselType {
	WOODEN_MUG(() -> LOTRItems.WOODEN_MUG.get(), "wooden_mug", "wooden_mug_drink", true), CERAMIC_MUG(() -> LOTRItems.CERAMIC_MUG.get(), "ceramic_mug", "ceramic_mug_drink", true), GOLDEN_GOBLET(() -> LOTRItems.GOLDEN_GOBLET.get(), "golden_goblet", "golden_goblet_drink", true), SILVER_GOBLET(() -> LOTRItems.SILVER_GOBLET.get(), "silver_goblet", "silver_goblet_drink", true), COPPER_GOBLET(() -> LOTRItems.COPPER_GOBLET.get(), "copper_goblet", "copper_goblet_drink", true), WOODEN_CUP(() -> LOTRItems.WOODEN_CUP.get(), "wooden_cup", "wooden_cup_drink", true), WATERSKIN(() -> LOTRItems.WATERSKIN.get(), "waterskin", "waterskin_drink", false), ALE_HORN(() -> LOTRItems.ALE_HORN.get(), "ale_horn", "ale_horn_drink", true), GOLDEN_ALE_HORN(() -> LOTRItems.GOLDEN_ALE_HORN.get(), "golden_ale_horn", "golden_ale_horn_drink", true);

	private static final Map NAME_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), hummel -> ((VesselType) hummel).getCodeName());
	private final Supplier itemSup;
	private final String vesselName;
	private final String emptyIconName;
	private final boolean isPlaceable;

	VesselType(Supplier item, String name, String iconName, boolean place) {
		itemSup = item;
		vesselName = name;
		emptyIconName = iconName;
		isPlaceable = place;
	}

	public ItemStack createEmpty() {
		return new ItemStack((IItemProvider) itemSup.get());
	}

	public String getCodeName() {
		return vesselName;
	}

	public String getEmptyIconName() {
		return emptyIconName;
	}

	public ResourceLocation getEmptySpritePath() {
		return new ResourceLocation("lotr", "item/" + emptyIconName);
	}

	public boolean isPlaceable() {
		return isPlaceable;
	}

	public static VesselType forName(String name) {
		return (VesselType) NAME_LOOKUP.getOrDefault(name, WOODEN_MUG);
	}
}
