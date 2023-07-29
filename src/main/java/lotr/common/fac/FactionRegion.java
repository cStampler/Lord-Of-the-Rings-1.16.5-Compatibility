package lotr.common.fac;

import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;

public class FactionRegion {
	private final ResourceLocation resourceName;
	private final int assignedId;
	private final String name;
	private final boolean translateName;
	private final RegistryKey dimension;
	private final int ordering;

	public FactionRegion(ResourceLocation res, int id, String name, boolean translateName, ResourceLocation dimName, int order) {
		resourceName = res;
		assignedId = id;
		this.name = name;
		this.translateName = translateName;
		dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, dimName);
		ordering = order;
	}

	public int getAssignedId() {
		return assignedId;
	}

	public RegistryKey getDimension() {
		return dimension;
	}

	public ResourceLocation getDimensionName() {
		return dimension.location();
	}

	public ITextComponent getDisplayName() {
		return translateName ? new TranslationTextComponent(name) : new StringTextComponent(name);
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	public int getOrdering() {
		return ordering;
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(resourceName);
		buf.writeVarInt(assignedId);
		buf.writeUtf(name);
		buf.writeBoolean(translateName);
		buf.writeResourceLocation(dimension.location());
		buf.writeInt(ordering);
	}

	public static FactionRegion read(PacketBuffer buf) {
		ResourceLocation resourceName = buf.readResourceLocation();
		int assignedId = buf.readVarInt();
		String name = buf.readUtf();
		boolean translateName = buf.readBoolean();
		ResourceLocation dimensionName = buf.readResourceLocation();
		int ordering = buf.readInt();
		return new FactionRegion(resourceName, assignedId, name, translateName, dimensionName, ordering);
	}

	public static FactionRegion read(ResourceLocation resourceName, JsonObject json, int assignedId) {
		if (json.size() == 0) {
			LOTRLog.info("Faction region %s has an empty file - not loading it in this world", resourceName);
			return null;
		}
		JsonObject nameObj = json.get("name").getAsJsonObject();
		String name = nameObj.get("text").getAsString();
		boolean translateName = nameObj.get("translate").getAsBoolean();
		String dimensionName = json.get("dimension").getAsString();
		ResourceLocation dimensionNameRes = new ResourceLocation(dimensionName);
		int ordering = json.get("ordering").getAsInt();
		return new FactionRegion(resourceName, assignedId, name, translateName, dimensionNameRes, ordering);
	}
}
