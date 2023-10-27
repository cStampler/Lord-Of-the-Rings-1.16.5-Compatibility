package lotr.common.world.spawning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.fac.FactionPointer;
import lotr.common.fac.FactionSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BiomeNPCSpawnList {
	private final ResourceLocation biomeName;
	private final List factionContainers;
	private final List presentFactions;
	private final double npcDensity;
	private final boolean allowDarknessSpawnsInDaytime;

	private BiomeNPCSpawnList(ResourceLocation biomeName, List factionContainers, double npcDensity, boolean allowDarknessSpawnsInDaytime) {
		this.biomeName = biomeName;
		this.factionContainers = factionContainers;
		presentFactions = (List) factionContainers.stream().map(hummel -> ((BiomeNPCSpawnList.FactionContainer) hummel).getFaction()).distinct().collect(Collectors.toList());
		this.npcDensity = npcDensity;
		this.allowDarknessSpawnsInDaytime = allowDarknessSpawnsInDaytime;
	}

	public boolean allowsDarknessSpawnsInDaytime() {
		return allowDarknessSpawnsInDaytime;
	}

	public boolean containsEntityClassByDefault(Class desiredClass, World world) {
		Iterator var3 = factionContainers.iterator();

		while (true) {
			BiomeNPCSpawnList.FactionContainer facCont;
			do {
				do {
					if (!var3.hasNext()) {
						return false;
					}

					facCont = (BiomeNPCSpawnList.FactionContainer) var3.next();
				} while (facCont.isEmpty());
			} while (facCont.isConquestOnlyFaction());

			Iterator var5 = facCont.spawnLists.iterator();

			while (var5.hasNext()) {
				BiomeNPCSpawnList.SpawnListContainer listCont = (BiomeNPCSpawnList.SpawnListContainer) var5.next();
				NPCSpawnList list = listCont.spawnList;
				Iterator var8 = list.getReadOnlyList().iterator();

				while (var8.hasNext()) {
					NPCSpawnEntry e = (NPCSpawnEntry) var8.next();
					if (e.getAllPossibleTypes().anyMatch(entityType -> {
						Entity proxyEntity = ((EntityType) entityType).create(world);
						return desiredClass.isAssignableFrom(proxyEntity.getClass());
					})) {
						return true;
					}
				}
			}
		}
	}

	public List getAllSpawnEntries(World world) {
		List spawns = new ArrayList();
		Iterator var3 = factionContainers.iterator();

		while (true) {
			BiomeNPCSpawnList.FactionContainer facCont;
			do {
				if (!var3.hasNext()) {
					return spawns;
				}

				facCont = (BiomeNPCSpawnList.FactionContainer) var3.next();
			} while (facCont.isEmpty());

			Iterator var5 = facCont.spawnLists.iterator();

			while (var5.hasNext()) {
				BiomeNPCSpawnList.SpawnListContainer listCont = (BiomeNPCSpawnList.SpawnListContainer) var5.next();
				NPCSpawnList list = listCont.spawnList;
				spawns.addAll(list.getReadOnlyList());
			}
		}
	}

	public double getNPCDensity() {
		return npcDensity;
	}

	public NPCSpawnEntry.EntryInContext getRandomSpawnEntry(Random rand, World world, BlockPos pos) {
		int totalWeight = 0;
		Map cachedFacWeights = new HashMap();
		Map cachedConqStrengths = new HashMap();
		Iterator var7 = factionContainers.iterator();

		while (var7.hasNext()) {
			BiomeNPCSpawnList.FactionContainer cont = (BiomeNPCSpawnList.FactionContainer) var7.next();
			if (!cont.isEmpty()) {
				float conq = 0.0F;
				int weight = cont.getFactionWeight(conq);
				if (weight > 0) {
					totalWeight += weight;
					cachedFacWeights.put(cont, weight);
					cachedConqStrengths.put(cont, conq);
				}
			}
		}

		if (totalWeight > 0) {
			BiomeNPCSpawnList.FactionContainer chosenFacContainer = null;
			boolean isConquestSpawn = false;
			int w = rand.nextInt(totalWeight);
			Iterator var16 = factionContainers.iterator();

			while (var16.hasNext()) {
				BiomeNPCSpawnList.FactionContainer cont = (BiomeNPCSpawnList.FactionContainer) var16.next();
				if (!cont.isEmpty() && cachedFacWeights.containsKey(cont)) {
					int facWeight = (Integer) cachedFacWeights.get(cont);
					w -= facWeight;
					if (w < 0) {
						chosenFacContainer = cont;
						if (facWeight > cont.baseWeight) {
							isConquestSpawn = rand.nextFloat() < (float) (facWeight - cont.baseWeight) / (float) facWeight;
						}
						break;
					}
				}
			}

			if (chosenFacContainer != null) {
				float conq = (Float) cachedConqStrengths.get(chosenFacContainer);
				BiomeNPCSpawnList.SpawnListContainer spawnList = chosenFacContainer.getRandomSpawnList(rand, conq);
				if (spawnList == null || spawnList.spawnList == null) {
					LOTRLog.error("WARNING anticipating NPE in biome %s, faction %s", biomeName, chosenFacContainer.getFaction().getName());
				}

				NPCSpawnEntry entry = spawnList.spawnList.getRandomSpawnEntry(rand);
				return new NPCSpawnEntry.EntryInContext(entry, isConquestSpawn);
			}
		}

		return null;
	}

	public boolean isFactionPresent(FactionPointer fac) {
		return presentFactions.contains(fac);
	}

	public static BiomeNPCSpawnList createDefaultEmptyList(ResourceLocation biomeName) {
		return new BiomeNPCSpawnList(biomeName, ImmutableList.of(), 1.0D, false);
	}

	public static class FactionContainer {
		private final FactionPointer theConquestFaction;
		private final int baseWeight;
		private final List spawnLists;

		private FactionContainer(FactionPointer fac, int w, List lists) {
			theConquestFaction = fac;
			baseWeight = w;
			spawnLists = lists;
		}

		public FactionPointer getFaction() {
			return theConquestFaction;
		}

		public int getFactionWeight(float conq) {
			if (conq > 0.0F) {
				float conqFactor = conq * 0.2F * 1.0F;
				return baseWeight + Math.round(conqFactor);
			}
			return baseWeight;
		}

		public BiomeNPCSpawnList.SpawnListContainer getRandomSpawnList(Random rand, float conq) {
			int totalWeight = 0;
			Iterator var4 = spawnLists.iterator();

			while (var4.hasNext()) {
				BiomeNPCSpawnList.SpawnListContainer cont = (BiomeNPCSpawnList.SpawnListContainer) var4.next();
				if (cont.canSpawnAtConquestLevel(conq)) {
					totalWeight += cont.weight;
				}
			}

			if (totalWeight <= 0) {
				return null;
			}
			BiomeNPCSpawnList.SpawnListContainer chosenList = null;
			int w = rand.nextInt(totalWeight);
			Iterator var6 = spawnLists.iterator();

			while (var6.hasNext()) {
				BiomeNPCSpawnList.SpawnListContainer cont = (BiomeNPCSpawnList.SpawnListContainer) var6.next();
				if (cont.canSpawnAtConquestLevel(conq)) {
					w -= cont.weight;
					if (w < 0) {
						chosenList = cont;
						break;
					}
				}
			}

			return chosenList;
		}

		public boolean isConquestOnlyFaction() {
			return baseWeight <= 0;
		}

		public boolean isEmpty() {
			return spawnLists.isEmpty();
		}

		public static BiomeNPCSpawnList.FactionContainer read(NPCSpawnSettings spawnSettings, FactionSettings factionSettings, ResourceLocation resourceName, JsonObject json) {
			FactionPointer faction = FactionPointer.of(new ResourceLocation(json.get("faction").getAsString()));
			if (factionSettings.getFactionByPointer(faction) == null) {
				LOTRLog.warn("Error loading biome NPC spawn list for %s - nonexistent faction '%s'", resourceName, faction.getName());
				return null;
			}
			int baseWeight = json.get("base_weight").getAsInt();
			List spawnLists = new ArrayList();
			JsonArray spawnListsArray = json.get("lists").getAsJsonArray();
			for (JsonElement elem : spawnListsArray) {
				try {
					BiomeNPCSpawnList.SpawnListContainer spawnList = BiomeNPCSpawnList.SpawnListContainer.read(spawnSettings, resourceName, faction, elem.getAsJsonObject());
					if (spawnList != null) {
						spawnLists.add(spawnList);
					}
				} catch (Exception var11) {
					LOTRLog.warn("Error loading a spawn list within faction spawn list %s in biome NPC spawn list for %s", faction.getName(), resourceName);
					var11.printStackTrace();
				}
			}

			return new BiomeNPCSpawnList.FactionContainer(faction, baseWeight, spawnLists);
		}
	}

	public static class PreLoaded {
		private final ResourceLocation biomeName;
		private final ResourceLocation parentName;
		private final List factionContainers;
		private final Double npcDensity;
		private final Boolean allowDarknessSpawnsInDaytime;
		private final Double conquestGainRate;

		private PreLoaded(ResourceLocation biomeName, ResourceLocation parentName, List factionContainers, Double npcDensity, Boolean allowDarknessSpawnsInDaytime, Double conquestGainRate) {
			this.biomeName = biomeName;
			this.parentName = parentName;
			this.factionContainers = factionContainers;
			this.npcDensity = npcDensity;
			this.allowDarknessSpawnsInDaytime = allowDarknessSpawnsInDaytime;
			this.conquestGainRate = conquestGainRate;
		}

		public BiomeNPCSpawnList resolveParentAndFinaliseAfterLoad(Map allLoadedLists) {
			List final_factionContainers = factionContainers;
			Double final_npcDensity = npcDensity;
			Boolean final_allowDarknessSpawnsInDaytime = allowDarknessSpawnsInDaytime;
			Double final_conquestGainRate = conquestGainRate;
			if (parentName != null) {
				BiomeNPCSpawnList.PreLoaded parent = (BiomeNPCSpawnList.PreLoaded) allLoadedLists.get(parentName);
				if (parent == null) {
					LOTRLog.warn("Error loading biome NPC spawn list %s - parent '%s' could not be resolved", biomeName, parentName);
					return null;
				}

				if (!checkForNoCircularInheritance(this, allLoadedLists)) {
					return null;
				}

				final_factionContainers = (List) inheritFromParent(final_factionContainers, parent.factionContainers);
				final_npcDensity = (Double) inheritFromParent(final_npcDensity, parent.npcDensity);
				final_allowDarknessSpawnsInDaytime = (Boolean) inheritFromParent(final_allowDarknessSpawnsInDaytime, parent.allowDarknessSpawnsInDaytime);
				final_conquestGainRate = (Double) inheritFromParent(final_conquestGainRate, parent.conquestGainRate);
			}

			final_factionContainers = (List) fallbackIfNull(final_factionContainers, new ArrayList());
			final_npcDensity = (Double) fallbackIfNull(final_npcDensity, 1.0D);
			final_allowDarknessSpawnsInDaytime = (Boolean) fallbackIfNull(final_allowDarknessSpawnsInDaytime, false);
			final_conquestGainRate = (Double) fallbackIfNull(final_conquestGainRate, 1.0D);
			return new BiomeNPCSpawnList(biomeName, final_factionContainers, final_npcDensity, final_allowDarknessSpawnsInDaytime);
		}

		private static boolean checkForNoCircularInheritance(BiomeNPCSpawnList.PreLoaded loader, Map allLoadedLists) {
			Set inheritanceTrail = new HashSet();

			for (BiomeNPCSpawnList.PreLoaded current = loader; current != null; current = current.parentName != null ? (BiomeNPCSpawnList.PreLoaded) allLoadedLists.get(current.parentName) : null) {
				if (inheritanceTrail.contains(current.biomeName)) {
					LOTRLog.warn("Error loading biome NPC spawn list %s - circular reference somewhere in 'parent' trail! Terminated at file %s, depth %d", loader.biomeName, current.biomeName, inheritanceTrail.size());
					return false;
				}

				inheritanceTrail.add(current.biomeName);
			}

			return true;
		}

		private static Object fallbackIfNull(Object ownValue, Object defaultValue) {
			return ownValue != null ? ownValue : defaultValue;
		}

		private static Object inheritFromParent(Object ownValue, Object parentValue) {
			return ownValue != null ? ownValue : parentValue;
		}

		public static BiomeNPCSpawnList.PreLoaded read(NPCSpawnSettings spawnSettings, FactionSettings factionSettings, ResourceLocation resourceName, JsonObject json) {
			ResourceLocation parentName = null;
			if (json.has("parent")) {
				parentName = new ResourceLocation(json.get("parent").getAsString());
				if (parentName.equals(resourceName)) {
					LOTRLog.warn("Error loading biome NPC spawn list for %s - cannot be its own parent!");
					return null;
				}
			}

			List factionSpawnLists = null;
			if (json.has("faction_spawn_lists")) {
				factionSpawnLists = new ArrayList();
				JsonArray factionSpawnListsArray = json.get("faction_spawn_lists").getAsJsonArray();
				for (JsonElement elem : factionSpawnListsArray) {
					try {
						BiomeNPCSpawnList.FactionContainer factionSpawnList = BiomeNPCSpawnList.FactionContainer.read(spawnSettings, factionSettings, resourceName, elem.getAsJsonObject());
						if (factionSpawnList != null) {
							factionSpawnLists.add(factionSpawnList);
						}
					} catch (Exception var10) {
						LOTRLog.warn("Error loading a faction spawn list in biome NPC spawn list for %s", resourceName);
						var10.printStackTrace();
					}
				}
			}

			Double npcDensity = null;
			if (json.has("npc_density")) {
				npcDensity = json.get("npc_density").getAsDouble();
			}

			Boolean allowDarknessSpawnsInDaytime = null;
			if (json.has("allow_darkness_spawns_in_daytime")) {
				allowDarknessSpawnsInDaytime = json.get("allow_darkness_spawns_in_daytime").getAsBoolean();
			}

			Double conquestGainRate = null;
			if (json.has("conquest_gain_rate")) {
				conquestGainRate = json.get("conquest_gain_rate").getAsDouble();
			}

			return new BiomeNPCSpawnList.PreLoaded(resourceName, parentName, factionSpawnLists, npcDensity, allowDarknessSpawnsInDaytime, conquestGainRate);
		}
	}

	public static class SpawnListContainer {
		private final NPCSpawnList spawnList;
		private final int weight;

		private SpawnListContainer(NPCSpawnList list, int w) {
			spawnList = list;
			weight = w;
		}

		public boolean canSpawnAtConquestLevel(float conq) {
			return conq > -1.0F;
		}

		public boolean isConquestOnly() {
			return false;
		}

		public static BiomeNPCSpawnList.SpawnListContainer read(NPCSpawnSettings spawnSettings, ResourceLocation resourceName, FactionPointer faction, JsonObject json) {
			ResourceLocation listName = new ResourceLocation(json.get("list").getAsString());
			NPCSpawnList list = spawnSettings.getSpawnListByName(listName);
			if (list == null) {
				LOTRLog.warn("Error loading biome NPC spawn list for %s - nonexistent NPC spawn list '%s' within faction spawn list %s", resourceName, listName, faction.getName());
				return null;
			}
			int weight = json.get("weight").getAsInt();
			return new BiomeNPCSpawnList.SpawnListContainer(list, weight);
		}
	}
}
