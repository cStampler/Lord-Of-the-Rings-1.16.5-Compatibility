package lotr.common.fac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketFactionSettings;
import lotr.common.resources.InstancedJsonReloadListener;
import lotr.common.resources.PostServerLoadedValidator;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

public class FactionSettingsManager extends InstancedJsonReloadListener implements PostServerLoadedValidator {
	private static final FactionSettingsManager CLIENT_INSTANCE;
	private static final FactionSettingsManager SERVER_INSTANCE;
	static {
		CLIENT_INSTANCE = new FactionSettingsManager(LogicalSide.CLIENT);
		SERVER_INSTANCE = new FactionSettingsManager(LogicalSide.SERVER);
		PostServerLoadedValidator.validators.add(SERVER_INSTANCE);
	}

	private FactionSettings currentLoadedFactions;

	private FactionSettingsManager(LogicalSide side) {
		super("factions", "FactionSettings", side);
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, IResourceManager serverResMgr, IProfiler profiler) {
		Map<ResourceLocation, JsonObject> regionJsons = filterDataJsonsBySubFolder(jsons, "regions/");
		Map<ResourceLocation, JsonObject> factionJsons = filterDataJsonsByRootFolderOnly(jsons);
		Map<ResourceLocation, JsonObject> relationsJsons = filterDataJsonsBySubFolder(jsons, "relations/");
		MapSettings mapSettings = MapSettingsManager.serverInstance().getCurrentLoadedMap();
		currentLoadedFactions = loadFactionsFromJsons(regionJsons, factionJsons, relationsJsons, mapSettings);
		logFactionsLoad("Loaded serverside faction settings", currentLoadedFactions);
	}

	public FactionSettings getCurrentLoadedFactions() {
		return currentLoadedFactions;
	}

	public void loadClientFactionsFromServer(IResourceManager resMgr, FactionSettings facSettings) {
		currentLoadedFactions = facSettings;
		logFactionsLoad("Loaded clientside faction settings from server", currentLoadedFactions);
	}

	private FactionSettings loadFactionsFromJsons(Map<ResourceLocation, JsonObject> regionJsons, Map<ResourceLocation, JsonObject> factionJsons, Map<ResourceLocation, JsonObject> relationsJsons, MapSettings mapSettings) {
		List<FactionRegion> regions = new ArrayList<>();
		int nextRegionId = 0;
	    for (Map.Entry<ResourceLocation, JsonObject> entry : regionJsons.entrySet()) {
	      ResourceLocation res = entry.getKey();
	      ResourceLocation regionName = trimSubFolderResource(res, "regions/");
	      JsonObject regionJson = entry.getValue();
	      try {
	        FactionRegion region = FactionRegion.read(regionName, regionJson, nextRegionId);
	        if (region != null)
	          regions.add(region); 
	        nextRegionId++;
	      } catch (Exception e) {
	        LOTRLog.warn("Failed to load faction region %s from file", new Object[] { regionName });
	        e.printStackTrace();
	      } 
	    } 

	    FactionSettings facSettings = new FactionSettings(regions);
	    List<Faction> factions = new ArrayList<>();
	    int nextFactionId = 0;
	    nextFactionId = TechnicalFactions.registerTechnicalFactions(facSettings, mapSettings, factions, nextFactionId);
	    for (Map.Entry<ResourceLocation, JsonObject> entry : factionJsons.entrySet()) {
	      ResourceLocation res = entry.getKey();
	      ResourceLocation factionName = new ResourceLocation(res.getNamespace(), res.getPath());
	      JsonObject factionJson = entry.getValue();
	      try {
	        Faction faction = Faction.read(facSettings, factionName, factionJson, nextFactionId, mapSettings);
	        if (faction != null)
	          factions.add(faction); 
	        nextFactionId++;
	      } catch (Exception e) {
	        LOTRLog.warn("Failed to load faction %s from file", new Object[] { factionName });
	        e.printStackTrace();
	      } 
	    } 

	    facSettings.setFactions(factions);
	    List<FactionRelationsTable> relationsTables = new ArrayList<>();
	    for (Map.Entry<ResourceLocation, JsonObject> entry : relationsJsons.entrySet()) {
	      ResourceLocation res = entry.getKey();
	      ResourceLocation relationsName = trimSubFolderResource(res, "relations/");
	      JsonObject relationsJson = entry.getValue();
	      try {
	        FactionRelationsTable relations = FactionRelationsTable.read(facSettings, relationsName, relationsJson);
	        if (relations != null)
	          relationsTables.add(relations); 
	      } catch (Exception e) {
	        LOTRLog.warn("Failed to load faction relations table %s from file", new Object[] { relationsName });
	        e.printStackTrace();
	      } 
	    } 
	    FactionRelationsTable combinedRelations = FactionRelationsTable.combine(relationsTables);
	    facSettings.setRelations(combinedRelations);
	    return facSettings;
	  }

	private void logFactionsLoad(String prefix, FactionSettings facSettings) {
		LOTRLog.info("%s - %d factions in %d regions, with %d relations (combined from %d relations lists)", prefix, facSettings.getFactions().size(), facSettings.getRegions().size(), facSettings.getRelations().size(), facSettings.getRelations().getNumCombinedFrom());
	}

	@Override
	public void performPostServerLoadValidation(World mainWorld) {
		currentLoadedFactions.postLoadValidateBiomes(mainWorld);
	}

	public void sendFactionsToPlayer(ServerPlayerEntity player) {
		SPacketFactionSettings packet = new SPacketFactionSettings(currentLoadedFactions);
		LOTRPacketHandler.sendTo(packet, player);
	}

	public static FactionSettingsManager clientInstance() {
		return CLIENT_INSTANCE;
	}

	public static FactionSettingsManager serverInstance() {
		return SERVER_INSTANCE;
	}

	public static FactionSettingsManager sidedInstance(IWorldReader world) {
		return !world.isClientSide() ? SERVER_INSTANCE : CLIENT_INSTANCE;
	}

	public static FactionSettingsManager sidedInstance(LogicalSide side) {
		return side == LogicalSide.SERVER ? SERVER_INSTANCE : CLIENT_INSTANCE;
	}
}
