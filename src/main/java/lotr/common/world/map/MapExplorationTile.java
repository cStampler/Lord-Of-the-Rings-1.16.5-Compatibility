package lotr.common.world.map;

import net.minecraft.util.math.MathHelper;

public class MapExplorationTile {
	public static final int SEARCH_DISTANCE_FROM_EXPLORED = 2;
	private final int mapX;
	private final int mapZ;
	private final int size;
	private final int distanceFromExplored;

	public MapExplorationTile(int mapX, int mapZ, int size, int distanceFromExplored) {
		this.mapX = mapX;
		this.mapZ = mapZ;
		this.size = size;
		this.distanceFromExplored = distanceFromExplored;
	}

	public int getMapBottom() {
		return mapZ + size;
	}

	public int getMapLeft() {
		return mapX;
	}

	public int getMapRight() {
		return mapX + size;
	}

	public int getMapTop() {
		return mapZ;
	}

	public int getPositionalHash() {
		return Math.abs((int) MathHelper.getSeed(mapX, 0, mapZ));
	}

	public int getSize() {
		return size;
	}

	public boolean isThickFog() {
		if (distanceFromExplored <= 1) {
			return false;
		}
		if (distanceFromExplored <= 2) {
			return getPositionalHash() % 8 == 1;
		}
		return getPositionalHash() % 12 != 1;
	}
}
