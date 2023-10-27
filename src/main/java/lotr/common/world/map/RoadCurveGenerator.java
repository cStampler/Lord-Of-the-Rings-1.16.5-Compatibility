package lotr.common.world.map;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.ImmutableList;

import lotr.common.LOTRLog;

public class RoadCurveGenerator {
	private static GeneratedRoadPoint bezier(MapSettings map, Road road, RoadPoint a, RoadPoint b, RoadPoint c, RoadPoint d, double t) {
		GeneratedRoadPoint ab = lerp(map, road, a, b, t);
		GeneratedRoadPoint bc = lerp(map, road, b, c, t);
		GeneratedRoadPoint cd = lerp(map, road, c, d, t);
		GeneratedRoadPoint abbc = lerp(map, road, ab, bc, t);
		GeneratedRoadPoint bccd = lerp(map, road, bc, cd, t);
		return lerp(map, road, abbc, bccd, t);
	}

	private static double[][] calculateControlPoints(double[] src) {
		int length = src.length - 1;
		double[] p1 = new double[length];
		double[] p2 = new double[length];
		double[] a = new double[length];
		double[] b = new double[length];
		double[] c = new double[length];
		double[] r = new double[length];
		a[0] = 0.0D;
		b[0] = 2.0D;
		c[0] = 1.0D;
		r[0] = src[0] + 2.0D * src[1];

		int i;
		for (i = 1; i < length - 1; ++i) {
			a[i] = 1.0D;
			b[i] = 4.0D;
			c[i] = 1.0D;
			r[i] = 4.0D * src[i] + 2.0D * src[i + 1];
		}

		a[length - 1] = 2.0D;
		b[length - 1] = 7.0D;
		c[length - 1] = 0.0D;
		r[length - 1] = 8.0D * src[length - 1] + src[length];

		double p;
		for (i = 1; i < length; ++i) {
			p = a[i] / b[i - 1];
			b[i] -= p * c[i - 1];
			r[i] -= p * r[i - 1];
		}

		p1[length - 1] = r[length - 1] / b[length - 1];

		for (i = length - 2; i >= 0; --i) {
			p = (r[i] - c[i] * p1[i + 1]) / b[i];
			p1[i] = p;
		}

		for (i = 0; i < length - 1; ++i) {
			p2[i] = 2.0D * src[i + 1] - p1[i + 1];
		}

		p2[length - 1] = 0.5D * (src[length] + p1[length - 1]);
		return new double[][] { p1, p2 };
	}

	public static List generateSplines(MapSettings map, Road road, List controlPoints, RoadPointCache roadPointCache) {
		if (controlPoints.isEmpty() || controlPoints.size() == 1) {
			LOTRLog.warn("Road %s has only %d control points - a road requires at least 2!", road.getName(), controlPoints.size());
			return ImmutableList.of();
		}
		GeneratedRoadPoint cp1;
		int i;
		if (controlPoints.size() == 2) {
			RouteRoadPoint p1 = (RouteRoadPoint) controlPoints.get(0);
			RouteRoadPoint p2 = (RouteRoadPoint) controlPoints.get(1);
			double dx = p2.getMapX() - p1.getMapX();
			double dz = p2.getMapZ() - p1.getMapZ();
			int roadLength = map.mapToWorldDistance(Math.sqrt(dx * dx + dz * dz));
			int numPoints = Math.round(roadLength * 1.0F);
			GeneratedRoadPoint[] points = new GeneratedRoadPoint[numPoints];

			for (i = 0; i < numPoints; ++i) {
				double t = (double) i / (double) numPoints;
				cp1 = new GeneratedRoadPoint(map, road, p1.getMapX() + dx * t, p1.getMapZ() + dz * t);
				points[i] = cp1;
				roadPointCache.add(cp1);
			}

			RoadSection section = new RoadSection(road, p1, p2, points);
			return Arrays.asList(section);
		}
		int length = controlPoints.size();
		double[] x = new double[length];
		double[] z = new double[length];

		for (int i1 = 0; i1 < length; ++i1) {
			x[i1] = ((RouteRoadPoint) controlPoints.get(i1)).getMapX();
			z[i1] = ((RouteRoadPoint) controlPoints.get(i1)).getMapZ();
		}

		double[][] controlX = calculateControlPoints(x);
		double[][] controlZ = calculateControlPoints(z);
		int numControlPoints = controlX[0].length;
		GeneratedRoadPoint[] controlPoints1 = new GeneratedRoadPoint[numControlPoints];
		GeneratedRoadPoint[] controlPoints2 = new GeneratedRoadPoint[numControlPoints];

		for (int i1 = 0; i1 < numControlPoints; ++i1) {
			GeneratedRoadPoint p1 = new GeneratedRoadPoint(map, road, controlX[0][i1], controlZ[0][i1]);
			GeneratedRoadPoint p2 = new GeneratedRoadPoint(map, road, controlX[1][i1], controlZ[1][i1]);
			controlPoints1[i1] = p1;
			controlPoints2[i1] = p2;
		}

		RoadSection[] sections = new RoadSection[length - 1];

		for (i = 0; i < sections.length; ++i) {
			RouteRoadPoint p1 = (RouteRoadPoint) controlPoints.get(i);
			RouteRoadPoint p2 = (RouteRoadPoint) controlPoints.get(i + 1);
			cp1 = controlPoints1[i];
			GeneratedRoadPoint cp2 = controlPoints2[i];
			double dx = p2.getMapX() - p1.getMapX();
			double dz = p2.getMapZ() - p1.getMapZ();
			int roadLength = map.mapToWorldDistance(Math.sqrt(dx * dx + dz * dz));
			int numPoints = Math.round(roadLength * 1.0F);
			GeneratedRoadPoint[] points = new GeneratedRoadPoint[numPoints];

			for (int l = 0; l < numPoints; ++l) {
				double t = (double) l / (double) numPoints;
				GeneratedRoadPoint point = bezier(map, road, p1, cp1, cp2, p2, t);
				points[l] = point;
				roadPointCache.add(point);
			}

			sections[i] = new RoadSection(road, p1, p2, points);
		}

		return Arrays.asList(sections);
	}

	private static GeneratedRoadPoint lerp(MapSettings map, Road road, RoadPoint a, RoadPoint b, double t) {
		double x = a.getMapX() + (b.getMapX() - a.getMapX()) * t;
		double z = a.getMapZ() + (b.getMapZ() - a.getMapZ()) * t;
		return new GeneratedRoadPoint(map, road, x, z);
	}
}
