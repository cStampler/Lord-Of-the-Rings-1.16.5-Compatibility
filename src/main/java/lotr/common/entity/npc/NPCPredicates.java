package lotr.common.entity.npc;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import lotr.common.data.LOTRLevelData;
import lotr.common.fac.AlignmentPredicate;
import lotr.common.fac.AlignmentPredicates;
import lotr.common.fac.EntityFactionHelper;
import lotr.common.fac.Faction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class NPCPredicates {
	public static Predicate<MobEntity> selectAngerableByKill(Faction killedFaction, LivingEntity killerResponsible) {
		return entity -> (((LivingEntity) entity).isAlive() && EntityFactionHelper.getFaction((Entity) entity).isGoodRelation(killedFaction));
	}

	public static Predicate<LivingEntity> selectByFaction(Faction fac) {
		return entity -> (((LivingEntity) entity).isAlive() && EntityFactionHelper.getFaction((Entity) entity) == fac);
	}

	public static Predicate<LivingEntity> selectFoes(NPCEntity theEntity) {
		return selectPlayersOrOthers(theEntity, AlignmentPredicates.NEGATIVE, (hummel, hummel2) -> ((Faction) hummel).isBadRelation((Faction) hummel2)).or(e -> (e == theEntity.getTarget()));
	}

	public static Predicate<LivingEntity> selectForLocalAreaOfInfluence(Faction fac) {
		return selectByFaction(fac).and(entity -> (entity instanceof NPCEntity ? ((NPCEntity) entity).generatesLocalAreaOfInfluence() : true));
	}

	public static Predicate<LivingEntity> selectFriends(NPCEntity theEntity) {
		return selectPlayersOrOthers(theEntity, AlignmentPredicates.POSITIVE, (hummel, hummel2) -> ((Faction) hummel).isGoodRelation((Faction) hummel2)).and(e -> (e != theEntity.getTarget()));
	}

	private static Predicate<LivingEntity> selectPlayersOrOthers(NPCEntity theEntity, AlignmentPredicate playerTest, BiPredicate<Faction, Faction> npcFactionTest) {
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
