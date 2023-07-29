package lotr.client.render.model;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import lotr.common.LOTRLog;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.*;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

public class DynamicTextureResourcePack implements IResourcePack {
	private final ResourcePackType packType;
	private final String namespace;
	private final Map inputStreams = new HashMap();
	public final ResourceLocation packIsLoadedMarkerResource;
	private final Map textureSetsLoadedMarkers = new HashMap();

	public DynamicTextureResourcePack(ResourcePackType type, String s) {
		packType = type;
		namespace = s;
		packIsLoadedMarkerResource = new ResourceLocation(namespace, "dynamic_tex_virtual_resource_pack_is_loaded_marker");
	}

	public void addDynamicTexture(ResourceLocation baseSetPath, ResourceLocation dynamicFullPath, DynamicTexture tex) {
		NativeImage image = tex.getPixels();
		Supplier sup = () -> {
			try {
				return new ByteArrayInputStream(image.asByteArray());
			} catch (Exception var3) {
				LOTRLog.error("Failed to setup dynamic texture resource: %s", dynamicFullPath);
				var3.printStackTrace();
				return new ByteArrayInputStream(new byte[0]);
			}
		};
		inputStreams.put(dynamicFullPath, sup);
		if (!textureSetsLoadedMarkers.containsKey(baseSetPath)) {
			textureSetsLoadedMarkers.put(baseSetPath, createDynamicTextureSetIsLoadedMarker(baseSetPath));
		}

	}

	@Override
	public void close() {
		inputStreams.clear();
	}

	@Override
	public Object getMetadataSection(IMetadataSectionSerializer deserializer) throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return String.format("%s:%s virtual pack for dynamic textures", "lotr", namespace);
	}

	@Override
	public Set getNamespaces(ResourcePackType type) {
		return type == packType ? Sets.newHashSet(namespace) : Collections.emptySet();
	}

	@Override
	public InputStream getResource(ResourcePackType type, ResourceLocation location) throws IOException {
		if (type == packType) {
			return (InputStream) ((Supplier) inputStreams.get(location)).get();
		}
		throw new FileNotFoundException(String.format("'%s' in ResourcePack '%s'", getFullPath(type, location), getName()));
	}

	@Override
	public Collection getResources(ResourcePackType type, String namespace, String path, int maxDepth, Predicate filter) {
		return type == packType ? (Collection) inputStreams.keySet().stream().filter(res -> {
			if (!((ResourceLocation) res).getNamespace().equals(namespace)) {
				return false;
			}
			String resPath = ((ResourceLocation) res).getPath();
			String[] pathElements = resPath.split("/");
			return resPath.startsWith(path) && pathElements.length >= maxDepth + 1 && filter.test(pathElements[pathElements.length - 1]);
		}).collect(Collectors.toList()) : Collections.emptySet();
	}

	@Override
	public InputStream getRootResource(String fileName) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasResource(ResourcePackType type, ResourceLocation location) {
		if (type != packType) {
			return false;
		}
		return inputStreams.containsKey(location) || packIsLoadedMarkerResource.equals(location) || textureSetsLoadedMarkers.containsValue(location);
	}

	public static ResourceLocation createDynamicTextureSetIsLoadedMarker(ResourceLocation baseSetPath) {
		return new ResourceLocation(baseSetPath.getNamespace(), baseSetPath.getPath() + "set_loaded_marker");
	}

	private static String getFullPath(ResourcePackType type, ResourceLocation location) {
		return String.format("%s/%s/%s", type.getDirectory(), location.getNamespace(), location.getPath());
	}
}
