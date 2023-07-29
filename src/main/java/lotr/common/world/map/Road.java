package lotr.common.world.map;

import java.util.*;

import com.google.gson.*;

import lotr.common.LOTRLog;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class Road {
	private final MapSettings mapSettings;
	private final ResourceLocation resourceName;
	private final String name;
	private final boolean translateName;
	private final List controlPoints;
	private final List sections = new ArrayList();

	public Road(MapSettings map, ResourceLocation res, String name, boolean translateName, List controlPoints) {
		mapSettings = map;
		resourceName = res;
		this.name = name;
		this.translateName = translateName;
		this.controlPoints = controlPoints;
	}

	public Road generateCurves() {
		if (!sections.isEmpty()) {
			throw new IllegalStateException("Road " + resourceName + " curves were already generated");
		}
		sections.addAll(RoadCurveGenerator.generateSplines(mapSettings, this, controlPoints, mapSettings.getRoadPointCache()));
		return this;
	}

	public ITextComponent getDisplayName() {
		return translateName ? new TranslationTextComponent(name) : new StringTextComponent(name);
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	public List getSections() {
		return sections;
	}

	public boolean hasSameDisplayNameAs(Road other) {
		return getDisplayName().getString().equals(other.getDisplayName().getString());
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(resourceName);
		buf.writeUtf(name);
		buf.writeBoolean(translateName);
		buf.writeVarInt(controlPoints.size());
		Iterator var2 = controlPoints.iterator();

		while (var2.hasNext()) {
			RouteRoadPoint point = (RouteRoadPoint) var2.next();
			point.write(buf);
		}

	}

	public static Road read(MapSettings map, PacketBuffer buf) {
		ResourceLocation resourceName = buf.readResourceLocation();
		String name = buf.readUtf();
		boolean translateName = buf.readBoolean();
		List controlPoints = new ArrayList();
		int routeSize = buf.readVarInt();

		for (int i = 0; i < routeSize; ++i) {
			RouteRoadPoint controlPoint = RouteRoadPoint.read(map, resourceName, buf);
			if (controlPoint != null) {
				controlPoints.add(controlPoint);
			}
		}

		return new Road(map, resourceName, name, translateName, controlPoints);
	}

	public static Road read(MapSettings map, ResourceLocation resourceName, JsonObject json) {
		if (json.size() == 0) {
			LOTRLog.info("Road %s has an empty file - not loading it in this world", resourceName);
			return null;
		}
		JsonObject nameObj = json.get("name").getAsJsonObject();
		String name = nameObj.get("text").getAsString();
		boolean translateName = nameObj.get("translate").getAsBoolean();
		List controlPoints = new ArrayList();
		JsonArray route = json.get("route").getAsJsonArray();
		for (JsonElement routeElement : route) {
			JsonObject pointObj = routeElement.getAsJsonObject();
			RouteRoadPoint controlPoint = RouteRoadPoint.read(map, resourceName, pointObj);
			if (controlPoint != null) {
				controlPoints.add(controlPoint);
			}
		}

		return new Road(map, resourceName, name, translateName, controlPoints);
	}
}
