package lotr.client.gui.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lotr.common.LOTRLog;
import lotr.common.world.map.MapPlayerLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;

public class MapPlayerLocationHolder {
	private static final Map<UUID, MapPlayerLocation> locations = new HashMap<>();

	public static void clearPlayerLocations() {
		locations.clear();
	}

	public static Map<UUID, MapPlayerLocation> getPlayerLocations() {
		return locations;
	}

	public static void refreshPlayerLocations(List<MapPlayerLocation> newPlayerLocations) {
		clearPlayerLocations();
	    ClientPlayNetHandler nph = Minecraft.getInstance().getConnection();
	    for (MapPlayerLocation loc : newPlayerLocations) {
	    	UUID playerID = loc.profile.getId();
	    	if (playerID != null) {
	    		NetworkPlayerInfo player = nph.getPlayerInfo(playerID);
	    		locations.put(playerID, loc.withFullProfile(player.getProfile()));
	    		continue;
	    	} 
	    	LOTRLog.warn("Received map player location from server with null UUID");
	    }
	}
}
