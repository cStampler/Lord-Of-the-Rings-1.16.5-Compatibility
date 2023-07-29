package lotr.common.world.biome;

import java.util.*;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class SeaClimateWaterSpawns {
	private Map typeSpawns = new HashMap();

	public void add(Spawners spawners) {
		EntityClassification type = spawners.type.getCategory();
		if (type != EntityClassification.WATER_AMBIENT && type != EntityClassification.WATER_CREATURE) {
			throw new IllegalArgumentException("Warning: this is intended for WATER_AMBIENT or WATER_CREATURE types, but tried to add " + type + "!");
		}
		((List) typeSpawns.computeIfAbsent(type, t -> new ArrayList())).add(spawners);
	}

	public List getSpawns(EntityClassification creatureType) {
		return (List) typeSpawns.computeIfAbsent(creatureType, t -> ImmutableList.of());
	}
}