package lotr.common.world.map;

public class GeneratedRoadPoint implements RoadPoint {
	private final double mapX;
	private final double mapZ;
	private final double worldX;
	private final double worldZ;

	public GeneratedRoadPoint(MapSettings map, Road road, double mapX, double mapZ) {
		this.mapX = mapX;
		this.mapZ = mapZ;
		worldX = map.mapToWorldX_frac(mapX);
		worldZ = map.mapToWorldZ_frac(mapZ);
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
}
