package lotr.client.render.model.connectedtex;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.*;
import java.util.stream.*;

import com.google.common.collect.ImmutableSet;

import lotr.common.block.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.*;

public class ConnectedTexture3DContext implements IModelData {
	private static final Map ALL_RELEVANT_3D_CONTEXTS = Util.make(new HashMap(), map -> {
		int maxCombinedBits = (1 << ConnectedTexture3DContext.PositionOfInterest.values().length) - 1;

		for (int combinedBitFlag = 0; combinedBitFlag <= maxCombinedBits; ++combinedBitFlag) {
			ConnectedTexture3DContext ctx = new ConnectedTexture3DContext(combinedBitFlag);
			if (!ctx.hasIrrelevantPositions()) {
				map.put(combinedBitFlag, ctx);
			}
		}

	});
	private static final Map CONTEXT_TO_FACE_2D_CONTEXT_MAP;
	static {
		CONTEXT_TO_FACE_2D_CONTEXT_MAP = (Map) ALL_RELEVANT_3D_CONTEXTS.entrySet().stream().collect(Collectors.toMap(hummel -> ((Entry) hummel).getKey(), entry -> {
			ConnectedTexture3DContext ctx3d = (ConnectedTexture3DContext) ((Entry) entry).getValue();
			return (Map) Stream.of(Direction.values()).collect(Collectors.toMap(UnaryOperator.identity(), dir -> ConnectedTextureFaceMapper.get2dFrom3d(ctx3d, dir)));
		}));
	}

	private final int combinedPositionBitFlags;

	private ConnectedTexture3DContext(int combinedPositionBitFlags) {
		this.combinedPositionBitFlags = combinedPositionBitFlags;
	}

