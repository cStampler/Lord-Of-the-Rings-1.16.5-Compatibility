package lotr.common.entity.npc.ai.goal;

import java.util.EnumSet;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class TalkToCurrentGoal extends Goal {
	private final NPCEntity entity;
	private final World world;
	private LivingEntity talkingTo;
	private int repathTimer;

	public TalkToCurrentGoal(NPCEntity entity) {
		this.entity = entity;
		world = entity.level;
		setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
	}

	@Override
	public boolean canContinueToUse() {
		return canUse();
	}

	@Override
	public boolean canUse() {
		talkingTo = entity.getTalkingToEntity();
		if (talkingTo == null || !FriendlyNPCConversationGoal.isAvailableForTalking(talkingTo) || talkingTo instanceof NPCEntity && ((NPCEntity) talkingTo).getTalkingToEntity() != entity) {
			return false;
		}
		double dSq = entity.distanceToSqr(talkingTo);
		double maxDist = entity.getTalkingToInitialDistance() + 4.0F;
		return dSq <= maxDist * maxDist;
	}

	@Override
	public void start() {
		entity.getTalkAnimations().startTalking();
		repathTimer = 0;
		entity.getNavigation().stop();
	}

	@Override
	public void stop() {
		talkingTo = null;
		entity.clearTalkingToEntity();
		entity.getTalkAnimations().stopTalking();
		repathTimer = 0;
		entity.getNavigation().stop();
	}

	@Override
	public void tick() {
		world.getProfiler().push("TalkToCurrentGoal#tick");
		talkingTo = entity.getTalkingToEntity();
		if (talkingTo != null) {
			entity.getLookControl().setLookAt(talkingTo.getX(), talkingTo.getEyeY(), talkingTo.getZ());
			if (!(talkingTo instanceof PlayerEntity) && entity.distanceToSqr(talkingTo) > 9.0D) {
				--repathTimer;
				if (repathTimer <= 0) {
					repathTimer = 10;
					entity.getNavigation().moveTo(talkingTo, 1.0D);
				}
			} else {
				entity.getNavigation().stop();
				repathTimer = 0;
			}
		}

		world.getProfiler().pop();
	}
}
