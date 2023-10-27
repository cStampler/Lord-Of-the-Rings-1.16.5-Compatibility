package lotr.common.world.spawning;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;

public class NPCSpawnSettings {
	private final List spawnLists;
	private final Map spawnListsByName;
	private Map biomeSpawnLists;

	public NPCSpawnSettings(List spawnLists) {
		this.spawnLists = spawnLists;
		spawnListsByName = (Map) spawnLists.stream().collect(Collectors.toMap(NPCSpawnList::getName, UnaryOperator.identity()));
	}

	public Map getBiomeSpawnLists() {
		return biomeSpawnLists;
	}

	public NPCSpawnList getSpawnListByName(ResourceLocation name) {
		return (NPCSpawnList) spawnListsByName.get(name);
	}

	public List getSpawnLists() {
		return spawnLists;
	}

	public BiomeNPCSpawnList getSpawnsForBiomeOrFallbackEmpty(ResourceLocation biomeName) {
		return (BiomeNPCSpawnList) biomeSpawnLists.computeIfAbsent(biomeName, hummel -> BiomeNPCSpawnList.createDefaultEmptyList((ResourceLocation) hummel));
	}

	public void setBiomeSpawnLists(Map spawns) {
		if (biomeSpawnLists != null) {
			throw new IllegalArgumentException("Cannot set biomeSpawnLists - already set!");
		}
		biomeSpawnLists = spawns;
	}
}
