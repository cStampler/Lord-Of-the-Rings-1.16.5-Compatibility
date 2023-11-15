package lotr.common.fac;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.resources.CombinableMappingsResource;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class FactionRelationsTable extends CombinableMappingsResource<FactionPair, FactionRelation> {
	public FactionRelationsTable(Map<FactionPair, FactionRelation> relations, int loadOrder, int numCombinedFrom) {
		super(relations, loadOrder, numCombinedFrom);
	}

	public FactionRelation getRelation(Faction fac1, Faction fac2) {
		if (FactionPointers.UNALIGNED.matches(fac1) || FactionPointers.UNALIGNED.matches(fac2)) {
			return FactionRelation.NEUTRAL;
		}
		if (FactionPointers.HOSTILE.matches(fac1) || FactionPointers.HOSTILE.matches(fac2)) {
			return FactionRelation.MORTAL_ENEMY;
		}
		if (fac1 == fac2) {
			return FactionRelation.ALLY;
		}
		FactionPair key = FactionPair.of(fac1, fac2);
		return mappings.containsKey(key) ? (FactionRelation) mappings.get(key) : FactionRelation.NEUTRAL;
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(mappings.size());
		mappings.forEach((factionPair, relation) -> {
			buf.writeVarInt(((FactionPair) factionPair).getFirst().getAssignedId());
			buf.writeVarInt(((FactionPair) factionPair).getSecond().getAssignedId());
			buf.writeVarInt(((FactionRelation) relation).networkID);
		});
		buf.writeVarInt(getNumCombinedFrom());
	}

	public static FactionRelationsTable combine(List<FactionRelationsTable> relationsTables) {
		return combine(relationsTables, FactionRelationsTable::new);
	}

	public static FactionRelationsTable read(FactionSettings facSettings, PacketBuffer buf) {
	    Map<FactionPair, FactionRelation> relations = new HashMap<>();
	    int numRelations = buf.readVarInt();
	    for (int i = 0; i < numRelations; i++) {
	      int facId1 = buf.readVarInt();
	      int facId2 = buf.readVarInt();
	      int relationId = buf.readVarInt();
	      Faction fac1 = facSettings.getFactionByID(facId1);
	      Faction fac2 = facSettings.getFactionByID(facId2);
	      FactionRelation relation = FactionRelation.forNetworkID(relationId);
	      if (fac1 == null) {
	        LOTRLog.warn("Received a faction relation from server with a nonexistent faction 1 ID (%d)", new Object[] { Integer.valueOf(facId1) });
	      } else if (fac2 == null) {
	        LOTRLog.warn("Received a faction relation from server with a nonexistent faction 2 ID (%d)", new Object[] { Integer.valueOf(facId2) });
	      } else if (relation == null) {
	        LOTRLog.warn("Received a faction relation from server with a nonexistent relation ID (%d)", new Object[] { Integer.valueOf(relationId) });
	      } else {
	        relations.put(FactionPair.of(fac1, fac2), relation);
	      } 
	    } 
	    int numCombinedFrom = buf.readVarInt();
	    return new FactionRelationsTable(relations, 0, numCombinedFrom);
	  }

	public static FactionRelationsTable read(FactionSettings facSettings, ResourceLocation relationsTableName, JsonObject json) {
	    int loadOrder = json.get("load_order").getAsInt();
	    Map<FactionPair, FactionRelation> relations = new HashMap<>();
	    JsonObject relationsJson = json.get("relations").getAsJsonObject();
	    for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)relationsJson.entrySet()) {
	      String key = entry.getKey();
	      JsonElement value = entry.getValue();
	      try {
	        List<String> splitKey = (List<String>)Stream.<String>of(key.split(",")).map(String::trim).collect(Collectors.toList());
	        if (splitKey.size() == 2) {
	          String facName1 = splitKey.get(0);
	          String facName2 = splitKey.get(1);
	          Faction fac1 = facSettings.getFactionByName(new ResourceLocation(facName1));
	          Faction fac2 = facSettings.getFactionByName(new ResourceLocation(facName2));
	          if (fac1 == null) {
	            LOTRLog.warn("Faction relations table %s references faction %s - but no such faction exists", new Object[] { relationsTableName, facName1 });
	            continue;
	          } 
	          if (fac2 == null) {
	            LOTRLog.warn("Faction relations table %s references faction %s - but no such faction exists", new Object[] { relationsTableName, facName2 });
	            continue;
	          } 
	          if (fac1 == fac2) {
	            LOTRLog.warn("Faction relations table %s cannot declare a faction's relation to itself (%s)", new Object[] { relationsTableName, facName1 });
	            continue;
	          } 
	          FactionPair factionPair = FactionPair.of(fac1, fac2);
	          if (relations.containsKey(factionPair)) {
	            LOTRLog.warn("Faction relations table %s contains duplicate key for pair (%s, %s) - relations were already declared as %s", new Object[] { relationsTableName, facName1, facName2, ((FactionRelation)relations
	                  .get(factionPair)).codeName });
	            continue;
	          } 
	          String relationName = value.getAsString();
	          FactionRelation relation = FactionRelation.forName(relationName);
	          if (relation != null) {
	            relations.put(factionPair, relation);
	            continue;
	          } 
	          LOTRLog.warn("Faction relations table %s references relation %s - but no such relation exists", new Object[] { relationsTableName, relationName });
	          continue;
	        } 
	        LOTRLog.warn("Couldn't parse a faction relations key in table %s: key = %s - expected two factions separated by a comma", new Object[] { relationsTableName, key });
	      } catch (Exception e) {
	        LOTRLog.warn("Couldn't parse a faction relations line in table %s: key = %s, value = %s", new Object[] { relationsTableName, key, value });
	        e.printStackTrace();
	      } 
	    } 
	    return new FactionRelationsTable(relations, loadOrder, 0);
	  }
}
