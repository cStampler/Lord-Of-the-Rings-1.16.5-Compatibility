package lotr.common.fac;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRDimensions;
import lotr.common.world.map.MapSettings;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class FactionSettings {
	private final List allRegions;
	private final Map regionsByDimension;
	private final Map regionsByName;
	private final Map regionsById;
	private List allFactions;
	private Map factionsByRegion;
	private Map factionsByName;
	private Map factionsById;
	private FactionRelationsTable relations;

	public FactionSettings(List regions) {
		allRegions = sortRegionsByOrder(regions);
		regionsByDimension = groupRegionsByDimensionAndSortOrder(allRegions);
		regionsByName = (Map) allRegions.stream().collect(Collectors.toMap(FactionRegion::getName, UnaryOperator.identity()));
		regionsById = (Map) allRegions.stream().collect(Collectors.toMap(FactionRegion::getAssignedId, UnaryOperator.identity()));
	}

	public List getAllPlayableAlignmentFactions() {
		return (List) streamFactions().filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).collect(Collectors.toList());
	}

	public Faction getFactionByID(int id) {
		return (Faction) factionsById.get(id);
	}

	public Faction getFactionByName(ResourceLocation name) {
		return (Faction) factionsByName.get(name);
	}

	public Faction getFactionByPointer(FactionPointer pointer) {
		return getFactionByName(pointer.getName());
	}

	public List getFactions() {
		return allFactions;
	}

	public List getFactionsForRegion(FactionRegion region) {
		return (List) factionsByRegion.get(region);
	}

	public List getFactionsOfTypes(FactionType... types) {
		return (List) streamFactions().filter(fac -> ((Faction) fac).isOfAnyType(types)).collect(Collectors.toList());
	}

	public List getPlayableFactionNames() {
		return (List) getAllPlayableAlignmentFactions().stream().map(hummel -> ((Faction) hummel).getName()).collect(Collectors.toList());
	}

	public FactionRegion getRegionByID(int id) {
		return (FactionRegion) regionsById.get(id);
	}

	public FactionRegion getRegionByName(ResourceLocation name) {
		return (FactionRegion) regionsByName.get(name);
	}

	public List getRegions() {
		return allRegions;
	}

	public List getRegionsForDimension(RegistryKey dim) {
		return (List) regionsByDimension.get(dim.location());
	}

	public List getRegionsForDimensionOrDefault(RegistryKey dim) {
		if (!regionsByDimension.containsKey(dim.location())) {
			dim = LOTRDimensions.MIDDLE_EARTH_WORLD_KEY;
		}

		return getRegionsForDimension(dim);
	}

	public FactionRelationsTable getRelations() {
		return relations;
	}

	public void postLoadValidateBiomes(World world) {
		allFactions.forEach(fac -> {
			((Faction) fac).postLoadValidateBiomes(world);
		});
	}

	public void setFactions(List facs) {
		if (allFactions != null) {
			throw new IllegalArgumentException("Cannot set faction list - already set!");
		}
		allFactions = sortFactionsByOrder(facs);
		factionsByRegion = groupFactionsByRegionAndSortOrder(allFactions);
		factionsById = (Map) allFactions.stream().collect(Collectors.toMap(Faction::getAssignedId, UnaryOperator.identity()));
		factionsByName = (Map) allFactions.stream().collect(Collectors.toMap(Faction::getName, UnaryOperator.identity()));
	}

	public void setRelations(FactionRelationsTable rels) {
		if (relations != null) {
			throw new IllegalArgumentException("Cannot set faction relations - already set!");
		}
		relations = rels;
	}

	public Stream streamFactions() {
		return allFactions.stream();
	}

	public Stream streamFactionsExcept(Faction except) {
		return streamFactions().filter(Predicate.isEqual(except).negate());
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(allRegions.size());
		allRegions.forEach(region -> {
			((FactionRegion) region).write(buf);
		});
		buf.writeVarInt(allFactions.size());
		allFactions.forEach(faction -> {
			((Faction) faction).write(buf);
		});
		relations.write(buf);
	}

	private static Map groupFactionsByRegionAndSortOrder(List factions) {
		Map unsortedMap = (Map) factions.stream().filter(fac -> (((Faction) fac).getRegion() != null)).collect(Collectors.groupingBy(Faction::getRegion));
		return (Map) unsortedMap.keySet().stream().collect(Collectors.toMap(UnaryOperator.identity(), region -> sortFactionsByOrder((List) unsortedMap.get(region))));
	}

	private static Map groupRegionsByDimensionAndSortOrder(List regions) {
		Map map = (Map) regions.stream().collect(Collectors.groupingBy(FactionRegion::getDimensionName));
		map.values().forEach(hummel -> FactionSettings.sortRegionsByOrder((List) hummel));
		return map;
	}

	public static FactionSettings read(MapSettings mapSettings, PacketBuffer buf) {
		List regions = new ArrayList();
		int numRegions = buf.readVarInt();

		for (int i = 0; i < numRegions; ++i) {
			try {
				FactionRegion region = FactionRegion.read(buf);
				if (region != null) {
					regions.add(region);
				}
			} catch (Exception var10) {
				LOTRLog.warn("Error loading a faction region from server");
				var10.printStackTrace();
			}
		}

		FactionSettings facSettings = new FactionSettings(regions);
		List factions = new ArrayList();
		int numFactions = buf.readVarInt();

		for (int i = 0; i < numFactions; ++i) {
			try {
				Faction faction = Faction.read(facSettings, mapSettings, buf);
				if (faction != null) {
					factions.add(faction);
				}
			} catch (Exception var9) {
				LOTRLog.warn("Error loading a faction from server");
				var9.printStackTrace();
			}
		}

		facSettings.setFactions(factions);
		FactionRelationsTable relations = FactionRelationsTable.read(facSettings, buf);
		facSettings.setRelations(relations);
		return facSettings;
	}

	private static List sortFactionsByOrder(List factions) {
		Collections.sort(factions, Comparator.comparingInt(Faction::getNullableRegionOrdering).thenComparingInt(Faction::getOrdering));
		return factions;
	}

	private static List sortRegionsByOrder(List regions) {
		Collections.sort(regions, Comparator.comparingInt(FactionRegion::getOrdering));
		return regions;
	}
}
