package lotr.common.world.spawning;

import java.util.*;

import com.google.gson.*;

import net.minecraft.util.*;

public class NPCSpawnList {
	private final List entries;
	private final ResourceLocation resourceName;

	private NPCSpawnList(ResourceLocation resourceName, List entries) {
		this.resourceName = resourceName;
		this.entries = entries;
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	public NPCSpawnEntry getRandomSpawnEntry(Random rand) {
		return (NPCSpawnEntry) WeightedRandom.getRandomItem(rand, entries);
	}

	public List getReadOnlyList() {
		return new ArrayList(entries);
	}

	public static NPCSpawnList read(ResourceLocation resourceName, JsonObject json) {
		JsonArray entryArray = json.get("entries").getAsJsonArray();
		List entries = new ArrayList();
		for (JsonElement elem : entryArray) {
			NPCSpawnEntry entry = NPCSpawnEntry.read(resourceName, elem.getAsJsonObject());
			if (entry != null) {
				entries.add(entry);
			}
		}

		return new NPCSpawnList(resourceName, entries);
	}
}
