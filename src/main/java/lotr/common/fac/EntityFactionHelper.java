package lotr.common.fac;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.data.PlayerMessageType;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.data.NPCEntitySettingsManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

public class EntityFactionHelper {
	public static boolean canEntityCauseDamageToTarget(LivingEntity attacker, Entity target, boolean warnFriendlyFireForPlayer) {
		return attacker instanceof PlayerEntity ? canPlayerCauseDamageToTarget((PlayerEntity) attacker, target, warnFriendlyFireForPlayer) : canNonPlayerEntityCauseDamageToTarget(attacker, target, false);
	}

	public static boolean canNonPlayerEntityCauseDamageToTarget(LivingEntity attacker, Entity target, boolean isPlayerDirectedAttack) {
		Faction attackerFaction = getFaction(attacker);
		if (attacker instanceof NPCEntity) {
		}

		Optional<LivingEntity> attackerTarget = getEntityAttackTarget(attacker);
		Predicate<Entity> isNotAttackerTarget = e -> !attackerTarget.filter(Predicates.equalTo(e)).isPresent();
		Predicate<Entity> shouldNotHitNpc = isNotAttackerTarget.and(e -> attackerFaction.isGoodRelation(getFaction((Entity) e)));
		Predicate<Entity> shouldNotHitPlayer = isNotAttackerTarget.and(e -> (e instanceof PlayerEntity && LOTRLevelData.getSidedData((PlayerEntity) e).getAlignmentData().hasAlignment(attackerFaction, AlignmentPredicates.POSITIVE)));
		if (isNotAttackerTarget.test(target)) {
			if (shouldNotHitNpc.test(target) || isListNonemptyAndAllMatch(target.getPassengers(), shouldNotHitNpc)) {
				return false;
			}

			if (!isPlayerDirectedAttack && (shouldNotHitPlayer.test(target) || isListNonemptyAndAllMatch(target.getPassengers(), shouldNotHitPlayer))) {
				return false;
			}
		}

		return true;
	}

	public static boolean canPlayerCauseDamageToTarget(PlayerEntity attacker, Entity target, boolean warnFriendlyFire) {
		LOTRPlayerData playerData = LOTRLevelData.getSidedData(attacker);
		boolean friendlyFire = false;
		boolean friendlyFireEnabled = playerData.getAlignmentData().isFriendlyFireEnabled();
		if (target instanceof PlayerEntity && target != attacker) {
		}

		Entity alignedTarget = isAlignedToSomeFaction(target) ? target : (Entity) target.getPassengers().stream().filter(EntityFactionHelper::isAlignedToSomeFaction).findFirst().orElse((Entity) null);
		if (alignedTarget != null) {
			Faction targetFaction = getFaction(alignedTarget);
			if (alignedTarget instanceof MobEntity && !LOTRMod.PROXY.getSidedAttackTarget((MobEntity) alignedTarget).filter(Predicates.equalTo(attacker)).isPresent() && playerData.getAlignmentData().hasAlignment(targetFaction, AlignmentPredicates.POSITIVE)) {
				friendlyFire = true;
			}
		}

		if (friendlyFireEnabled || !friendlyFire) {
			return true;
		}
		if (warnFriendlyFire && !attacker.level.isClientSide) {
			playerData.getMessageData().sendMessageIfNotReceived(PlayerMessageType.FRIENDLY_FIRE);
		}

		return false;
	}

	private static Optional<LivingEntity> getEntityAttackTarget(LivingEntity entity) {
		return entity instanceof MobEntity ? LOTRMod.PROXY.getSidedAttackTarget((MobEntity) entity) : Optional.empty();
	}

	public static Faction getFaction(Entity entity) {
		return entity instanceof NPCEntity ? ((NPCEntity) entity).getFaction() : NPCEntitySettingsManager.getEntityTypeFaction(entity);
	}

	private static boolean isAlignedToSomeFaction(Entity entity) {
		return !FactionPointers.UNALIGNED.matches(getFaction(entity));
	}

	public static boolean isCivilian(Entity entity) {
		return entity instanceof NPCEntity && ((NPCEntity) entity).isCivilianNPC();
	}

	private static <T> boolean isListNonemptyAndAllMatch(List<T> list, Predicate<T> predicate) {
		return !list.isEmpty() && list.stream().allMatch(predicate);
	}
}
