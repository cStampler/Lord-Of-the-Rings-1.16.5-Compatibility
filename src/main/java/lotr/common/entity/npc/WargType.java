package lotr.common.entity.npc;

import java.util.Map;

import lotr.common.util.LOTRUtil;
import net.minecraft.util.ResourceLocation;

public enum WargType {
	BROWN(0, "brown"), GREY(1, "grey"), BLACK(2, "black"), SILVER(3, "silver"), WHITE(4, "white");

	private static final Map ID_LOOKUP = LOTRUtil.createKeyedEnumMap(values(), hummel -> ((WargType) hummel).getId());
	private final int id;
	private final ResourceLocation texture;

	WargType(int id, String name) {
		this.id = id;
		texture = new ResourceLocation("lotr", String.format("textures/entity/warg/%s.png", name));
	}

	public int getId() {
		return id;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public static WargType forId(int id) {
		return (WargType) ID_LOOKUP.getOrDefault(id, BROWN);
	}
}
