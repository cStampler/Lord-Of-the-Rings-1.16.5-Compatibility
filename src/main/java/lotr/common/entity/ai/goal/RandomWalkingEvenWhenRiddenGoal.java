package lotr.common.entity.ai.goal;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.util.math.vector.Vector3d;

public class RandomWalkingEvenWhenRiddenGoal extends WaterAvoidingRandomWalkingGoal {
	public RandomWalkingEvenWhenRiddenGoal(CreatureEntity creature, double speed) {
		super(creature, speed);
	}

	@Override
	public boolean canContinueToUse() {
		return !mob.getNavigation().isDone();
	}

	@Override
	public boolean canUse() {
		if (!forceTrigger && (mob.getNoActionTime() >= 100 || mob.getRandom().nextInt(interval) != 0)) {
			return false;
		}

		Vector3d target = getPosition();
		if (target == null) {
			return false;
		}
		wantedX = target.x;
		wantedY = target.y;
		wantedZ = target.z;
		forceTrigger = false;
		return true;
	}
}
