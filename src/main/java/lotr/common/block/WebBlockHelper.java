package lotr.common.block;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.SpiderEntity;

public class WebBlockHelper {
	public static boolean shouldApplyWebSlowness(Entity entity) {
		return !(entity instanceof SpiderEntity);
	}
}
