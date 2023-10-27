package lotr.common.world.spawning;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.entity.npc.data.NPCEntitySettingsManager;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom.Item;
import net.minecraft.util.math.MathHelper;

public class NPCSpawnEntry extends Item {
	private final NPCEntityTypeProvider typeProvider;
	private final int minCount;
	private final int maxCount;

	private NPCSpawnEntry(NPCEntityTypeProvider typeProvider, int weight, int min, int max) {
		super(weight);
		this.typeProvider = typeProvider;
		minCount = min;
		maxCount = max;
	}

	public Stream getAllPossibleTypes() {
		return typeProvider.getAllPossibleTypes();
	}

	private int getRandomGroupSize(Random rand) {
		return MathHelper.nextInt(rand, minCount, maxCount);
	}

	public static NPCSpawnEntry read(ResourceLocation resourceName, JsonObject json) {
		NPCEntityTypeProvider typeProvider = null;
		if (json.has("entity_type")) {
			String typeName = json.get("entity_type").getAsString();
			ResourceLocation typeNameRes = new ResourceLocation(typeName);
			EntityType entityType = NPCEntitySettingsManager.lookupEntityTypeByName(typeNameRes);
			if (entityType == null) {
				LOTRLog.warn("Failed to load an entry within NPC spawn list %s - nonexistent single entity type %s", resourceName, typeName);
				return null;
			}

			typeProvider = new NPCEntityTypeProvider.Single(entityType);
		} else {
			if (!json.has("mixed_entity_types")) {
				LOTRLog.warn("Failed to load NPC spawn entry in list %s - found neither a single entity_type nor a mixed_entity_types array", resourceName);
				return null;
			}

			List mixedTypeEntries = new ArrayList();
			JsonArray mixedEntityTypes = json.get("mixed_entity_types").getAsJsonArray();
			for (JsonElement elem : mixedEntityTypes) {
				JsonObject singleTypeJson = elem.getAsJsonObject();
				String typeName = singleTypeJson.get("entity_type").getAsString();
				ResourceLocation typeNameRes = new ResourceLocation(typeName);
				EntityType entityType = NPCEntitySettingsManager.lookupEntityTypeByName(typeNameRes);
				if (entityType == null) {
					LOTRLog.warn("Failed to load an entry within NPC spawn list %s - nonexistent entity type %s in mixed_entity_types", resourceName, typeName);
					return null;
				}

				int weight = singleTypeJson.get("weight").getAsInt();
				mixedTypeEntries.add(new NPCEntityTypeProvider.MixedEntry(entityType, weight));
			}

			typeProvider = new NPCEntityTypeProvider.Mixed(mixedTypeEntries);
		}

		int weight = json.get("weight").getAsInt();
		int minCount = json.get("min_count").getAsInt();
		int maxCount = json.get("max_count").getAsInt();
		return new NPCSpawnEntry(typeProvider, weight, minCount, maxCount);
	}

	public static class EntryInContext {
		private final NPCSpawnEntry spawnEntry;
		private final boolean isConquestSpawn;

		public EntryInContext(NPCSpawnEntry entry, boolean conquest) {
			spawnEntry = entry;
			isConquestSpawn = conquest;
		}

		public int getRandomGroupSize(Random rand) {
			return spawnEntry.getRandomGroupSize(rand);
		}

		public EntityType getTypeToSpawn(Random rand) {
			return spawnEntry.typeProvider.getTypeToSpawn(rand);
		}

		public boolean isConquestSpawn() {
			return isConquestSpawn;
		}
	}
}
