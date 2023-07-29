package lotr.common.entity.npc.ai;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;

public class AttackGoalsHolder {
	private final NPCEntity theEntity;
	private Goal meleeAttackGoal;
	private Goal rangedAttackGoal;

	public AttackGoalsHolder(NPCEntity npc) {
		theEntity = npc;
	}

	public Goal getInitialAttackGoal() {
		if (meleeAttackGoal != null) {
			return meleeAttackGoal;
		}
		if (rangedAttackGoal != null) {
			return rangedAttackGoal;
		}
		throw new IllegalStateException("Tried to fetch the initial attack goal for an NPC (" + theEntity.getName().getString() + ") without any goals defined - this is a development error!");
	}

	public Goal getMeleeAttackGoal() {
		return meleeAttackGoal;
	}

	public Goal getNonNullMeleeAttackGoal() {
		if (meleeAttackGoal == null) {
			throw new IllegalStateException("Tried to fetch the melee attack goal for an NPC (" + theEntity.getName().getString() + ") without such a goal defined - this is a development error!");
		}
		return meleeAttackGoal;
	}

	public Goal getNonNullRangedAttackGoal() {
		if (rangedAttackGoal == null) {
			throw new IllegalStateException("Tried to fetch the ranged attack goal for an NPC (" + theEntity.getName().getString() + ") without such a goal defined - this is a development error!");
		}
		return rangedAttackGoal;
	}

	public Goal getRangedAttackGoal() {
		return rangedAttackGoal;
	}

	public void setMeleeAttackGoal(Goal goal) {
		if (meleeAttackGoal != null) {
			throw new IllegalStateException("meleeAttackGoal is already set!");
		}
		meleeAttackGoal = goal;
	}

	public void setRangedAttackGoal(Goal goal) {
		if (rangedAttackGoal != null) {
			throw new IllegalStateException("rangedAttackGoal is already set!");
		}
		rangedAttackGoal = goal;
	}
}
