package lotr.common.world.map;

import java.util.Map;

import lotr.common.util.LOTRUtil;

public enum MapMarkerIcon {
	CROSS("cross", 0, 236), PICKAXE("pickaxe", 10, 236), SWORD("sword", 20, 236), CIRCLE("circle", 30, 236), EXCLAMATION("exclamation", 40, 236), QUESTION("question", 50, 236), STAR("star", 60, 236), ARROW("arrow", 70, 236), EYE("eye", 80, 236), TREE("tree", 90, 236), SNAKE("snake", 100, 236), MOUNTAIN("mountain", 110, 236), ANVIL("anvil", 120, 236), TOWER("tower", 130, 236), SHIP("ship", 140, 236), SKULL("skull", 150, 236), SACK("sack", 160, 236), CHEST("chest", 170, 236);

	private static final Map NAME_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), type -> ((MapMarkerIcon) type).name);
	private static final Map ID_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), type -> ((MapMarkerIcon) type).networkId);
	public final String name;
	public final int networkId;
	private final int iconU;
	private final int iconV;

	MapMarkerIcon(String name, int u, int v) {
		this.name = name;
		networkId = ordinal();
		iconU = u;
		iconV = v;
	}

	public int getU(boolean highlight) {
		return iconU;
	}

	public int getV(boolean highlight) {
		return iconV + (highlight ? 10 : 0);
	}

	public static MapMarkerIcon forNameOrDefault(String name) {
		return (MapMarkerIcon) NAME_LOOKUP.getOrDefault(name, CROSS);
	}

	public static MapMarkerIcon forNetworkIdOrDefault(int id) {
		return (MapMarkerIcon) ID_LOOKUP.getOrDefault(id, CROSS);
	}
}
