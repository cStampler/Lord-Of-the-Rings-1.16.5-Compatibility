package lotr.common.util;

import net.minecraft.world.*;

public class GameRuleUtil {
	public static boolean canDropLoot(World world) {
		return world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
	}
}
