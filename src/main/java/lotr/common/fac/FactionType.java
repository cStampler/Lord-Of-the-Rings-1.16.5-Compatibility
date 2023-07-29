package lotr.common.fac;

import java.util.Map;

import lotr.common.util.LOTRUtil;
import net.minecraft.util.ResourceLocation;

public enum FactionType {
	FREE_PEOPLE("free_people"), ELF("elf"), MAN("man"), DWARF("dwarf"), ORC("orc"), TROLL("troll"), TREE("tree");

	private static final Map NAME_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), type -> ((FactionType) type).namespacedID);
	private static final Map ID_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), type -> ((FactionType) type).networkID);
	public final ResourceLocation namespacedID;
	public final int networkID;

	FactionType(String name) {
		namespacedID = new ResourceLocation("lotr", name);
		networkID = ordinal();
	}

	public static FactionType forName(ResourceLocation name) {
		return (FactionType) NAME_LOOKUP.get(name);
	}

	public static FactionType forNetworkID(int id) {
		return (FactionType) ID_LOOKUP.get(id);
	}
}
