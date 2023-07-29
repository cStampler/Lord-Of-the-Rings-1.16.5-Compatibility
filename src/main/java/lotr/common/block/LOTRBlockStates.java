package lotr.common.block;

import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;

public class LOTRBlockStates {
	public static final EnumProperty SLAB_AXIS;
	public static final EnumProperty VERTICAL_ONLY_SLAB_AXIS;
	public static final BooleanProperty MOSSY;
	public static final BooleanProperty PILLAR_ABOVE;
	public static final BooleanProperty PILLAR_BELOW;
	public static final EnumProperty DRIPSTONE_TYPE;
	public static final int MAX_CLOVERS = 4;
	public static final IntegerProperty CLOVERS_1_4;
	public static final int MAX_CANDLES = 4;
	public static final IntegerProperty CANDLES_1_4;
	public static final BooleanProperty WATTLE_CONNECTED;
	public static final int TREASURE_PILE_MAX_LEVEL = 8;
	public static final IntegerProperty TREASURE_PILE_LEVEL;
	public static final BooleanProperty SUCH_WEALTH;
	public static final EnumProperty HANGING_WEB_TYPE;
	public static final BooleanProperty BRICK_ABOVE;
	public static final BooleanProperty DIRTY_CHALK_BELOW;
	public static final EnumProperty REEDS_TYPE;
	public static final BooleanProperty BEACON_FULLY_LIT;
	public static final BooleanProperty GATE_OPEN;

	static {
		SLAB_AXIS = BlockStateProperties.AXIS;
		VERTICAL_ONLY_SLAB_AXIS = BlockStateProperties.HORIZONTAL_AXIS;
		MOSSY = BooleanProperty.create("mossy");
		PILLAR_ABOVE = BooleanProperty.create("above");
		PILLAR_BELOW = BooleanProperty.create("below");
		DRIPSTONE_TYPE = EnumProperty.create("type", DripstoneBlock.Type.class);
		CLOVERS_1_4 = IntegerProperty.create("clovers", 1, 4);
		CANDLES_1_4 = IntegerProperty.create("candles", 1, 4);
		WATTLE_CONNECTED = BooleanProperty.create("connected");
		TREASURE_PILE_LEVEL = IntegerProperty.create("level", 1, 8);
		SUCH_WEALTH = BooleanProperty.create("such_wealth");
		HANGING_WEB_TYPE = EnumProperty.create("type", HangingWebBlock.Type.class);
		BRICK_ABOVE = BooleanProperty.create("above");
		DIRTY_CHALK_BELOW = BooleanProperty.create("below");
		REEDS_TYPE = EnumProperty.create("type", ReedsBlock.Type.class);
		BEACON_FULLY_LIT = BooleanProperty.create("fully_lit");
		GATE_OPEN = BooleanProperty.create("open");
	}
}
