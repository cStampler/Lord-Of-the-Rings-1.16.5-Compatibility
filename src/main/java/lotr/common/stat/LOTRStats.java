package lotr.common.stat;

import net.minecraft.stats.IStatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class LOTRStats {
	public static ResourceLocation INTERACT_FACTION_CRAFTING_TABLE;
	public static ResourceLocation RING_INTO_FIRE;
	public static ResourceLocation INTERACT_ALLOY_FORGE;
	public static ResourceLocation INTERACT_DWARVEN_FORGE;
	public static ResourceLocation INTERACT_ELVEN_FORGE;
	public static ResourceLocation INTERACT_ORC_FORGE;
	public static ResourceLocation INTERACT_HOBBIT_OVEN;
	public static ResourceLocation INTERACT_KEG;
	public static ResourceLocation FAST_TRAVEL;
	public static ResourceLocation FAST_TRAVEL_ONE_M;
	public static ResourceLocation CREATE_CUSTOM_WAYPOINT;
	public static ResourceLocation ADOPT_CUSTOM_WAYPOINT;
	public static ResourceLocation LIGHT_GONDOR_BEACON;
	public static ResourceLocation EXTINGUISH_GONDOR_BEACON;
	public static ResourceLocation TALK_TO_NPC;
	public static ResourceLocation OPEN_POUCH;
	public static ResourceLocation CLEAN_POUCH;
	public static ResourceLocation CLEAN_SMOKING_PIPE;
	public static final IStatFormatter DISTANCE_IN_M = value -> IStatFormatter.DISTANCE.format(value * 100);

	private static ResourceLocation registerCustom(String key, IStatFormatter formatter) {
		ResourceLocation res = new ResourceLocation("lotr", key);
		Registry.register(Registry.CUSTOM_STAT, res.toString(), res);
		Stats.CUSTOM.get(res, formatter);
		return res;
	}

	public static void setup() {
		INTERACT_FACTION_CRAFTING_TABLE = registerCustom("interact_faction_crafting_table", IStatFormatter.DEFAULT);
		RING_INTO_FIRE = registerCustom("throw_ring_in_fire", IStatFormatter.DEFAULT);
		INTERACT_ALLOY_FORGE = registerCustom("interact_alloy_forge", IStatFormatter.DEFAULT);
		INTERACT_DWARVEN_FORGE = registerCustom("interact_dwarven_forge", IStatFormatter.DEFAULT);
		INTERACT_ELVEN_FORGE = registerCustom("interact_elven_forge", IStatFormatter.DEFAULT);
		INTERACT_ORC_FORGE = registerCustom("interact_orc_forge", IStatFormatter.DEFAULT);
		INTERACT_HOBBIT_OVEN = registerCustom("interact_hobbit_oven", IStatFormatter.DEFAULT);
		INTERACT_KEG = registerCustom("interact_keg", IStatFormatter.DEFAULT);
		FAST_TRAVEL = registerCustom("fast_travel", IStatFormatter.DEFAULT);
		FAST_TRAVEL_ONE_M = registerCustom("fast_travel_one_m", DISTANCE_IN_M);
		CREATE_CUSTOM_WAYPOINT = registerCustom("create_custom_waypoint", IStatFormatter.DEFAULT);
		ADOPT_CUSTOM_WAYPOINT = registerCustom("adopt_custom_waypoint", IStatFormatter.DEFAULT);
		LIGHT_GONDOR_BEACON = registerCustom("light_gondor_beacon", IStatFormatter.DEFAULT);
		EXTINGUISH_GONDOR_BEACON = registerCustom("extinguish_gondor_beacon", IStatFormatter.DEFAULT);
		TALK_TO_NPC = registerCustom("talk_to_npc", IStatFormatter.DEFAULT);
		OPEN_POUCH = registerCustom("open_pouch", IStatFormatter.DEFAULT);
		CLEAN_POUCH = registerCustom("clean_pouch", IStatFormatter.DEFAULT);
		CLEAN_SMOKING_PIPE = registerCustom("clean_smoking_pipe", IStatFormatter.DEFAULT);
	}
}
