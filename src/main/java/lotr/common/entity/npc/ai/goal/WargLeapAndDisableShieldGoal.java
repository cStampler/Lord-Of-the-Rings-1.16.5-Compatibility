package lotr.common.entity.npc.ai.goal;

import lotr.common.entity.npc.WargEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LeapAtTargetGoal;

public class WargLeapAndDisableShieldGoal extends LeapAtTargetGoal {
	private final WargEntity theWarg;

	public WargLeapAndDisableShieldGoal(WargEntity warg, float leapMotionY) {
		super(warg, leapMotionY);
		theWarg = warg;
	}

	@Override
	public boolean canUse() {
		return super.canUse() && theWarg.getRandom().nextInt(5) == 0;
	}

	@Override
	public void start() {
		super.start();
		theWarg.startLeaping();
	}

	@Override
	public void tick() {
		super.tick();
		LivingEntity target = theWarg.getTarget();
		if (target != null) {
			theWarg.getLookControl().setLookAt(target, 360.0F, 360.0F);
		}

	}
}
