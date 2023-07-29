package lotr.common.world.map;

public class RoadSection {
	private final RouteRoadPoint[] startAndEndPoints;
	private final GeneratedRoadPoint[] roadPoints;

	public RoadSection(Road road, RouteRoadPoint startPoint, RouteRoadPoint endPoint, GeneratedRoadPoint[] roadPoints) {
		startAndEndPoints = new RouteRoadPoint[] { startPoint, endPoint };
		this.roadPoints = roadPoints;
	}

	public GeneratedRoadPoint[] getRoutePoints() {
		return roadPoints;
	}

	public RouteRoadPoint[] getStartAndEndPoints() {
		return startAndEndPoints;
	}
}
