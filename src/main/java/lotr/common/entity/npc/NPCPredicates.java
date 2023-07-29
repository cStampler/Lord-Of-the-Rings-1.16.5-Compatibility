package lotr.common.entity.npc;

import java.util.function.*;

import lotr.common.data.LOTRLevelData;
import lotr.common.fac.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;

public class NPCPredicates {
	public static Predicate selectAngerableByKill(Faction killedFaction, LivingEntity killerResponsible) {
		return entity -> (((LivingEntity) entity).isAlive() && EntityFactionHelper.getFaction((Entity) entity).isGoodRelation(killedFaction));
	}

	public static Predicate selectByFaction(Faction fac) {
		return entity -> (((LivingEntity) entity).isAlive() && EntityFactionHelper.getFaction((Entity) entity) == fac);
	}

	public static Predicate selectFoes(NPCEntity theEntity) {
		return selectPlayersOrOthers(theEntity, AlignmentPredicates.NEGATIVE, (hummel, hummel2) -> ((Faction) hummel).isBadRelation((Faction) hummel2)).or(e -> (e == theEntity.getTarget()));
	}

	public static Predicate selectForLocalAreaOfInfluence(Faction fac) {
		return selectByFaction(fac).and(entity -> (entity instanceof NPCEntity ? ((NPCEntity) entity).generatesLocalAreaOfInfluence() : true));
	}

	public static Predicate selectFriends(NPCEntity theEntity) {
		return selectPlayersOrOthers(theEntity, AlignmentPredicates.POSITIVE, (hummel, hummel2) -> ((Faction) hummel).isGoodRelation((Faction) hummel2)).and(e -> (e != theEntity.getTarget()));
	}

	private static Predicate selectPlayersOrOthers(NPCEntity theEntity, AlignmentPredicate playerTest, BiPredicate npcFactionTest) {
		Faction entityFaction = EntityFactionHelper.getFaction(theEntity);
		return otherEntity -> {
			if (otherEntity == theEntity || !((LivingEntity) otherEntity).isAlive()) {
				return false;
			}
			if (otherEntity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) otherEntity;
				return LOTRLevelData.getSidedData(player).getAlignmentData().hasAlignment(entityFaction, playerTest);
			}
			Faction otherFaction = EntityFactionHelper.getFaction((Entity) otherEntity);
			return npcFactionTest.test(otherFaction, entityFaction);
		};
	}
}
