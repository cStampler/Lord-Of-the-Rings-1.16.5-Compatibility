package lotr.common.entity.npc.ai.goal;

import java.util.*;
import java.util.stream.Stream;

import lotr.common.entity.npc.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

public class NPCMeleeAttackGoal extends Goal {
	private static final UUID DEFENDING_CLOSE_HALT_ID = UUID.fromString("c95298e0-93a9-4965-9189-52c9c151fb33");
	private static final AttributeModifier DEFENDING_CLOSE_HALT;
	static {
		DEFENDING_CLOSE_HALT = new AttributeModifier(DEFENDING_CLOSE_HALT_ID, "Halt when defending close", -1.0D, Operation.MULTIPLY_TOTAL);
	}
	private final NPCEntity theEntity;
	private final World world;
	private final Random rand;
	private final double speedTowardsTarget;
	private final boolean longMemory;
	private Path initialPath;
	private int rePathDelay;
	private long lastCheckTime;
	private int weaponAccountedSwingCooldown;
	private NPCMeleeAttackGoal.MeleeMode meleeMode;
	private int decideMeleeModeTimer;
	private int timeSinceDifferentMeleeMode;
	private float cachedFriendsToFoesRatio;
	private long lastFriendsToFoesCacheTime;
	private int ongoingShieldingTime;

	private int timeUntilCanShield;

	public NPCMeleeAttackGoal(NPCEntity entity, double speed) {
		this(entity, speed, true);
	}

