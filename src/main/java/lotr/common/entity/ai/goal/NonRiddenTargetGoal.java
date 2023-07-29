package lotr.common.entity.ai.goal;

import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;

public class NonRiddenTargetGoal extends NearestAttackableTargetGoal {
	public NonRiddenTargetGoal(MobEntity entity, Class targetClass, int targetChance, boolean checkSight, boolean nearbyOnly, @Nullable Predicate targetPredicate) {
		super(entity, targetClass, targetChance, checkSight, nearbyOnly, targetPredicate);
	}

	@Override
	public boolean canUse() {
		return !mob.isVehicle() && super.canUse();
	}
}
