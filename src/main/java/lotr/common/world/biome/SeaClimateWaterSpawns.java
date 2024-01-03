package lotr.common.world.biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class SeaClimateWaterSpawns {
	private Map<EntityClassification, List<MobSpawnInfo.Spawners>> typeSpawns = new HashMap<>();

	public void add(Spawners spawners) {
		EntityClassification type = spawners.type.getCategory();
		if (type != EntityClassification.WATER_AMBIENT && type != EntityClassification.WATER_CREATURE) {
			throw new IllegalArgumentException("Warning: this is intended for WATER_AMBIENT or WATER_CREATURE types, but tried to add " + type + "!");
		}
		(typeSpawns.computeIfAbsent(type, t -> new ArrayList<>())).add(spawners);
	}

	public List<MobSpawnInfo.Spawners> getSpawns(EntityClassification creatureType) {
		return typeSpawns.computeIfAbsent(creatureType, t -> ImmutableList.of());
	}
}