	private NPCMeleeAttackGoal(NPCEntity entity, double speed, boolean longMem) {
		decideMeleeModeTimer = 0;
		timeSinceDifferentMeleeMode = 0;
		lastFriendsToFoesCacheTime = 0L;
		ongoingShieldingTime = 0;
		timeUntilCanShield = 0;
		theEntity = entity;
		world = theEntity.level;
		rand = theEntity.getRandom();
		speedTowardsTarget = speed;
		longMemory = longMem;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	private void baseMeleeTick() {
		LivingEntity target = theEntity.getTarget();
		theEntity.getLookControl().setLookAt(target, 30.0F, 30.0F);
		double dSq = theEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
		rePathDelay = Math.max(rePathDelay - 1, 0);
		if ((longMemory || theEntity.getSensing().canSee(target)) && rePathDelay <= 0) {
			rePathDelay = 4 + rand.nextInt(7);
			if (dSq > 1024.0D) {
				rePathDelay += 10;
			} else if (dSq > 256.0D) {
				rePathDelay += 5;
			}

			if (theEntity.getNavigation().isDone() && !this.isInRangeToAttack(target)) {
				theEntity.getNavigation().moveTo(theEntity.getNavigation().createPath(target, 0), speedTowardsTarget);
			} else if (!theEntity.getNavigation().moveTo(target, speedTowardsTarget)) {
				rePathDelay += 15;
			}
		}

		weaponAccountedSwingCooldown = Math.max(weaponAccountedSwingCooldown - 1, 0);
		checkAndPerformAttack(target, dSq);
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = theEntity.getTarget();
		if (target == null || !target.isAlive()) {
			return false;
		}
		if (!longMemory) {
			return !theEntity.getNavigation().isDone();
		}
		if (!(target instanceof PlayerEntity)) {
			return true;
		}
		PlayerEntity playerTarget = (PlayerEntity) target;
		return !playerTarget.isSpectator() && !playerTarget.isCreative();
	}

	@Override
	public boolean canUse() {
		long time = theEntity.level.getGameTime();
		if (time - lastCheckTime < 20L) {
			return false;
		}
		lastCheckTime = time;
		LivingEntity target = theEntity.getTarget();
		if (target != null && target.isAlive()) {
			initialPath = theEntity.getNavigation().createPath(target, 0);
			return initialPath != null || getAttackReachSq(target) >= theEntity.distanceToSqr(target.getX(), target.getY(), target.getZ());
		}
		return false;
	}

	private boolean canUseShield() {
		return getShieldingHand().isPresent() && !theEntity.getNPCCombatUpdater().isShieldDisabled();
	}

	protected void checkAndPerformAttack(LivingEntity target, double distToEnemySq) {
		if (meleeMode == NPCMeleeAttackGoal.MeleeMode.AGGRESSIVE && this.isInRangeToAttack(target, distToEnemySq) && weaponAccountedSwingCooldown <= 0) {
			resetSwingCooldown();
			theEntity.swing(Hand.MAIN_HAND);
			theEntity.doHurtTarget(target);
		}

	}

	private NPCMeleeAttackGoal.MeleeMode decideMeleeMode() {
		world.getProfiler().push("decideNewMeleeMode");
		NPCMeleeAttackGoal.MeleeMode decision = doDecideMeleeMode();
		world.getProfiler().pop();
		return decision;
	}

	private NPCMeleeAttackGoal.MeleeMode doDecideMeleeMode() {
		if (ongoingShieldingTime > 80 + rand.nextInt(50)) {
			ongoingShieldingTime = 0;
			timeUntilCanShield = 80 + rand.nextInt(60);
			return NPCMeleeAttackGoal.MeleeMode.AGGRESSIVE;
		}
		if (timeUntilCanShield > 0 || !canUseShield()) {
			return NPCMeleeAttackGoal.MeleeMode.AGGRESSIVE;
		}
		LivingEntity target = theEntity.getTarget();
		float attackReadiness = 1.0F;
		float lowHealthPriority;
		if (!this.isInRangeToAttack(target)) {
			int ticksSinceAttacked = theEntity.getNPCCombatUpdater().getTicksSinceAttacked();
			lowHealthPriority = 1.0F - ticksSinceAttacked / 100.0F;
			lowHealthPriority = MathHelper.clamp(lowHealthPriority, 0.0F, 1.0F);
			attackReadiness -= lowHealthPriority * 2.0F;
		} else {
			attackReadiness += 0.5F;
		}

		float health = theEntity.getHealth() / Math.max(theEntity.getMaxHealth(), 0.001F);
		lowHealthPriority = 1.0F - health / 0.4F;
		lowHealthPriority = MathHelper.clamp(lowHealthPriority, 0.0F, 1.0F);
		attackReadiness -= lowHealthPriority * 0.33F;
		world.getProfiler().push("getNearbyFoesToFriendsRatio");
		float foesFriendsRatio = getNearbyFoesToFriendsRatio();
		world.getProfiler().pop();
		if (foesFriendsRatio > 1.0F) {
			attackReadiness -= Math.min(foesFriendsRatio, 2.0F) * 0.33F;
		} else {
			attackReadiness += 1.0F / foesFriendsRatio * 0.25F;
		}

		if (target.isBlocking()) {
			attackReadiness -= 0.3F;
		}

		if (attackReadiness >= 0.9F) {
			return NPCMeleeAttackGoal.MeleeMode.AGGRESSIVE;
		}
		if (attackReadiness <= 0.1F) {
			return NPCMeleeAttackGoal.MeleeMode.DEFENSIVE;
		}
		if (meleeMode != null && timeSinceDifferentMeleeMode < 40) {
			return meleeMode;
		}
		return rand.nextFloat() < attackReadiness ? NPCMeleeAttackGoal.MeleeMode.AGGRESSIVE : NPCMeleeAttackGoal.MeleeMode.DEFENSIVE;
	}

	protected double getAttackReachSq(LivingEntity target) {
		double fullReach = theEntity.getAttributeValue(ForgeMod.REACH_DISTANCE.get());
		return fullReach * fullReach;
	}

	private float getNearbyFoesToFriendsRatio() {
		long gameTime = world.getGameTime();
		if (gameTime - lastFriendsToFoesCacheTime > 10L) {
			Set friends = new HashSet();
			Set foes = new HashSet();
			friends.add(theEntity);
			foes.add(theEntity.getTarget());
			double nearbyRange = 8.0D;
			AxisAlignedBB checkBox = theEntity.getBoundingBox().inflate(nearbyRange);
			friends.addAll(theEntity.level.getLoadedEntitiesOfClass(LivingEntity.class, checkBox, NPCPredicates.selectFriends(theEntity)));
			foes.addAll(theEntity.level.getLoadedEntitiesOfClass(LivingEntity.class, checkBox, NPCPredicates.selectFoes(theEntity)));
			friends.removeAll(foes);
			cachedFriendsToFoesRatio = (float) foes.size() / (float) friends.size();
			lastFriendsToFoesCacheTime = gameTime;
		}

		return cachedFriendsToFoesRatio;
	}

	private Optional getShieldingHand() {
		return Stream.of(Hand.values()).filter(hand -> theEntity.getItemInHand(hand).isShield(theEntity)).findFirst();
	}

	private int getWeaponMeleeCooldown() {
		return theEntity.getAttackCooldownTicks();
	}

	private boolean isInRangeToAttack(LivingEntity target) {
		return this.isInRangeToAttack(target, theEntity.distanceToSqr(target));
	}

	private boolean isInRangeToAttack(LivingEntity target, double distToEnemySq) {
		double reachSq = getAttackReachSq(target);
		return distToEnemySq <= reachSq;
	}

	protected void resetSwingCooldown() {
		weaponAccountedSwingCooldown = getWeaponMeleeCooldown();
	}

	@Override
	public void start() {
		theEntity.getNavigation().moveTo(initialPath, speedTowardsTarget);
		theEntity.setAggressive(true);
		rePathDelay = 0;
		weaponAccountedSwingCooldown = 0;
		updateMeleeMode(decideMeleeMode());
		decideMeleeModeTimer = 5;
		ongoingShieldingTime = 0;
		timeUntilCanShield = 0;
	}

	@Override
	public void stop() {
		LivingEntity target = theEntity.getTarget();
		if (!EntityPredicates.NO_CREATIVE_OR_SPECTATOR.test(target)) {
			theEntity.setTarget((LivingEntity) null);
		}

		theEntity.setAggressive(false);
		theEntity.getNavigation().stop();
		updateMeleeMode((NPCMeleeAttackGoal.MeleeMode) null);
		decideMeleeModeTimer = 0;
		updateHaltIfDefendingClose();
		ongoingShieldingTime = 0;
		timeUntilCanShield = 0;
	}

	@Override
	public void tick() {
		if (meleeMode == NPCMeleeAttackGoal.MeleeMode.DEFENSIVE && !canUseShield()) {
			updateMeleeMode(NPCMeleeAttackGoal.MeleeMode.AGGRESSIVE);
		}

		if (theEntity.isBlocking()) {
			++ongoingShieldingTime;
		} else {
			ongoingShieldingTime = 0;
		}

		timeUntilCanShield = Math.max(timeUntilCanShield - 1, 0);
		++timeSinceDifferentMeleeMode;
		if (decideMeleeModeTimer > 0) {
			--decideMeleeModeTimer;
		} else {
			updateMeleeMode(decideMeleeMode());
		}

		baseMeleeTick();
		updateHaltIfDefendingClose();
	}

	private void updateHaltIfDefendingClose() {
		ModifiableAttributeInstance attrib = theEntity.getAttribute(Attributes.MOVEMENT_SPEED);
		attrib.removeModifier(DEFENDING_CLOSE_HALT_ID);
		if (meleeMode == NPCMeleeAttackGoal.MeleeMode.DEFENSIVE) {
			double dSq = theEntity.distanceToSqr(theEntity.getTarget());
			double defendingHaltRange = 2.0D;
			if (dSq < defendingHaltRange * defendingHaltRange) {
				attrib.addTransientModifier(DEFENDING_CLOSE_HALT);
			}
		}

	}

	private void updateMeleeMode(NPCMeleeAttackGoal.MeleeMode newMode) {
		if (newMode != meleeMode) {
			meleeMode = newMode;
			timeSinceDifferentMeleeMode = 0;
			if (meleeMode == NPCMeleeAttackGoal.MeleeMode.DEFENSIVE) {
				Optional var10000 = getShieldingHand();
				NPCEntity var10001 = theEntity;
				var10000.ifPresent(hummel -> var10001.startUsingItem((Hand) hummel));
			} else {
				theEntity.releaseUsingItem();
			}
		}

	}

	public enum MeleeMode {
		AGGRESSIVE, DEFENSIVE;
	}
}
