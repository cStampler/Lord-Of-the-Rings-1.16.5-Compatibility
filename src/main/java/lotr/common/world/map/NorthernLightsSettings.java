package lotr.common.world.map;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class NorthernLightsSettings {
	public static final ResourceLocation NORTHERN_LIGHTS_SETTINGS_PATH = new ResourceLocation("lotr", "map/northern_lights.json");
	private final float fullNorth;
	private final float startSouth;
	private final float furthestPossibleSouth;
	private final int fullNorth_world;
	private final int startSouth_world;
	private final int furthestPossibleSouth_world;

	private NorthernLightsSettings(MapSettings map, float fullNorth, float startSouth, float furthestPossibleSouth) {
		this.fullNorth = fullNorth;
		this.startSouth = startSouth;
		this.furthestPossibleSouth = furthestPossibleSouth;
		fullNorth_world = map.mapToWorldZ(fullNorth);
		startSouth_world = map.mapToWorldZ(startSouth);
		furthestPossibleSouth_world = map.mapToWorldZ(furthestPossibleSouth);
	}

	public int getFullNorth_world() {
		return fullNorth_world;
	}

	public int getFurthestPossibleSouth_world() {
		return furthestPossibleSouth_world;
	}

	public int getStartSouth_world() {
		return startSouth_world;
	}

	public void write(PacketBuffer buf) {
		buf.writeFloat(fullNorth);
		buf.writeFloat(startSouth);
		buf.writeFloat(furthestPossibleSouth);
	}

	public static NorthernLightsSettings read(MapSettings map, JsonObject json) {
		float fullNorth = json.get("full_north").getAsFloat();
		float startSouth = json.get("start_south").getAsFloat();
		float furthestPossibleSouth = json.get("furthest_possible_south").getAsFloat();
		return new NorthernLightsSettings(map, fullNorth, startSouth, furthestPossibleSouth);
	}

	public static NorthernLightsSettings read(MapSettings map, PacketBuffer buf) {
		float fullNorth = buf.readFloat();
		float startSouth = buf.readFloat();
		float furthestPossibleSouth = buf.readFloat();
		return new NorthernLightsSettings(map, fullNorth, startSouth, furthestPossibleSouth);
	}
}
