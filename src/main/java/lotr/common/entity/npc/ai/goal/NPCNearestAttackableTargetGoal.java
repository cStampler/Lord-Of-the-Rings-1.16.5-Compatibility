package lotr.common.entity.npc.ai.goal;

import java.util.*;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import lotr.common.data.LOTRLevelData;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.ai.NPCTargetSorter;
import lotr.common.fac.AlignmentPredicates;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class NPCNearestAttackableTargetGoal extends NearestAttackableTargetGoal {
	private final NPCEntity theNPC;
	private final NPCTargetSorter targetSorter;

	public NPCNearestAttackableTargetGoal(NPCEntity entity, Class targetClass, boolean checkSight) {
		this(entity, targetClass, checkSight, Predicates.alwaysTrue());
	}

	public NPCNearestAttackableTargetGoal(NPCEntity entity, Class targetClass, boolean checkSight, Predicate selector) {
		super(entity, targetClass, checkSight);
		theNPC = entity;
		targetConditions.selector(e -> (canNPCTarget(e) && selector.test(e)));
		targetSorter = new NPCTargetSorter(theNPC);
	}

	protected boolean canNPCTarget(LivingEntity entity) {
		if (entity != theNPC.getVehicle() && !theNPC.hasPassenger(entity)) {
			return entity instanceof PlayerEntity ? isPlayerSuitableTarget((PlayerEntity) entity) : true;
		}
		return false;
	}

	@Override
	public boolean canUse() {
		return theNPC.isBaby() ? false : super.canUse();
	}

	@Override
	protected void findTarget() {
		List potentialTargets = theNPC.level.getEntitiesOfClass(targetType, getTargetSearchArea(getFollowDistance()), e -> targetConditions.test(theNPC, (LivingEntity) e));
		Collections.sort(potentialTargets, targetSorter);
		if (!potentialTargets.isEmpty()) {
			target = (LivingEntity) potentialTargets.get(0);
		} else {
			target = null;
		}

	}

	@Override
	protected AxisAlignedBB getTargetSearchArea(double targetDistance) {
		double rangeY = Math.min(targetDistance, 8.0D);
		return theNPC.getBoundingBox().inflate(targetDistance, rangeY, targetDistance);
	}

	protected boolean isPlayerSuitableAlignmentTarget(PlayerEntity player) {
		return LOTRLevelData.getSidedData(player).getAlignmentData().hasAlignment(theNPC.getFaction(), AlignmentPredicates.NEGATIVE);
	}

	protected boolean isPlayerSuitableTarget(PlayerEntity player) {
		return isPlayerSuitableAlignmentTarget(player);
	}
}