	private ConnectedTexture3DContext(Set positionsOfInterest) {
		int combined = 0;

		ConnectedTexture3DContext.PositionOfInterest poi;
		for (Iterator var3 = positionsOfInterest.iterator(); var3.hasNext(); combined |= poi.bitFlag) {
			poi = (ConnectedTexture3DContext.PositionOfInterest) var3.next();
		}

		combinedPositionBitFlags = combined;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other != null && other.getClass() == this.getClass()) {
			ConnectedTexture3DContext otherData = (ConnectedTexture3DContext) other;
			return combinedPositionBitFlags == otherData.combinedPositionBitFlags;
		}
		return false;
	}

	public int getCombinedBitFlags() {
		return combinedPositionBitFlags;
	}

	@Override
	public Object getData(ModelProperty prop) {
		return null;
	}

	public ConnectedTexture2DContext getFace2DContext(Direction face) {
		return (ConnectedTexture2DContext) ((Map) CONTEXT_TO_FACE_2D_CONTEXT_MAP.get(combinedPositionBitFlags)).get(face);
	}

	public boolean has(ConnectedTexture3DContext.PositionOfInterest poi) {
		return (combinedPositionBitFlags & poi.bitFlag) != 0;
	}

	@Override
	public int hashCode() {
		return getCombinedBitFlags();
	}

	private boolean hasIrrelevantPositions() {
		return ConnectedTexture3DContext.PositionOfInterest.COMPOUND_OFFSET_POSITIONS.stream().filter(hummel -> has((PositionOfInterest) hummel)).anyMatch(poi -> isIrrelevantCompoundOffsetPosition((PositionOfInterest) poi, hummel -> has((PositionOfInterest) hummel)));
	}

	@Override
	public boolean hasProperty(ModelProperty prop) {
		return false;
	}

	@Override
	public Object setData(ModelProperty prop, Object data) {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("ConnectedTexture3DContext[");
		int added = 0;
		ConnectedTexture3DContext.PositionOfInterest[] var3 = ConnectedTexture3DContext.PositionOfInterest.values();
		int var4 = var3.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			ConnectedTexture3DContext.PositionOfInterest poi = var3[var5];
			if (has(poi)) {
				if (added > 0) {
					s.append(", ");
				}

				s.append(poi.name());
				++added;
			}
		}

		s.append("]");
		return s.toString();
	}

	public static ConnectedTexture3DContext gatherFromWorld(IBlockDisplayReader world, BlockPos pos, BlockState state, TransformationMatrix blockstateRotation, ConnectedTexture3DContext.BlockConnectionType connectionType) {
		Set pois = EnumSet.noneOf(ConnectedTexture3DContext.PositionOfInterest.class);
		Mutable movingPos = new Mutable();
		ConnectedTexture3DContext.PositionOfInterest[] var7 = ConnectedTexture3DContext.PositionOfInterest.values();
		int var8 = var7.length;

		for (int var9 = 0; var9 < var8; ++var9) {
			ConnectedTexture3DContext.PositionOfInterest poi = var7[var9];
			movingPos.set(pos);
			Stream var10000 = poi.offsets.stream();
			blockstateRotation.getClass();
			List poiOffsets = (List) var10000.map(hummel -> blockstateRotation.rotateTransform((Direction) hummel)).collect(Collectors.toList());
			poiOffsets.forEach(hummel -> movingPos.move((Direction) hummel));
			if (connectionType.connects(state, world.getBlockState(movingPos), poiOffsets)) {
				pois.add(poi);
			}
		}

		pruneIrrelevantPositions(pois);
		return new ConnectedTexture3DContext(pois);
	}

	private static boolean isIrrelevantCompoundOffsetPosition(ConnectedTexture3DContext.PositionOfInterest poi, Predicate isOtherPoiContained) {
		Stream var10000 = poi.offsets.stream();
		Map var10001 = ConnectedTexture3DContext.PositionOfInterest.SIMPLE_OFFSET_POSITIONS;
		var10001.getClass();
		return var10000.map(var10001::get).noneMatch(isOtherPoiContained);
	}

	public static ConnectedTexture3DContext newContextFrom(Collection pois) {
		return new ConnectedTexture3DContext(EnumSet.copyOf(pois));
	}

	public static ConnectedTexture3DContext newEmptyContext() {
		return new ConnectedTexture3DContext(ImmutableSet.of());
	}

	private static void pruneIrrelevantPositions(Set pois) {
		pois.removeIf(poi -> {
			if (((PositionOfInterest) poi).isCompoundOffset()) {
				pois.getClass();
				if (isIrrelevantCompoundOffsetPosition((PositionOfInterest) poi, pois::contains)) {
					return true;
				}
			}

			return false;
		});
	}

	public enum BlockConnectionType {
		SAME_BLOCK("same_block", (state, otherState, offsets) -> (state.getBlock() == otherState.getBlock())), NO_CONNECTIONS("no_connections", (state, otherState, offsets) -> false), GATE("gate", GateBlock::doBlocksConnectVisually), CONNECTED_WATTLE("connected_wattle", WattleAndDaubBlock::doBlocksConnectVisually);

		private static final Map TYPES_BY_NAME = Stream.of(values()).collect(Collectors.toMap(type -> type.name, UnaryOperator.identity()));
		private final String name;
		private final ConnectedTexture3DContext.BlockConnectionType.BlockConnectionTest connectionTest;

		BlockConnectionType(String name, ConnectedTexture3DContext.BlockConnectionType.BlockConnectionTest connectionTest) {
			this.name = name;
			this.connectionTest = connectionTest;
		}

		public boolean connects(BlockState state, BlockState otherState, List offsets) {
			return connectionTest.test(state, otherState, offsets);
		}

		public static ConnectedTexture3DContext.BlockConnectionType getByName(String name) {
			return (ConnectedTexture3DContext.BlockConnectionType) TYPES_BY_NAME.get(name);
		}

		@FunctionalInterface
		public interface BlockConnectionTest {
			boolean test(BlockState var1, BlockState var2, List var3);
		}
	}

	public enum PositionOfInterest {
		DOWN("down", new Direction[] { Direction.DOWN }), UP("up", new Direction[] { Direction.UP }), NORTH("north", new Direction[] { Direction.NORTH }), SOUTH("south", new Direction[] { Direction.SOUTH }), WEST("west", new Direction[] { Direction.WEST }), EAST("east", new Direction[] { Direction.EAST }), DOWN_NORTH("down_north", new Direction[] { Direction.DOWN, Direction.NORTH }), DOWN_SOUTH("down_south", new Direction[] { Direction.DOWN, Direction.SOUTH }), DOWN_WEST("down_west", new Direction[] { Direction.DOWN, Direction.WEST }), DOWN_EAST("down_east", new Direction[] { Direction.DOWN, Direction.EAST }), UP_NORTH("up_north", new Direction[] { Direction.UP, Direction.NORTH }), UP_SOUTH("up_south", new Direction[] { Direction.UP, Direction.SOUTH }), UP_WEST("up_west", new Direction[] { Direction.UP, Direction.WEST }), UP_EAST("up_east", new Direction[] { Direction.UP, Direction.EAST }), NORTH_WEST("north_west", new Direction[] { Direction.NORTH, Direction.WEST }), NORTH_EAST("north_east", new Direction[] { Direction.NORTH, Direction.EAST }), SOUTH_WEST("south_west", new Direction[] { Direction.SOUTH, Direction.WEST }), SOUTH_EAST("south_east", new Direction[] { Direction.SOUTH, Direction.EAST });

		private static final Map POSITIONS_BY_NAME = Stream.of(values()).collect(Collectors.toMap(poi -> poi.nameInJson, UnaryOperator.identity()));
		public static final Map SIMPLE_OFFSET_POSITIONS = Stream.of(values()).filter(ConnectedTexture3DContext.PositionOfInterest::isSimpleOffset).collect(Collectors.toMap(poi -> ((Direction) poi.offsets.get(0)), UnaryOperator.identity()));
		public static final List COMPOUND_OFFSET_POSITIONS = Stream.of(values()).filter(ConnectedTexture3DContext.PositionOfInterest::isCompoundOffset).collect(Collectors.toList());
		public final int bitFlag = 1 << ordinal();
		public final String nameInJson;
		public final List offsets;

		PositionOfInterest(String s, Direction... offs) {
			nameInJson = s;
			offsets = Arrays.asList(offs);
			if (offsets.isEmpty()) {
				throw new IllegalArgumentException("Connected tex: position of interest '" + nameInJson + "' offsets must not be empty");
			}
		}

		public boolean isCompoundOffset() {
			return offsets.size() > 1;
		}

		public boolean isSimpleOffset() {
			return offsets.size() == 1;
		}

		public static ConnectedTexture3DContext.PositionOfInterest getByJsonName(String name) {
			return (ConnectedTexture3DContext.PositionOfInterest) POSITIONS_BY_NAME.get(name);
		}
	}
}
