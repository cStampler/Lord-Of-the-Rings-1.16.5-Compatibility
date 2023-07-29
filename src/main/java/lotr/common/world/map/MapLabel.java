package lotr.common.world.map;

import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRBiomes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class MapLabel {
	private final ResourceLocation resourceName;
	private final RegistryKey biomeName;
	private final String textName;
	private final boolean translateName;
	private final int mapX;
	private final int mapZ;
	private final float scale;
	private final float angle;
	private final float minZoom;
	private final float maxZoom;

	private MapLabel(ResourceLocation res, RegistryKey biome, String text, boolean translate, int x, int z, float scale, float angle, float minZoom, float maxZoom) {
		resourceName = res;
		biomeName = biome;
		textName = text;
		translateName = translate;
		mapX = x;
		mapZ = z;
		this.scale = scale;
		this.angle = angle;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	public float getAngle() {
		return angle;
	}

	public ITextComponent getDisplayName(IWorld world) {
		if (biomeName != null) {
			Biome biome = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).get(biomeName);
			return biome != null ? LOTRBiomes.getBiomeDisplayName(biome, world) : new StringTextComponent(String.format("Unknown biome '%s'", biomeName.location()));
		}
		return translateName ? new TranslationTextComponent(textName) : new StringTextComponent(textName);
	}

	public int getMapX() {
		return mapX;
	}

	public int getMapZ() {
		return mapZ;
	}

	public float getMaxZoom() {
		return maxZoom;
	}

	public float getMinZoom() {
		return minZoom;
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	public float getScale() {
		return scale;
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(resourceName);
		buf.writeInt(mapX);
		buf.writeInt(mapZ);
		buf.writeFloat(scale);
		buf.writeFloat(angle);
		buf.writeFloat(minZoom);
		buf.writeFloat(maxZoom);
		boolean isBiomeName = biomeName != null;
		buf.writeBoolean(isBiomeName);
		if (isBiomeName) {
			buf.writeResourceLocation(biomeName.location());
		} else {
			buf.writeUtf(textName);
			buf.writeBoolean(translateName);
		}

	}

	public static MapLabel makeBiomeLabel(MapSettings map, ResourceLocation res, RegistryKey biome, int x, int z, float scale, float angle, float minZoom, float maxZoom) {
		return new MapLabel(res, biome, (String) null, false, x, z, scale, angle, minZoom, maxZoom);
	}

	public static MapLabel makeTextLabel(MapSettings map, ResourceLocation res, String text, boolean translate, int x, int z, float scale, float angle, float minZoom, float maxZoom) {
		return new MapLabel(res, (RegistryKey) null, text, translate, x, z, scale, angle, minZoom, maxZoom);
	}

	public static MapLabel read(MapSettings map, PacketBuffer buf) {
		ResourceLocation resourceName = buf.readResourceLocation();
		int mapX = buf.readInt();
		int mapZ = buf.readInt();
		float scale = buf.readFloat();
		float angle = buf.readFloat();
		float minZoom = buf.readFloat();
		float maxZoom = buf.readFloat();
		boolean isBiomeName = buf.readBoolean();
		if (isBiomeName) {
			ResourceLocation biomeName = buf.readResourceLocation();
			RegistryKey biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biomeName);
			return makeBiomeLabel(map, resourceName, biomeKey, mapX, mapZ, scale, angle, minZoom, maxZoom);
		}
		String textName = buf.readUtf();
		boolean translateName = buf.readBoolean();
		return makeTextLabel(map, resourceName, textName, translateName, mapX, mapZ, scale, angle, minZoom, maxZoom);
	}

	public static MapLabel read(MapSettings map, ResourceLocation resourceName, JsonObject json) {
		if (json.size() == 0) {
			LOTRLog.info("Map label %s has an empty file - not loading it in this world", resourceName);
			return null;
		}
		int mapX = json.get("x").getAsInt();
		int mapZ = json.get("z").getAsInt();
		float scale = json.get("scale").getAsFloat();
		float angle = json.get("angle").getAsFloat();
		float minZoom = json.get("min_zoom").getAsFloat();
		float maxZoom = json.get("max_zoom").getAsFloat();
		JsonObject nameObj = json.get("name").getAsJsonObject();
		String nameType = nameObj.get("type").getAsString();
		if ("biome".equals(nameType)) {
			ResourceLocation biomeName = new ResourceLocation(nameObj.get("biome").getAsString());
			RegistryKey biomeKey = RegistryKey.create(Registry.BIOME_REGISTRY, biomeName);
			return makeBiomeLabel(map, resourceName, biomeKey, mapX, mapZ, scale, angle, minZoom, maxZoom);
		}
		if ("text".equals(nameType)) {
			String text = nameObj.get("text").getAsString();
			boolean translateText = nameObj.get("translate").getAsBoolean();
			return makeTextLabel(map, resourceName, text, translateText, mapX, mapZ, scale, angle, minZoom, maxZoom);
		}
		LOTRLog.error("Error loading map label %s - name type %s is not recognised", resourceName, nameType);
		return null;
	}
}
