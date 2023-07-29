package lotr.common.resources;

import java.util.*;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;

import net.minecraft.util.ResourceLocation;

public class DataDirectorySettings {
	public static final String SETTINGS_FILENAME = "_settings.json";
	private final Set removeNamespaces;
	private final Set removeResources;

	private DataDirectorySettings(Set removeNamespaces, Set removeResources) {
		this.removeNamespaces = removeNamespaces;
		this.removeResources = removeResources;
		validate();
	}

	public boolean shouldExclude(ResourceLocation resource) {
		return removeNamespaces.contains(resource.getNamespace()) || removeResources.contains(resource);
	}

	@Override
	public String toString() {
		return String.format("%s [removeNamespaces = %d, removeResources = %d]", "_settings.json", removeNamespaces.size(), removeResources.size());
	}

	private void validate() {
		removeNamespaces.forEach(namespace -> {
			if (ResourceLocation.tryParse(namespace + ":test_resource_path") == null) {
				throw new IllegalArgumentException("Invalid namespace declaration: " + namespace);
			}
		});
	}

	public static DataDirectorySettings empty() {
		return new DataDirectorySettings(ImmutableSet.of(), ImmutableSet.of());
	}

	public static DataDirectorySettings read(ResourceLocation resourceName, JsonObject json) {
		Set removeNamespaces = new HashSet();
		JsonArray removeNamespacesArray = json.get("remove_namespaces").getAsJsonArray();
		for (JsonElement namespace : removeNamespacesArray) {
			removeNamespaces.add(namespace.getAsString());
		}

		Set removeResources = new HashSet();
		JsonArray removeResourcesArray = json.get("remove_singles").getAsJsonArray();
		for (JsonElement resource : removeResourcesArray) {
			removeResources.add(new ResourceLocation(resource.getAsString()));
		}

		return new DataDirectorySettings(removeNamespaces, removeResources);
	}
}
