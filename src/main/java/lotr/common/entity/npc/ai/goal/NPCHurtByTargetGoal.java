package lotr.common.entity.npc.ai.goal;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.util.math.BlockPos;

public class NPCHurtByTargetGoal extends HurtByTargetGoal {
	private final NPCEntity theNPC;

	public NPCHurtByTargetGoal(NPCEntity entity) {
		super(entity, new Class[0]);
		theNPC = entity;
	}

	@Override
	protected boolean canAttack(LivingEntity potentialTarget, EntityPredicate targetPredicate) {
		if (potentialTarget != theNPC.getVehicle() && !theNPC.hasPassenger(potentialTarget)) {
			BlockPos homePos = theNPC.getRestrictCenter();
			int homeRange = (int) theNPC.getRestrictRadius();
			theNPC.clearHomePos();
			boolean superSuitable = super.canAttack(potentialTarget, targetPredicate);
			theNPC.restrictTo(homePos, homeRange);
			return superSuitable;
		}
		return false;
	}
}
