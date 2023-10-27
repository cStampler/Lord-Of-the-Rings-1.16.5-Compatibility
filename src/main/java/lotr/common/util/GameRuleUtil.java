package lotr.common.util;

import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class GameRuleUtil {
	public static boolean canDropLoot(World world) {
		return world.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
	}
}
