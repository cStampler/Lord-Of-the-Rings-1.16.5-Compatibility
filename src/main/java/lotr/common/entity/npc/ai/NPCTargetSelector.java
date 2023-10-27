package lotr.common.entity.npc.ai;

import java.util.function.Predicate;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.fac.EntityFactionHelper;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionPointers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class NPCTargetSelector implements Predicate {
	private NPCEntity owner;

	public NPCTargetSelector(NPCEntity entity) {
		owner = entity;
	}

	@Override
	public boolean test(Object target) {
		Faction ownerFaction = owner.getFaction();
		if (FactionPointers.HOSTILE.matches(ownerFaction) && (target.getClass().isAssignableFrom(owner.getClass()) || owner.getClass().isAssignableFrom(target.getClass()))) {
			return false;
		}
		if (((LivingEntity) target).isAlive()) {
			if (target instanceof NPCEntity && !((NPCEntity) target).canBeFreelyTargetedBy(owner)) {
				return false;
			}

			if (!ownerFaction.approvesCivilianKills() && target instanceof NPCEntity && ((NPCEntity) target).isCivilianNPC()) {
				return false;
			}

			Faction targetFaction = EntityFactionHelper.getFaction((Entity) target);
			if (ownerFaction.isBadRelation(targetFaction)) {
				return true;
			}

			if (ownerFaction.isNeutral(targetFaction)) {
			}
		}

		return false;
	}
}
