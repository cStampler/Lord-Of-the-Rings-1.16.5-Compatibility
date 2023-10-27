package lotr.common.fac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRDimensions;
import lotr.common.world.map.MapSettings;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class FactionSettings {
	private final List<FactionRegion> allRegions;
	private final Map<ResourceLocation, List<FactionRegion>> regionsByDimension;
	private final Map<ResourceLocation, FactionRegion> regionsByName;
	private final Map<Integer, FactionRegion> regionsById;
	private List<Faction> allFactions;
	private Map<FactionRegion, List<Faction>> factionsByRegion;
	private Map<ResourceLocation, Faction> factionsByName;
	private Map<Integer, Faction> factionsById;
	private FactionRelationsTable relations;

	public FactionSettings(List<FactionRegion> regions) {
		allRegions = sortRegionsByOrder(regions);
		regionsByDimension = groupRegionsByDimensionAndSortOrder(allRegions);
		regionsByName = allRegions.stream().collect(Collectors.toMap(FactionRegion::getName, UnaryOperator.identity()));
		regionsById = allRegions.stream().collect(Collectors.toMap(FactionRegion::getAssignedId, UnaryOperator.identity()));
	}

	public List<Faction> getAllPlayableAlignmentFactions() {
		return streamFactions().filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).collect(Collectors.toList());
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

	public List<Faction> getFactions() {
		return allFactions;
	}

	public List<Faction> getFactionsForRegion(FactionRegion region) {
		return factionsByRegion.get(region);
	}

	public List<Faction> getFactionsOfTypes(FactionType... types) {
		return (List<Faction>) streamFactions().filter(fac -> ((Faction) fac).isOfAnyType(types)).collect(Collectors.toList());
	}

	public List<ResourceLocation> getPlayableFactionNames() {
		return getAllPlayableAlignmentFactions().stream().map(hummel -> ((Faction) hummel).getName()).collect(Collectors.toList());
	}

	public FactionRegion getRegionByID(int id) {
		return (FactionRegion) regionsById.get(id);
	}

	public FactionRegion getRegionByName(ResourceLocation name) {
		return (FactionRegion) regionsByName.get(name);
	}

	public List<FactionRegion> getRegions() {
		return allRegions;
	}

	public List<FactionRegion> getRegionsForDimension(RegistryKey<World> dim) {
		return regionsByDimension.get(dim.location());
	}

	public List<FactionRegion> getRegionsForDimensionOrDefault(RegistryKey<World> dim) {
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

	public void setFactions(List<Faction> facs) {
		if (allFactions != null) {
			throw new IllegalArgumentException("Cannot set faction list - already set!");
		}
		allFactions = sortFactionsByOrder(facs);
		factionsByRegion = groupFactionsByRegionAndSortOrder(allFactions);
		factionsById = allFactions.stream().collect(Collectors.toMap(Faction::getAssignedId, UnaryOperator.identity()));
		factionsByName = allFactions.stream().collect(Collectors.toMap(Faction::getName, UnaryOperator.identity()));
	}

	public void setRelations(FactionRelationsTable rels) {
		if (relations != null) {
			throw new IllegalArgumentException("Cannot set faction relations - already set!");
		}
		relations = rels;
	}

	public Stream<Faction> streamFactions() {
		return allFactions.stream();
	}

	public Stream<Faction> streamFactionsExcept(Faction except) {
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

	private static Map<FactionRegion, List<Faction>> groupFactionsByRegionAndSortOrder(List<Faction> factions) {
		Map<FactionRegion, List<Faction>> unsortedMap = factions.stream().filter(fac -> (((Faction) fac).getRegion() != null)).collect(Collectors.groupingBy(Faction::getRegion));
		return unsortedMap.keySet().stream().collect(Collectors.toMap(UnaryOperator.identity(), region -> sortFactionsByOrder((List<Faction>)unsortedMap.get(region))));
	}

	private static Map<ResourceLocation, List<FactionRegion>> groupRegionsByDimensionAndSortOrder(List<FactionRegion> regions) {
		Map<ResourceLocation, List<FactionRegion>> map = regions.stream().collect(Collectors.groupingBy(FactionRegion::getDimensionName));
		map.values().forEach(hummel -> FactionSettings.sortRegionsByOrder(hummel));
		return map;
	}

	public static FactionSettings read(MapSettings mapSettings, PacketBuffer buf) {
		List<FactionRegion> regions = new ArrayList<FactionRegion>();
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
		List<Faction> factions = new ArrayList<Faction>();
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

	private static List<Faction> sortFactionsByOrder(List<Faction> factions) {
		Collections.sort(factions, Comparator.comparingInt(Faction::getNullableRegionOrdering).thenComparingInt(Faction::getOrdering));
		return factions;
	}

	private static List<FactionRegion> sortRegionsByOrder(List<FactionRegion> regions) {
		Collections.sort(regions, Comparator.comparingInt(FactionRegion::getOrdering));
		return regions;
	}
}
