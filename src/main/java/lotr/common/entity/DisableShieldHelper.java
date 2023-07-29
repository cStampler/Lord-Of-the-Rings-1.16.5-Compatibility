package lotr.common.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;

public class DisableShieldHelper {
	public static void disableShieldIfEntityShielding(Entity entity, boolean flag) {
		if (entity instanceof LivingEntity && ((LivingEntity) entity).isBlocking()) {
			if (entity instanceof PlayerEntity) {
				((PlayerEntity) entity).disableShield(flag);
			} else if (entity instanceof CanHaveShieldDisabled) {
				((CanHaveShieldDisabled) entity).disableShield(flag);
			}
		}

	}
}
