package lotr.common.event;

import lotr.common.LOTRLog;
import lotr.common.config.*;
import lotr.common.init.LOTRDimensions;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent.Size;

public class BeeAdjustments {
	public static final float DOWNSCALE = 0.35F;

	public static void adjustSize(Size event) {
		event.setNewSize(event.getNewSize().scale(0.35F), true);
		BeeEntity bee = (BeeEntity) event.getEntity();
		float newWidth = event.getNewSize().width;
		float defaultWidth = EntityType.BEE.getWidth();
		if ((bee.isBaby() || newWidth != defaultWidth * 0.35F) && (!bee.isBaby() || newWidth != defaultWidth * 0.35F * 0.5F)) {
			LOTRLog.warn("Bee size is not as expected - %s [is child %s], size %s", bee.toString(), String.valueOf(bee.isBaby()), event.getNewSize());
			Thread.dumpStack();
		}

	}

	private static boolean isEnabledThroughConfig(World world) {
		return !world.isClientSide ? (Boolean) LOTRConfig.COMMON.smallerBees.get() : ClientsideCurrentServerConfigSettings.INSTANCE.smallerBees;
	}

	public static boolean shouldApply(Entity entity, World world) {
		if (world == null) {
			return false;
		}
		return isEnabledThroughConfig(world) && entity instanceof BeeEntity && LOTRDimensions.isModDimension(world);
	}
}
