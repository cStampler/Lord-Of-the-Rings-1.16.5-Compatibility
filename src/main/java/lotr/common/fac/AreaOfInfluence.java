package lotr.common.fac;

import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.world.map.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class AreaOfInfluence {
	private final MapSettings mapSettings;
	private final double mapX;
	private final double mapZ;
	private final int mapRadius;
	private final double worldX;
	private final double worldZ;
	private final int worldRadius;
	private final long worldRadiusSq;

	public AreaOfInfluence(MapSettings map, double mapX, double mapZ, int mapR) {
		mapSettings = map;
		this.mapX = mapX;
		this.mapZ = mapZ;
		mapRadius = mapR;
		worldX = map.mapToWorldX_frac(mapX);
		worldZ = map.mapToWorldZ_frac(mapZ);
		worldRadius = map.mapToWorldDistance(mapR);
		worldRadiusSq = (long) worldRadius * (long) worldRadius;
	}

	public int getMapRadius() {
		return mapRadius;
	}

	public double getMapX() {
		return mapX;
	}

	public double getMapZ() {
		return mapZ;
	}

	public int getWorldRadius() {
		return worldRadius;
	}

	public double getWorldX() {
		return worldX;
	}

	public double getWorldZ() {
		return worldZ;
	}

	public boolean intersectsWith(AreaOfInfluence other, int extraMapRadius) {
		double dx = other.worldX - worldX;
		double dz = other.worldZ - worldZ;
		double distSq = dx * dx + dz * dz;
		double r12 = worldRadius + other.worldRadius + mapSettings.mapToWorldDistance(extraMapRadius * 2);
		double r12Sq = r12 * r12;
		return distSq <= r12Sq;
	}

	public boolean isInArea(double x, double y, double z, int extraMapRange) {
		double dx = x - worldX;
		double dz = z - worldZ;
		double distSq = dx * dx + dz * dz;
		if (extraMapRange == 0) {
			return distSq <= worldRadiusSq;
		}
		int checkRadius = mapSettings.mapToWorldDistance(mapRadius + extraMapRange);
		long checkRadiusSq = (long) checkRadius * (long) checkRadius;
		return distSq <= checkRadiusSq;
	}

	public void write(PacketBuffer buf) {
		buf.writeDouble(mapX);
		buf.writeDouble(mapZ);
		buf.writeInt(mapRadius);
	}

	public static AreaOfInfluence read(MapSettings map, ResourceLocation factionName, JsonObject json) {
		String typeCode = json.get("type").getAsString();
		AreaOfInfluence.Type type = AreaOfInfluence.Type.forCode(typeCode);
		int mapRadius = json.get("radius").getAsInt();
		if (type == AreaOfInfluence.Type.WAYPOINT) {
			String wpName = json.get("waypoint").getAsString();
			MapWaypoint wp = map.getWaypointByName(new ResourceLocation(wpName));
			if (wp != null) {
				return new AreaOfInfluence(map, wp.getMapX(), wp.getMapZ(), mapRadius);
			}
			LOTRLog.warn("Faction %s area of influence references waypoint %s - but no such waypoint exists", factionName, wpName);
			return null;
		}
		if (type == AreaOfInfluence.Type.COORDINATES) {
			double mapX = json.get("x").getAsDouble() + 0.5D;
			double mapZ = json.get("z").getAsDouble() + 0.5D;
			return new AreaOfInfluence(map, mapX, mapZ, mapRadius);
		}
		LOTRLog.warn("Faction %s declares an area of influence of unknown type %s - no such type exists", factionName, typeCode);
		return null;
	}

	public static AreaOfInfluence read(MapSettings map, ResourceLocation factionName, PacketBuffer buf) {
		double mapX = buf.readDouble();
		double mapZ = buf.readDouble();
		int mapRadius = buf.readInt();
		return new AreaOfInfluence(map, mapX, mapZ, mapRadius);
	}

	public enum Type {
		WAYPOINT("waypoint"), COORDINATES("coords");

		private final String code;

		Type(String s) {
			code = s;
		}

		public String getCode() {
			return code;
		}

		public static AreaOfInfluence.Type forCode(String code) {
			AreaOfInfluence.Type[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				AreaOfInfluence.Type type = var1[var3];
				if (type.getCode().equals(code)) {
					return type;
				}
			}

			return null;
		}
	}
}
