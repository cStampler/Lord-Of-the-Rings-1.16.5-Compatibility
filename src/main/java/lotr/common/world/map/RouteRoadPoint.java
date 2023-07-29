package lotr.common.world.map;

import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RouteRoadPoint implements RoadPoint {
	private final double mapX;
	private final double mapZ;
	private final double worldX;
	private final double worldZ;
	private final MapWaypoint correspondingWaypoint;

	public RouteRoadPoint(MapSettings map, double mapX, double mapZ, MapWaypoint wp) {
		this.mapX = mapX;
		this.mapZ = mapZ;
		worldX = map.mapToWorldX_frac(mapX);
		worldZ = map.mapToWorldZ_frac(mapZ);
		correspondingWaypoint = wp;
	}

	public MapWaypoint getCorrespondingWaypoint() {
		return correspondingWaypoint;
	}

	@Override
	public double getMapX() {
		return mapX;
	}

	@Override
	public double getMapZ() {
		return mapZ;
	}

	@Override
	public double getWorldX() {
		return worldX;
	}

	@Override
	public double getWorldZ() {
		return worldZ;
	}

	public void write(PacketBuffer buf) {
		buf.writeDouble(mapX);
		buf.writeDouble(mapZ);
		int wpId = correspondingWaypoint != null ? correspondingWaypoint.getAssignedId() : -1;
		buf.writeVarInt(wpId);
	}

	public static RouteRoadPoint read(MapSettings map, ResourceLocation roadName, JsonObject json) {
		String typeCode = json.get("type").getAsString();
		RouteRoadPoint.Type type = RouteRoadPoint.Type.forCode(typeCode);
		String wpName;
		if (type == RouteRoadPoint.Type.WAYPOINT) {
			wpName = json.get("waypoint").getAsString();
			MapWaypoint wp = map.getWaypointByName(new ResourceLocation(wpName));
			if (wp != null) {
				return new RouteRoadPoint(map, wp.getMapX(), wp.getMapZ(), wp);
			}
			LOTRLog.warn("Road %s references waypoint %s - but no such waypoint exists", roadName, wpName);
			return null;
		}
		if (type == RouteRoadPoint.Type.WAYPOINT_OFFSET) {
			wpName = json.get("waypoint").getAsString();
			double xOffset = json.get("x_offset").getAsDouble();
			double zOffset = json.get("z_offset").getAsDouble();
			MapWaypoint wp = map.getWaypointByName(new ResourceLocation(wpName));
			if (wp != null) {
				double mapX = wp.getMapX() + xOffset;
				double mapZ = wp.getMapZ() + zOffset;
				return new RouteRoadPoint(map, mapX, mapZ, (MapWaypoint) null);
			}
			LOTRLog.warn("Road %s references waypoint %s - but no such waypoint exists", roadName, wpName);
			return null;
		}
		if (type == RouteRoadPoint.Type.COORDINATES) {
			double mapX = json.get("x").getAsDouble() + 0.5D;
			double mapZ = json.get("z").getAsDouble() + 0.5D;
			return new RouteRoadPoint(map, mapX, mapZ, (MapWaypoint) null);
		}
		LOTRLog.warn("Road %s declares a route point of unknown type %s - no such type exists", roadName, typeCode);
		return null;
	}

	public static RouteRoadPoint read(MapSettings map, ResourceLocation roadName, PacketBuffer buf) {
		double mapX = buf.readDouble();
		double mapZ = buf.readDouble();
		MapWaypoint correspondingWaypoint = null;
		int wpId = buf.readVarInt();
		if (wpId >= 0) {
			correspondingWaypoint = map.getWaypointByID(wpId);
			if (correspondingWaypoint == null) {
				LOTRLog.warn("Received a road route point (on road %s) from server corresponding to a waypoint ID %d that doesn't exist", roadName, wpId);
			}
		}

		return new RouteRoadPoint(map, mapX, mapZ, correspondingWaypoint);
	}

	public enum Type {
		WAYPOINT("waypoint"), WAYPOINT_OFFSET("waypoint_offset"), COORDINATES("coords");

		private final String code;
		private final int networkID;

		Type(String s) {
			code = s;
			networkID = ordinal();
		}

		public String getCode() {
			return code;
		}

		public static RouteRoadPoint.Type forCode(String code) {
			RouteRoadPoint.Type[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				RouteRoadPoint.Type type = var1[var3];
				if (type.getCode().equals(code)) {
					return type;
				}
			}

			return null;
		}

		public static RouteRoadPoint.Type forNetworkID(int id) {
			RouteRoadPoint.Type[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				RouteRoadPoint.Type type = var1[var3];
				if (type.networkID == id) {
					return type;
				}
			}

			return null;
		}
	}
}
