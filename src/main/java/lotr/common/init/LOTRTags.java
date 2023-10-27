package lotr.common.init;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class LOTRTags {
	public static class Blocks {
		public static final INamedTag ORES_COPPER = tag("forge", "ores/copper");
		public static final INamedTag ORES_NITER = tag("forge", "ores/niter");
		public static final INamedTag ORES_SALT = tag("forge", "ores/salt");
		public static final INamedTag ORES_SILVER = tag("forge", "ores/silver");
		public static final INamedTag ORES_SULFUR = tag("forge", "ores/sulfur");
		public static final INamedTag ORES_TIN = tag("forge", "ores/tin");
		public static final INamedTag STORAGE_BLOCKS_BRONZE = tag("forge", "storage_blocks/bronze");
		public static final INamedTag STORAGE_BLOCKS_COPPER = tag("forge", "storage_blocks/copper");
		public static final INamedTag STORAGE_BLOCKS_NITER = tag("forge", "storage_blocks/niter");
		public static final INamedTag STORAGE_BLOCKS_SALT = tag("forge", "storage_blocks/salt");
		public static final INamedTag STORAGE_BLOCKS_SILVER = tag("forge", "storage_blocks/silver");
		public static final INamedTag STORAGE_BLOCKS_SULFUR = tag("forge", "storage_blocks/sulfur");
		public static final INamedTag STORAGE_BLOCKS_TIN = tag("forge", "storage_blocks/tin");
		public static final INamedTag SAND_WHITE = tag("forge", "sand/white");
		public static final INamedTag WOODEN_FENCES = tag("lotr", "wooden_fences");
		public static final INamedTag WOODEN_FENCE_GATES = tag("lotr", "wooden_fence_gates");
		public static final INamedTag PINE_LOGS = tag("lotr", "pine_logs");
		public static final INamedTag MALLORN_LOGS = tag("lotr", "mallorn_logs");
		public static final INamedTag MIRK_OAK_LOGS = tag("lotr", "mirk_oak_logs");
		public static final INamedTag CHARRED_LOGS = tag("lotr", "charred_logs");
		public static final INamedTag APPLE_LOGS = tag("lotr", "apple_logs");
		public static final INamedTag PEAR_LOGS = tag("lotr", "pear_logs");
		public static final INamedTag CHERRY_LOGS = tag("lotr", "cherry_logs");
		public static final INamedTag LEBETHRON_LOGS = tag("lotr", "lebethron_logs");
		public static final INamedTag BEECH_LOGS = tag("lotr", "beech_logs");
		public static final INamedTag MAPLE_LOGS = tag("lotr", "maple_logs");
		public static final INamedTag ASPEN_LOGS = tag("lotr", "aspen_logs");
		public static final INamedTag LAIRELOSSE_LOGS = tag("lotr", "lairelosse_logs");
		public static final INamedTag CEDAR_LOGS = tag("lotr", "cedar_logs");
		public static final INamedTag FIR_LOGS = tag("lotr", "fir_logs");
		public static final INamedTag LARCH_LOGS = tag("lotr", "larch_logs");
		public static final INamedTag HOLLY_LOGS = tag("lotr", "holly_logs");
		public static final INamedTag GREEN_OAK_LOGS = tag("lotr", "green_oak_logs");
		public static final INamedTag CYPRESS_LOGS = tag("lotr", "cypress_logs");
		public static final INamedTag ROTTEN_LOGS = tag("lotr", "rotten_logs");
		public static final INamedTag CULUMALDA_LOGS = tag("lotr", "culumalda_logs");
		public static final INamedTag LOG_SLABS = tag("lotr", "log_slabs");
		public static final INamedTag LOG_STAIRS = tag("lotr", "log_stairs");
		public static final INamedTag WOODEN_BEAMS = tag("lotr", "wooden_beams");
		public static final INamedTag WOODEN_BEAM_SLABS = tag("lotr", "wooden_beam_slabs");
		public static final INamedTag BRANCHES = tag("lotr", "branches");
		public static final INamedTag MORDOR_PLANT_SURFACES = tag("lotr", "mordor_plant_surfaces");
		public static final INamedTag REEDS_PLACEABLE_ON = tag("lotr", "reeds_placeable_on");
		public static final INamedTag CUSTOM_WAYPOINT_CENTERPIECES = tag("lotr", "custom_waypoint_centerpieces");
		public static final INamedTag BREAK_MALLORN_RESPONSES = tag("lotr", "break_mallorn_responses");
		public static final INamedTag PILLARS = tag("lotr", "pillars");
		public static final INamedTag DOL_AMROTH_BRICKS = tag("lotr", "dol_amroth_bricks");

		private static INamedTag tag(String namespace, String name) {
			return BlockTags.bind(new ResourceLocation(namespace, name).toString());
		}
	}

	public static class Items {
		public static final INamedTag APPLES = tag("forge", "apples");
		public static final INamedTag FURS = tag("forge", "furs");
		public static final INamedTag HORNS = tag("forge", "horns");
		public static final INamedTag INGOTS_BRONZE = tag("forge", "ingots/bronze");
		public static final INamedTag INGOTS_COPPER = tag("forge", "ingots/copper");
		public static final INamedTag INGOTS_SILVER = tag("forge", "ingots/silver");
		public static final INamedTag INGOTS_TIN = tag("forge", "ingots/tin");
		public static final INamedTag NUGGETS_BRONZE = tag("forge", "nuggets/bronze");
		public static final INamedTag NUGGETS_SILVER = tag("forge", "nuggets/silver");
		public static final INamedTag ORES_COPPER = tag("forge", "ores/copper");
		public static final INamedTag ORES_NITER = tag("forge", "ores/niter");
		public static final INamedTag ORES_SALT = tag("forge", "ores/salt");
		public static final INamedTag ORES_SILVER = tag("forge", "ores/silver");
		public static final INamedTag ORES_SULFUR = tag("forge", "ores/sulfur");
		public static final INamedTag ORES_TIN = tag("forge", "ores/tin");
		public static final INamedTag SALT = tag("forge", "salt");
		public static final INamedTag STORAGE_BLOCKS_BRONZE = tag("forge", "storage_blocks/bronze");
		public static final INamedTag STORAGE_BLOCKS_COPPER = tag("forge", "storage_blocks/copper");
		public static final INamedTag STORAGE_BLOCKS_NITER = tag("forge", "storage_blocks/niter");
		public static final INamedTag STORAGE_BLOCKS_SALT = tag("forge", "storage_blocks/salt");
		public static final INamedTag STORAGE_BLOCKS_SILVER = tag("forge", "storage_blocks/silver");
		public static final INamedTag STORAGE_BLOCKS_SULFUR = tag("forge", "storage_blocks/sulfur");
		public static final INamedTag STORAGE_BLOCKS_TIN = tag("forge", "storage_blocks/tin");
		public static final INamedTag SAND_WHITE = tag("forge", "sand/white");
		public static final INamedTag WOODEN_FENCES = tag("lotr", "wooden_fences");
		public static final INamedTag WOODEN_FENCE_GATES = tag("lotr", "wooden_fence_gates");
		public static final INamedTag PINE_LOGS = tag("lotr", "pine_logs");
		public static final INamedTag MALLORN_LOGS = tag("lotr", "mallorn_logs");
		public static final INamedTag MIRK_OAK_LOGS = tag("lotr", "mirk_oak_logs");
		public static final INamedTag CHARRED_LOGS = tag("lotr", "charred_logs");
		public static final INamedTag APPLE_LOGS = tag("lotr", "apple_logs");
		public static final INamedTag PEAR_LOGS = tag("lotr", "pear_logs");
		public static final INamedTag CHERRY_LOGS = tag("lotr", "cherry_logs");
		public static final INamedTag LEBETHRON_LOGS = tag("lotr", "lebethron_logs");
		public static final INamedTag BEECH_LOGS = tag("lotr", "beech_logs");
		public static final INamedTag MAPLE_LOGS = tag("lotr", "maple_logs");
		public static final INamedTag ASPEN_LOGS = tag("lotr", "aspen_logs");
		public static final INamedTag LAIRELOSSE_LOGS = tag("lotr", "lairelosse_logs");
		public static final INamedTag CEDAR_LOGS = tag("lotr", "cedar_logs");
		public static final INamedTag FIR_LOGS = tag("lotr", "fir_logs");
		public static final INamedTag LARCH_LOGS = tag("lotr", "larch_logs");
		public static final INamedTag HOLLY_LOGS = tag("lotr", "holly_logs");
		public static final INamedTag GREEN_OAK_LOGS = tag("lotr", "green_oak_logs");
		public static final INamedTag CYPRESS_LOGS = tag("lotr", "cypress_logs");
		public static final INamedTag ROTTEN_LOGS = tag("lotr", "rotten_logs");
		public static final INamedTag CULUMALDA_LOGS = tag("lotr", "culumalda_logs");
		public static final INamedTag LOG_SLABS = tag("lotr", "log_slabs");
		public static final INamedTag LOG_STAIRS = tag("lotr", "log_stairs");
		public static final INamedTag WOODEN_BEAMS = tag("lotr", "wooden_beams");
		public static final INamedTag WOODEN_BEAM_SLABS = tag("lotr", "wooden_beam_slabs");
		public static final INamedTag BRANCHES = tag("lotr", "branches");
		public static final INamedTag PILLARS = tag("lotr", "pillars");
		public static final INamedTag CLOVERS = tag("lotr", "clovers");
		public static final INamedTag CLAY_BALLS = tag("lotr", "clay_balls");
		public static final INamedTag BLUE_MALLORN_COLORANTS = tag("lotr", "blue_mallorn_colorants");
		public static final INamedTag GREEN_MALLORN_COLORANTS = tag("lotr", "green_mallorn_colorants");
		public static final INamedTag GOLD_MALLORN_COLORANTS = tag("lotr", "gold_mallorn_colorants");
		public static final INamedTag SILVER_MALLORN_COLORANTS = tag("lotr", "silver_mallorn_colorants");
		public static final INamedTag ALLOY_FORGE_EXTRA_SMELTABLES = tag("lotr", "alloy_forge_extra_smeltables");
		public static final INamedTag CHARRABLE_LOGS = tag("lotr", "charrable_logs");
		public static final INamedTag CHARRABLE_WOODS = tag("lotr", "charrable_woods");
		public static final INamedTag CHARRABLE_STRIPPED_LOGS = tag("lotr", "charrable_stripped_logs");
		public static final INamedTag CHARRABLE_STRIPPED_WOODS = tag("lotr", "charrable_stripped_woods");
		public static final INamedTag DRINK_VESSELS = tag("lotr", "drink_vessels");
		public static final INamedTag HOBBIT_OVEN_EXTRA_COOKABLES = tag("lotr", "hobbit_oven_extra_cookables");
		public static final INamedTag KEG_BREWING_WATER_SOURCES = tag("lotr", "keg_brewing_water_sources");
		public static final INamedTag ORC_DRAUGHT_MUSHROOMS = tag("lotr", "orc_draught_mushrooms");
		public static final INamedTag PIPE_MAGIC_SMOKE_INGREDIENTS = tag("lotr", "pipe_magic_smoke_ingredients");

		private static INamedTag tag(String namespace, String name) {
			return ItemTags.bind(new ResourceLocation(namespace, name).toString());
		}
	}
}
