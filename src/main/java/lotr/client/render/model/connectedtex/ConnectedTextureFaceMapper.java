package lotr.client.render.model.connectedtex;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.Direction;
import net.minecraft.util.Util;

public abstract class ConnectedTextureFaceMapper {
	public static final Map FACE_MAPPERS = Util.make(new HashMap(), map -> {
		map.put(Direction.DOWN, new ConnectedTextureFaceMapper() {
			@Override
			public ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition pos) {
				switch (pos) {
				case TOP_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH_WEST;
				case TOP:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH;
				case TOP_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH_EAST;
				case LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.WEST;
				case RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.EAST;
				case BOTTOM_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH_WEST;
				case BOTTOM:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH;
				case BOTTOM_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH_EAST;
				case CENTRE:
				default:
					throw new IllegalArgumentException(pos.name());
				}
			}
		});
		map.put(Direction.UP, new ConnectedTextureFaceMapper() {
			@Override
			public ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition pos) {
				switch (pos) {
				case TOP_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH_WEST;
				case TOP:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH;
				case TOP_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH_EAST;
				case LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.WEST;
				case RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.EAST;
				case BOTTOM_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH_WEST;
				case BOTTOM:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH;
				case BOTTOM_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH_EAST;
				case CENTRE:
				default:
					throw new IllegalArgumentException(pos.name());
				}
			}
		});
		map.put(Direction.NORTH, new ConnectedTextureFaceMapper() {
			@Override
			public ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition pos) {
				switch (pos) {
				case TOP_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_EAST;
				case TOP:
					return ConnectedTexture3DContext.PositionOfInterest.UP;
				case TOP_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_WEST;
				case LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.EAST;
				case RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.WEST;
				case BOTTOM_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_EAST;
				case BOTTOM:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN;
				case BOTTOM_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_WEST;
				case CENTRE:
				default:
					throw new IllegalArgumentException(pos.name());
				}
			}
		});
		map.put(Direction.SOUTH, new ConnectedTextureFaceMapper() {
			@Override
			public ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition pos) {
				switch (pos) {
				case TOP_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_WEST;
				case TOP:
					return ConnectedTexture3DContext.PositionOfInterest.UP;
				case TOP_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_EAST;
				case LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.WEST;
				case RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.EAST;
				case BOTTOM_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_WEST;
				case BOTTOM:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN;
				case BOTTOM_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_EAST;
				case CENTRE:
				default:
					throw new IllegalArgumentException(pos.name());
				}
			}
		});
		map.put(Direction.WEST, new ConnectedTextureFaceMapper() {
			@Override
			public ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition pos) {
				switch (pos) {
				case TOP_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_NORTH;
				case TOP:
					return ConnectedTexture3DContext.PositionOfInterest.UP;
				case TOP_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_SOUTH;
				case LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH;
				case RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH;
				case BOTTOM_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_NORTH;
				case BOTTOM:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN;
				case BOTTOM_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_SOUTH;
				case CENTRE:
				default:
					throw new IllegalArgumentException(pos.name());
				}
			}
		});
		map.put(Direction.EAST, new ConnectedTextureFaceMapper() {
			@Override
			public ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition pos) {
				switch (pos) {
				case TOP_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_SOUTH;
				case TOP:
					return ConnectedTexture3DContext.PositionOfInterest.UP;
				case TOP_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.UP_NORTH;
				case LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.SOUTH;
				case RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.NORTH;
				case BOTTOM_LEFT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_SOUTH;
				case BOTTOM:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN;
				case BOTTOM_RIGHT:
					return ConnectedTexture3DContext.PositionOfInterest.DOWN_NORTH;
				case CENTRE:
				default:
					throw new IllegalArgumentException(pos.name());
				}
			}
		});
	});

	private ConnectedTextureFaceMapper() {
	}

	// $FF: synthetic method
	ConnectedTextureFaceMapper(Object x0) {
		this();
	}

	abstract ConnectedTexture3DContext.PositionOfInterest getPositionToCheck(ConnectedTexture2DContext.RelativePosition var1);

	public static ConnectedTexture2DContext get2dFrom3d(ConnectedTexture3DContext ctx3d, Direction side) {
		ConnectedTextureFaceMapper faceMapper = (ConnectedTextureFaceMapper) FACE_MAPPERS.get(side);
		Set relativePositions = EnumSet.noneOf(ConnectedTexture2DContext.RelativePosition.class);
		ConnectedTexture2DContext.RelativePosition[] var4 = ConnectedTexture2DContext.RelativePosition.values();
		int var5 = var4.length;

		for (int var6 = 0; var6 < var5; ++var6) {
			ConnectedTexture2DContext.RelativePosition rPos = var4[var6];
			if (rPos == ConnectedTexture2DContext.RelativePosition.CENTRE || ctx3d.has(faceMapper.getPositionToCheck(rPos))) {
				relativePositions.add(rPos);
			}
		}

		return new ConnectedTexture2DContext(relativePositions);
	}
}
