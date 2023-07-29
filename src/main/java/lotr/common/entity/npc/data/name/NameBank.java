package lotr.common.entity.npc.data.name;

import java.util.*;

import net.minecraft.util.ResourceLocation;

public class NameBank {
	private final ResourceLocation path;
	private final List names;

	public NameBank(ResourceLocation path, List names) {
		this.path = path;
		this.names = names;
	}

	public String getRandomName(Random rand) {
		return names.isEmpty() ? String.format("Name bank %s was empty!", path) : (String) names.get(rand.nextInt(names.size()));
	}
}
