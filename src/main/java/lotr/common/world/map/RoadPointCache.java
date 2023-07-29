package lotr.common.world.map;

import java.util.*;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.init.LOTRWorldTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.server.ServerWorld;

public class RoadPointCache {
	private Map pointMap = new HashMap();
	private Map roadAtQueryCache = new HashMap();

	public void add(RoadPoint point) {
		int x = (int) Math.round(point.getWorldX() / 1000.0D);
		int z = (int) Math.round(point.getWorldZ() / 1000.0D);
		int overlap = 1;

		for (int i = -overlap; i <= overlap; ++i) {
			for (int k = -overlap; k <= overlap; ++k) {
				int xKey = x + i;
				int zKey = z + k;
				getRoadList(xKey, zKey, true).add(point);
			}
		}

	}

	public List getPointsForCoords(int x, int z) {
		int x1 = x / 1000;
		int z1 = z / 1000;
		return getRoadList(x1, z1, false);
	}

	public float getRoadCentreCloseness(int x, int z, int width) {
		double widthSq = width * width;
		float mostCloseness = -1.0F;
		List points = getPointsForCoords(x, z);
		Iterator var8 = points.iterator();

		while (var8.hasNext()) {
			RoadPoint point = (RoadPoint) var8.next();
			double dx = point.getWorldX() - x;
			double dz = point.getWorldZ() - z;
			double distSq = dx * dx + dz * dz;
			if (distSq < widthSq) {
				double sqDistRatio = distSq / widthSq;
				float distRatio = 1.0F / (float) MathHelper.fastInvSqrt(sqDistRatio);
				float closeness = 1.0F - distRatio;
				if (mostCloseness == -1.0F || closeness > mostCloseness) {
					mostCloseness = closeness;
				}
			}
		}

		return mostCloseness;
	}

	private List getRoadList(int xKey, int zKey, boolean addToMap) {
		Pair key = Pair.of(xKey, zKey);
		List list = (List) pointMap.get(key);
		if (list == null) {
			list = new ArrayList();
			if (addToMap) {
				pointMap.put(key, list);
			}
		}

		return list;
	}

	public boolean isPartOfRoadWithinRange(int x, int z, int range) {
		return getRoadCentreCloseness(x, z, 4 + range) >= 0.0F;
	}

	public boolean isRoadAt(int x, int z) {
		Pair key = Pair.of(x, z);
		Boolean cachedResult = (Boolean) roadAtQueryCache.get(key);
		if (cachedResult == null) {
			cachedResult = getRoadCentreCloseness(x, z, 4) >= 0.0F;
			if (roadAtQueryCache.size() > 60000) {
				roadAtQueryCache.clear();
			}

			roadAtQueryCache.put(key, cachedResult);
		}

		return cachedResult;
	}

	public boolean isRoadAtPositionSurface(BlockPos pos) {
		return isRoadAt(pos.getX(), pos.getZ());
	}

	public static boolean checkNotGeneratingOnRoad(ISeedReader seedReader, BlockPos pos) {
		return filterNotGeneratingOnRoad(seedReader).test(pos);
	}

	public static boolean checkNotGeneratingWithinRangeOfRoad(ISeedReader seedReader, BlockPos pos, int range) {
		ServerWorld world = seedReader.getLevel();
		return !LOTRWorldTypes.hasMapFeatures(world) || !MapSettingsManager.sidedInstance(world).getCurrentLoadedMap().getRoadPointCache().isPartOfRoadWithinRange(pos.getX(), pos.getZ(), range);
	}

	public static Predicate filterNotGeneratingOnRoad(ISeedReader seedReader) {
		ServerWorld world = seedReader.getLevel();
		return pos -> (!LOTRWorldTypes.hasMapFeatures(world) || !MapSettingsManager.sidedInstance(world).getCurrentLoadedMap().getRoadPointCache().isRoadAtPositionSurface((BlockPos) pos));
	}
}
