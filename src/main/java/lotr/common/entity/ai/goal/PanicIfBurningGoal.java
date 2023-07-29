package lotr.common.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.PanicGoal;

public class PanicIfBurningGoal extends PanicGoal {
	public PanicIfBurningGoal(CreatureEntity entity, double speed) {
		super(entity, speed);
	}

	@Override
	public boolean canUse() {
		return mob.isOnFire() && super.canUse();
	}
}
