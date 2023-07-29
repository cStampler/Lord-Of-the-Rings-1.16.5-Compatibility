package lotr.common.world.gen.feature.grassblend;

import java.util.function.Supplier;

import lotr.common.block.ThreeLeafCloverBlock;
import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.world.gen.blockplacer.*;
import net.minecraft.world.gen.blockstateprovider.*;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig.Builder;

public class GrassBlends {
	public static final Supplier GRASS_CONFIG = () -> buildSimpleGrassConfig(Blocks.GRASS, 32);
	public static final Supplier FERN_CONFIG = () -> buildSimpleGrassConfig(Blocks.FERN, 32);
	public static final Supplier SHORT_GRASS_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.SHORT_GRASS.get(), 32);
	public static final Supplier WHEATGRASS_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.WHEATGRASS.get(), 32);
	public static final Supplier FLOWERY_GRASS_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.FLOWERY_GRASS.get(), 32);
	public static final Supplier PURPLE_MOOR_GRASS_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.PURPLE_MOOR_GRASS.get(), 100);
	public static final Supplier RED_MOOR_GRASS_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.RED_MOOR_GRASS.get(), 100);
	public static final Supplier THISTLE_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.THISTLE.get(), 32);
	public static final Supplier NETTLES_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.NETTLES.get(), 32);
	public static final Supplier FERNSPROUT_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.FERNSPROUT.get(), 32);
	public static final Supplier WILD_FLAX_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.WILD_FLAX.get(), 32);
	public static final Supplier ARID_GRASS_CONFIG = () -> buildSimpleGrassConfig((Block) LOTRBlocks.ARID_GRASS.get(), 32);
	public static final Supplier CLOVER_CONFIG = () -> {
		BlockState clover = ((Block) LOTRBlocks.CLOVER.get()).defaultBlockState();
		BlockState clover2 = clover.setValue(ThreeLeafCloverBlock.NUM_CLOVERS, 2);
		BlockState clover3 = clover.setValue(ThreeLeafCloverBlock.NUM_CLOVERS, 3);
		BlockState clover4 = clover.setValue(ThreeLeafCloverBlock.NUM_CLOVERS, 4);
		BlockState fourLeafClover = ((Block) LOTRBlocks.FOUR_LEAF_CLOVER.get()).defaultBlockState();
		return new Builder(new WeightedBlockStateProvider().add(clover, 1250).add(clover2, 1250).add(clover3, 1250).add(clover4, 1250).add(fourLeafClover, 1), new SimpleBlockPlacer()).tries(32).build();
	};
	public static final Supplier SHIRE_CLOVER_CONFIG = () -> {
		BlockState clover = ((Block) LOTRBlocks.CLOVER.get()).defaultBlockState();
		BlockState clover2 = clover.setValue(ThreeLeafCloverBlock.NUM_CLOVERS, 2);
		BlockState clover3 = clover.setValue(ThreeLeafCloverBlock.NUM_CLOVERS, 3);
		BlockState clover4 = clover.setValue(ThreeLeafCloverBlock.NUM_CLOVERS, 4);
		BlockState fourLeafClover = ((Block) LOTRBlocks.FOUR_LEAF_CLOVER.get()).defaultBlockState();
		return new Builder(new WeightedBlockStateProvider().add(clover, 1250).add(clover2, 1250).add(clover3, 1250).add(clover4, 1250).add(fourLeafClover, 10), new SimpleBlockPlacer()).tries(32).build();
	};
	public static final Supplier DOUBLE_GRASS_CONFIG = () -> buildSimpleDoubleGrassConfig(Blocks.TALL_GRASS, 64);
	public static final Supplier DOUBLE_FERN_CONFIG = () -> buildSimpleDoubleGrassConfig(Blocks.LARGE_FERN, 64);
	public static final Supplier DOUBLE_WHEATGRASS_CONFIG = () -> buildSimpleDoubleGrassConfig((Block) LOTRBlocks.TALL_WHEATGRASS.get(), 64);
	public static final Supplier DOUBLE_ARID_GRASS_CONFIG = () -> buildSimpleDoubleGrassConfig((Block) LOTRBlocks.TALL_ARID_GRASS.get(), 64);
	public static final SingleGrassBlend STANDARD;
	public static final SingleGrassBlend SHIRE;
	public static final SingleGrassBlend WITH_FERNS;
	public static final SingleGrassBlend SHIRE_WITH_FERNS;
	public static final SingleGrassBlend WITH_FERNS_AND_SPROUTS;
	public static final SingleGrassBlend EXTRA_WHEATGRASS;
	public static final SingleGrassBlend MOORS;
	public static final SingleGrassBlend SHIRE_MOORS;
	public static final SingleGrassBlend MOORS_WITH_FERNS;
	public static final SingleGrassBlend WITH_ARID;
	public static final SingleGrassBlend BASIC;
	public static final SingleGrassBlend MUTED;
	public static final SingleGrassBlend MUTED_WITHOUT_THISTLES;
	public static final SingleGrassBlend MUTED_WITH_FERNS;
	public static final DoubleGrassBlend DOUBLE_STANDARD;
	public static final DoubleGrassBlend DOUBLE_WITH_FERNS;
	public static final DoubleGrassBlend DOUBLE_WITH_EXTRA_WHEATGRASS;
	public static final DoubleGrassBlend DOUBLE_MOORS;
	public static final DoubleGrassBlend DOUBLE_MOORS_WITH_FERNS;
	public static final DoubleGrassBlend DOUBLE_WITH_ARID;
	public static final DoubleGrassBlend DOUBLE_MUTED;
	public static final DoubleGrassBlend DOUBLE_MUTED_WITH_FERNS;

	static {
		STANDARD = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 100, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10);
		SHIRE = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, SHIRE_CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 100, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10);
		WITH_FERNS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 100, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, FERN_CONFIG, 500);
		SHIRE_WITH_FERNS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, SHIRE_CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 100, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, FERN_CONFIG, 500);
		WITH_FERNS_AND_SPROUTS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 100, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, FERN_CONFIG, 500, FERNSPROUT_CONFIG, 500);
		EXTRA_WHEATGRASS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 800, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10);
		MOORS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, PURPLE_MOOR_GRASS_CONFIG, 80, RED_MOOR_GRASS_CONFIG, 240);
		SHIRE_MOORS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, SHIRE_CLOVER_CONFIG, 250, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, PURPLE_MOOR_GRASS_CONFIG, 50, RED_MOOR_GRASS_CONFIG, 270);
		MOORS_WITH_FERNS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, PURPLE_MOOR_GRASS_CONFIG, 50, RED_MOOR_GRASS_CONFIG, 270, FERN_CONFIG, 500);
		WITH_ARID = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, WHEATGRASS_CONFIG, 100, FLOWERY_GRASS_CONFIG, 65, WILD_FLAX_CONFIG, 2, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, ARID_GRASS_CONFIG, 550);
		BASIC = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100);
		MUTED = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10);
		MUTED_WITHOUT_THISTLES = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, NETTLES_CONFIG, 10);
		MUTED_WITH_FERNS = SingleGrassBlend.of(GRASS_CONFIG, 500, SHORT_GRASS_CONFIG, 100, CLOVER_CONFIG, 250, THISTLE_CONFIG, 5, NETTLES_CONFIG, 10, FERN_CONFIG, 500);
		DOUBLE_STANDARD = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500, DOUBLE_WHEATGRASS_CONFIG, 100);
		DOUBLE_WITH_FERNS = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500, DOUBLE_WHEATGRASS_CONFIG, 100, DOUBLE_FERN_CONFIG, 500);
		DOUBLE_WITH_EXTRA_WHEATGRASS = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500, DOUBLE_WHEATGRASS_CONFIG, 800);
		DOUBLE_MOORS = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500);
		DOUBLE_MOORS_WITH_FERNS = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500, DOUBLE_FERN_CONFIG, 500);
		DOUBLE_WITH_ARID = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500, DOUBLE_WHEATGRASS_CONFIG, 100, DOUBLE_ARID_GRASS_CONFIG, 550);
		DOUBLE_MUTED = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500);
		DOUBLE_MUTED_WITH_FERNS = DoubleGrassBlend.of(DOUBLE_GRASS_CONFIG, 500, DOUBLE_FERN_CONFIG, 500);
	}

	public static BlockClusterFeatureConfig buildSimpleDoubleGrassConfig(Block block, int tries) {
		return new Builder(new SimpleBlockStateProvider(block.defaultBlockState()), new DoublePlantBlockPlacer()).tries(tries).noProjection().build();
	}

	public static BlockClusterFeatureConfig buildSimpleGrassConfig(Block block, int tries) {
		return new Builder(new SimpleBlockStateProvider(block.defaultBlockState()), new SimpleBlockPlacer()).tries(tries).build();
	}
}
