package lotr.common.world.map;

import java.util.*;
import java.util.function.Supplier;

import com.google.gson.*;

import lotr.common.LOTRLog;
import lotr.common.data.DataUtil;
import lotr.common.init.LOTRBiomes;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class WaypointRegion {
	private final ResourceLocation resourceName;
	private final int assignedId;
	private final List biomeNames;

	private WaypointRegion(ResourceLocation resourceName, int assignedId, List biomeNames) {
		this.resourceName = resourceName;
		this.assignedId = assignedId;
		this.biomeNames = biomeNames;
	}

	public int getAssignedId() {
		return assignedId;
	}

	public List getBiomeNames() {
		return biomeNames;
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	protected void postLoadValidateBiomes(World world) {
		biomeNames.forEach(biomeName -> {
			Biome foundBiome = LOTRBiomes.getBiomeByRegistryName((ResourceLocation) biomeName, world);
			if (foundBiome == null) {
				LOTRLog.warn("WaypointRegion %s specifies a biome '%s' which does not exist in the biome registry!", resourceName, biomeName);
			}

		});
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(resourceName);
		buf.writeVarInt(assignedId);
		DataUtil.writeCollectionToBuffer(buf, biomeNames, hummel -> buf.writeResourceLocation((ResourceLocation) hummel));
	}

	public static WaypointRegion read(PacketBuffer buf) {
		ResourceLocation resourceName = buf.readResourceLocation();
		int assignedID = buf.readVarInt();
		Supplier var10001 = ArrayList::new;
		buf.getClass();
		List biomeNames = (List) DataUtil.readNewCollectionFromBuffer(buf, var10001, buf::readResourceLocation);
		return new WaypointRegion(resourceName, assignedID, biomeNames);
	}

	public static WaypointRegion readCombined(MapSettings map, ResourceLocation resourceName, List jsonVersions, int assignedId) {
		List biomeNames = new ArrayList();
		Iterator var5 = jsonVersions.iterator();

		while (var5.hasNext()) {
			JsonObject json = (JsonObject) var5.next();
			boolean replacePrevious = json.get("replace").getAsBoolean();
			if (replacePrevious) {
				biomeNames.clear();
			}

			JsonArray biomeArray = json.get("biomes").getAsJsonArray();
			for (JsonElement elem : biomeArray) {
				ResourceLocation biomeName = new ResourceLocation(elem.getAsString());
				biomeNames.add(biomeName);
			}
		}

		if (biomeNames.isEmpty()) {
			LOTRLog.warn("Waypoint region %s does not declare any biomes - this will make its waypoints impossible to unlock ingame!", resourceName);
		}

		return new WaypointRegion(resourceName, assignedId, biomeNames);
	}
}
