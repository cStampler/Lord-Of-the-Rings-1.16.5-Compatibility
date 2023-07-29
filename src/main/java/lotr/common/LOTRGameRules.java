package lotr.common;

import java.lang.reflect.Method;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class LOTRGameRules {
	public static RuleKey ORC_SKIRMISH;
	public static RuleKey MIDDLE_EARTH_RESPAWNING;
	public static RuleKey FAST_TRAVEL;
	public static RuleKey CUSTOM_WAYPOINT_CREATION;
	public static RuleKey GONDOR_BEACON_RANGE;
	public static RuleKey GONDOR_BEACON_LIGHTING_TIME;

	private static RuleType createBoolean(boolean defaultValue) {
		try {
			Method m = ObfuscationReflectionHelper.findMethod(BooleanValue.class, "create", Boolean.TYPE);
			return (RuleType) m.invoke((Object) null, defaultValue);
		} catch (Exception var2) {
			LOTRLog.error("Error creating new boolean gamerule type");
			throw new RuntimeException(var2);
		}
	}

	private static RuleType createInt(int defaultValue) {
		try {
			Method m = ObfuscationReflectionHelper.findMethod(IntegerValue.class, "create", Integer.TYPE);
			return (RuleType) m.invoke((Object) null, defaultValue);
		} catch (Exception var2) {
			LOTRLog.error("Error creating new integral gamerule type");
			throw new RuntimeException(var2);
		}
	}

	private static RuleKey register(String ruleName, Category category, RuleType type) {
		String namespacedName = new ResourceLocation("lotr", ruleName).toString();
		return GameRules.register(namespacedName, category, type);
	}

	public static void registerAll() {
		ORC_SKIRMISH = register("orc_skirmish", Category.MOBS, createBoolean(true));
		MIDDLE_EARTH_RESPAWNING = register("middle_earth_respawning", Category.PLAYER, createBoolean(true));
		FAST_TRAVEL = register("fast_travel", Category.PLAYER, createBoolean(true));
		CUSTOM_WAYPOINT_CREATION = register("custom_waypoint_creation", Category.PLAYER, createBoolean(true));
		GONDOR_BEACON_RANGE = register("gondor_beacon_range", Category.UPDATES, createInt(80));
		GONDOR_BEACON_LIGHTING_TIME = register("gondor_beacon_lighting_time", Category.UPDATES, createInt(100));
	}
}
