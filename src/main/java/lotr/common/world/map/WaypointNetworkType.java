package lotr.common.world.map;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import lotr.common.data.LOTRPlayerData;
import net.minecraft.network.PacketBuffer;

public class WaypointNetworkType {
	private static final Map TYPES = new HashMap();
	public static final WaypointNetworkType MAP = registerType((h1, h2) -> MapWaypoint.writeIdentification((PacketBuffer) h1, (MapWaypoint) h2), (h1, h2) -> MapWaypoint.readFromIdentification((PacketBuffer) h1, (LOTRPlayerData) h2));
	public static final WaypointNetworkType CUSTOM = registerType((h1, h2) -> CustomWaypoint.writeIdentification((PacketBuffer) h1, (CustomWaypoint) h2), (h1, h2) -> CustomWaypoint.readFromIdentification((PacketBuffer) h1, (LOTRPlayerData) h2));
	public static final WaypointNetworkType ADOPTED_CUSTOM = registerType((h1, h2) -> AdoptedCustomWaypoint.writeIdentification((PacketBuffer) h1, (AdoptedCustomWaypoint) h2), (h1, h2) -> AdoptedCustomWaypoint.readFromIdentification((PacketBuffer) h1, (LOTRPlayerData) h2));
	private final int typeId;
	private final BiConsumer identificationWriter;
	private final BiFunction identificationReader;

	private WaypointNetworkType(int id, BiConsumer writer, BiFunction reader) {
		typeId = id;
		identificationWriter = writer;
		identificationReader = reader;
	}

	public static Waypoint readFromIdentification(PacketBuffer buf, LOTRPlayerData pd) {
		int typeId = buf.readByte() & 255;
		WaypointNetworkType type = (WaypointNetworkType) TYPES.get(typeId);
		if (type == null) {
			throw new IllegalStateException("Networking error - packet received unknown waypoint type ID " + typeId);
		}
		return (Waypoint) type.identificationReader.apply(buf, pd);
	}

	private static final WaypointNetworkType registerType(BiConsumer writer, BiFunction reader) {
		int nextTypeId = TYPES.size();
		if (nextTypeId > 255) {
			throw new IllegalStateException("No more waypoint type IDs available!");
		}
		WaypointNetworkType type = new WaypointNetworkType(nextTypeId, writer, reader);
		TYPES.put(nextTypeId, type);
		return type;
	}

	public static void writeIdentification(PacketBuffer buf, Waypoint waypoint) {
		WaypointNetworkType type = waypoint.getNetworkType();
		buf.writeByte(waypoint.getNetworkType().typeId);
		type.identificationWriter.accept(buf, waypoint);
	}
}
