package lotr.common.resources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.*;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;

import lotr.common.*;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.packs.*;

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

	protected Map asMapOfSingletonLists(Map map) {
		return (Map) map.entrySet().stream().collect(Collectors.toMap(hummel -> ((Entry) hummel).getKey(), e -> ImmutableList.of(((Entry) e).getValue())));
	}

	private void extractAndApplyDataDirectorySettings(Map jsons, String subFolder) {
		Entry settingsEntry = extractDataDirectorySettingsJson(jsons);
		if (settingsEntry != null) {
			ResourceLocation settingsRes = (ResourceLocation) settingsEntry.getKey();
			JsonObject settingsJson = (JsonObject) settingsEntry.getValue();
			DataDirectorySettings settings = DataDirectorySettings.read(settingsRes, settingsJson);
			removeResourcesExcludedInSettings(jsons, subFolder, settings);
		}

	}

	private Entry extractDataDirectorySettingsJson(Map jsons) {
		Optional settingsResOpt = jsons.keySet().stream().filter(res -> getPreparedPath((ResourceLocation) res).getPath().endsWith("/_settings.json")).findFirst();
		if (settingsResOpt.isPresent()) {
			ResourceLocation settingsRes = (ResourceLocation) settingsResOpt.get();
			JsonObject settingsJson = (JsonObject) jsons.remove(settingsRes);
			return Pair.of(settingsRes, settingsJson);
		}
		return null;
	}

	protected Map filterDataJsonsByRootFolderOnly(Map jsons) {
		Map subJsons = (Map) jsons.entrySet().stream().filter(e -> {
			ResourceLocation res = (ResourceLocation) ((Entry) e).getKey();
			return !res.getPath().contains("/");
		}).collect(jsonElemToObjMapCollector());
		extractAndApplyDataDirectorySettings(subJsons, (String) null);
		return subJsons;
	}

	protected Map filterDataJsonsBySubFolder(Map jsons, String subFolder) {
		Map rootJsons = (Map) jsons.entrySet().stream().filter(e -> {
			ResourceLocation res = (ResourceLocation) ((Entry) e).getKey();
			String resPath = res.getPath();
			return resPath.startsWith(subFolder) && !resPath.substring(subFolder.length()).contains("/");
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

	protected JsonObject loadDataJsonIfExists(Map jsons, ResourceLocation targetPath) {
		Optional optEntry = jsons.entrySet().stream().filter(entry -> {
			ResourceLocation shortenedPath = (ResourceLocation) ((Entry) entry).getKey();
			return getPreparedPath(shortenedPath).equals(targetPath);
		}).findFirst();
		if (optEntry.isPresent()) {
			return ((JsonElement) ((Entry) optEntry.get()).getValue()).getAsJsonObject();
		}
		LOTRLog.error("%s datapack load missing %s", loaderNameForLogging, targetPath);
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

	protected Map loadDefaultJsonsInSubFolder(String subFolder, int maxDepth) {
		String fullFolder = String.format("%s/%s", rootFolder, subFolder);
		Collection resources = getDefaultDatapackResourcesInFolder(fullFolder, maxDepth, s -> ((String) s).endsWith(".json"));
		Map jsons = (Map) resources.stream().collect(Collectors.toMap(res -> {
			String resPath = res.getPath();
			return new ResourceLocation(res.getNamespace(), resPath.substring((rootFolder + "/").length(), resPath.indexOf(".json")));
		}, this::loadDefaultJson));
		extractDataDirectorySettingsJson(jsons);
		return jsons;
	}

	protected Map loadJsonResourceVersionsFromAllDatapacks(Set jsonPaths, IResourceManager resMgr) {
		return (Map) jsonPaths.stream().collect(Collectors.toMap(res -> res, res -> {
			ResourceLocation fullRes = getPreparedPath((ResourceLocation) res);

			try {
				return (List) resMgr.getResources(fullRes).stream().map(IResource::getInputStream).map(is -> {
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

	private void removeResourcesExcludedInSettings(Map jsons, String subFolder, DataDirectorySettings settings) {
		int sizeBefore = jsons.size();
		Set toRemove = new HashSet();
		Iterator var6 = jsons.keySet().iterator();

		while (var6.hasNext()) {
			ResourceLocation res = (ResourceLocation) var6.next();
			if (settings.shouldExclude(trimSubFolderResource(res, subFolder))) {
				toRemove.add(res);
			}
		}

		toRemove.forEach(jsons::remove);
		int numRemoved = sizeBefore - jsons.size();
		LOTRLog.info("Excluded %d resources in folder '%s' based on the %s", numRemoved, getFullFolderName(subFolder), "_settings.json");
	}

	private static Collection getDefaultDatapackResourcesInFolder(String path, int maxDepth, Predicate filter) {
		String namespace = "lotr";
		ModFileResourcePack lotrAsPack = ResourcePackLoader.getResourcePackFor(namespace).get();
		return lotrAsPack.getResources(ResourcePackType.SERVER_DATA, namespace, path, maxDepth, filter);
	}

	private static InputStream getDefaultDatapackResourceStream(ResourceLocation res) {
		return LOTRMod.getDefaultModResourceStream(ResourcePackType.SERVER_DATA, res);
	}

	protected static Collector jsonElemToObjMapCollector() {
		return Collectors.toMap(hummel -> ((Entry) hummel).getKey(), e -> ((JsonElement) ((Entry) e).getValue()).getAsJsonObject());
	}

	protected static Collector toMapCollector() {
		return Collectors.toMap(hummel -> ((Entry) hummel).getKey(), hummel -> ((Entry) hummel).getValue());
	}

	protected static ResourceLocation trimSubFolderResource(ResourceLocation res, String subFolder) {
		return subFolder == null ? res : new ResourceLocation(res.getNamespace(), res.getPath().substring(subFolder.length()));
	}
}
