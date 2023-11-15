package lotr.common.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.packs.ModFileResourcePack;
import net.minecraftforge.fml.packs.ResourcePackLoader;

public abstract class InstancedJsonReloadListener extends JsonReloadListener {
	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	protected static final String JSON_EXTENSION = ".json";
	private final String rootFolder;
	protected final String loaderNameForLogging;
	protected final LogicalSide side;

	public InstancedJsonReloadListener(String folder, String name, LogicalSide side) {
		super(GSON, folder);
		rootFolder = folder;
		loaderNameForLogging = name;
		this.side = side;
	}

	protected Map<ResourceLocation, List<JsonObject>> asMapOfSingletonLists(Map<ResourceLocation, JsonObject> map) {
		return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ImmutableList.of(e.getValue())));
	}

	private void extractAndApplyDataDirectorySettings(Map<ResourceLocation, JsonObject> jsons, String subFolder) {
		Entry<ResourceLocation, JsonObject> settingsEntry = extractDataDirectorySettingsJson(jsons);
		if (settingsEntry != null) {
			ResourceLocation settingsRes = settingsEntry.getKey();
			JsonObject settingsJson = settingsEntry.getValue();
			DataDirectorySettings settings = DataDirectorySettings.read(settingsRes, settingsJson);
			removeResourcesExcludedInSettings(jsons, subFolder, settings);
		}

	}

	private Entry<ResourceLocation, JsonObject> extractDataDirectorySettingsJson(Map<ResourceLocation, JsonObject> jsons) {
		Optional<ResourceLocation> settingsResOpt = jsons.keySet().stream().filter(res -> getPreparedPath(res).getPath().endsWith("/_settings.json")).findFirst();
		if (settingsResOpt.isPresent()) {
			ResourceLocation settingsRes = settingsResOpt.get();
			JsonObject settingsJson = jsons.remove(settingsRes);
			return Pair.of(settingsRes, settingsJson);
		}
		return null;
	}

	protected Map<ResourceLocation, JsonObject> filterDataJsonsByRootFolderOnly(Map<ResourceLocation, JsonElement> jsons) {
	    Map<ResourceLocation, JsonObject> subJsons = jsons.entrySet().stream().filter(e -> {
	          ResourceLocation res = e.getKey();
	          return !res.getPath().contains("/");
	        }).collect(jsonElemToObjMapCollector());
	    extractAndApplyDataDirectorySettings(subJsons, (String)null);
	    return subJsons;
	  }

	protected Map<ResourceLocation, JsonObject> filterDataJsonsBySubFolder(Map<ResourceLocation, JsonElement> jsons, String subFolder) {
	    Map<ResourceLocation, JsonObject> rootJsons = jsons.entrySet().stream().filter(e -> {
	          ResourceLocation res = e.getKey();
	          String resPath = res.getPath();
	          return (resPath.startsWith(subFolder) && !resPath.substring(subFolder.length()).contains("/"));
	        }).collect(jsonElemToObjMapCollector());
	    extractAndApplyDataDirectorySettings(rootJsons, subFolder);
	    return rootJsons;
	  }

	private String getFullFolderName(String subFolder) {
		return rootFolder + (subFolder == null ? "" : "/" + subFolder);
	}

	public final LogicalSide getSide() {
		return side;
	}

	protected JsonObject loadDataJsonIfExists(Map<ResourceLocation, JsonElement> jsons, ResourceLocation targetPath) {
	    Optional<Map.Entry<ResourceLocation, JsonElement>> optEntry = jsons.entrySet().stream().filter(entry -> {
	          ResourceLocation shortenedPath = entry.getKey();
	          return getPreparedPath(shortenedPath).equals(targetPath);
	        }).findFirst();
	    if (optEntry.isPresent())
	      return optEntry.get().getValue().getAsJsonObject(); 
	    LOTRLog.error("%s datapack load missing %s", new Object[] { this.loaderNameForLogging, targetPath });
	    return null;
	  }

	protected JsonObject loadDefaultJson(ResourceLocation res) {
		try {
			Reader reader = new BufferedReader(new InputStreamReader(getDefaultDatapackResourceStream(res), StandardCharsets.UTF_8));
			Throwable var3 = null;

			JsonObject var5;
			try {
				JsonObject jsonObj = JSONUtils.fromJson(GSON, reader, JsonObject.class);
				var5 = jsonObj;
			} catch (Throwable var15) {
				var3 = var15;
				throw var15;
			} finally {
				if (reader != null) {
					if (var3 != null) {
						try {
							reader.close();
						} catch (Throwable var14) {
							var3.addSuppressed(var14);
						}
					} else {
						reader.close();
					}
				}

			}

			return var5;
		} catch (Exception var17) {
			LOTRLog.warn("Failed to parse %s json resource: %s", loaderNameForLogging, res);
			var17.printStackTrace();
			return null;
		}
	}

	protected Map<ResourceLocation, JsonObject> loadDefaultJsonsInSubFolder(String subFolder, int maxDepth) {
		String fullFolder = String.format("%s/%s", rootFolder, subFolder);
		Collection<ResourceLocation> resources = getDefaultDatapackResourcesInFolder(fullFolder, maxDepth, s -> s.endsWith(".json"));
		Map<ResourceLocation, JsonObject> jsons = resources.stream().collect(Collectors.toMap(res -> {
			String resPath = res.getPath();
			return new ResourceLocation(res.getNamespace(), resPath.substring((rootFolder + "/").length(), resPath.indexOf(".json")));
		}, this::loadDefaultJson));
		extractDataDirectorySettingsJson(jsons);
		return jsons;
	}

	protected Map<ResourceLocation, List<JsonObject>> loadJsonResourceVersionsFromAllDatapacks(Set<ResourceLocation> jsonPaths, IResourceManager resMgr) {
		return jsonPaths.stream().collect(Collectors.toMap(res -> res, res -> {
			ResourceLocation fullRes = getPreparedPath(res);

			try {
				return resMgr.getResources(fullRes).stream().map(IResource::getInputStream).map(is -> {
					Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
					JsonElement json = JSONUtils.fromJson(GSON, reader, JsonElement.class);
					return json.getAsJsonObject();
				}).collect(Collectors.toList());
			} catch (Exception var5) {
				LOTRLog.error("Couldn't parse datapack variant file %s from %s", res, fullRes);
				var5.printStackTrace();
				return ImmutableList.of();
			}
		}));
	}

	private void removeResourcesExcludedInSettings(Map<ResourceLocation, JsonObject> jsons, String subFolder, DataDirectorySettings settings) {
		int sizeBefore = jsons.size();
		Set<ResourceLocation> toRemove = new HashSet<>();
	    for (ResourceLocation res : jsons.keySet()) {
	      if (settings.shouldExclude(trimSubFolderResource(res, subFolder)))
	        toRemove.add(res); 
	    } 
	    toRemove.forEach(jsons::remove);
		int numRemoved = sizeBefore - jsons.size();
		LOTRLog.info("Excluded %d resources in folder '%s' based on the %s", numRemoved, getFullFolderName(subFolder), "_settings.json");
	}

	private static Collection<ResourceLocation> getDefaultDatapackResourcesInFolder(String path, int maxDepth, Predicate<String> filter) {
		String namespace = "lotr";
		ModFileResourcePack lotrAsPack = ResourcePackLoader.getResourcePackFor(namespace).get();
		return lotrAsPack.getResources(ResourcePackType.SERVER_DATA, namespace, path, maxDepth, filter);
	}

	private static InputStream getDefaultDatapackResourceStream(ResourceLocation res) {
		return LOTRMod.getDefaultModResourceStream(ResourcePackType.SERVER_DATA, res);
	}

	protected static Collector<Map.Entry<ResourceLocation, JsonElement>, ?, Map<ResourceLocation, JsonObject>> jsonElemToObjMapCollector() {
		return Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAsJsonObject());
	}

	protected static <K, V> Collector<Map.Entry<K, V>, ?, Map<K, V>> toMapCollector() {
		return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
	}

	protected static ResourceLocation trimSubFolderResource(ResourceLocation res, String subFolder) {
		return subFolder == null ? res : new ResourceLocation(res.getNamespace(), res.getPath().substring(subFolder.length()));
	}
}
