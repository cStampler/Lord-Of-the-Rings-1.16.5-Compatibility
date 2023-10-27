package lotr.common.entity.npc.ai.goal;

import java.util.EnumSet;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BowItem;

public class NPCRangedAttackGoal extends Goal {
	private final MobEntity entity;
	private final Random rand;
	private final double moveSpeed;
	private int attackCooldown;
	private final float maxAttackDistanceSq;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public NPCRangedAttackGoal(MobEntity entity, double moveSpeed, int attackCooldown, float maxAttackDistance) {
		this.entity = entity;
		rand = entity.getRandom();
		this.moveSpeed = moveSpeed;
		this.attackCooldown = attackCooldown;
		maxAttackDistanceSq = maxAttackDistance * maxAttackDistance;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canContinueToUse() {
		return (canUse() || !entity.getNavigation().isDone()) && isBowInMainhand();
	}

	@Override
	public boolean canUse() {
		return entity.getTarget() == null ? false : isBowInMainhand();
	}

	protected boolean isBowInMainhand() {
		return entity.isHolding(testRangedWeapon());
	}

	public void setAttackCooldown(int cooldown) {
		attackCooldown = cooldown;
	}

	@Override
	public void start() {
		super.start();
		entity.setAggressive(true);
	}

	@Override
	public void stop() {
		super.stop();
		entity.setAggressive(false);
		seeTime = 0;
		attackTime = -1;
		entity.stopUsingItem();
		entity.getNavigation().stop();
	}

	protected Predicate testRangedWeapon() {
		return item -> (item instanceof BowItem);
	}

	@Override
	public void tick() {
		LivingEntity target = entity.getTarget();
		if (target != null) {
			double dSq = entity.distanceToSqr(target.getX(), target.getY(), target.getZ());
			boolean canSee = entity.getSensing().canSee(target);
			boolean wasSeeing = seeTime > 0;
			if (canSee != wasSeeing) {
				seeTime = 0;
			}

			if (canSee) {
				++seeTime;
			} else {
				--seeTime;
			}

			if (dSq <= maxAttackDistanceSq && seeTime >= 20) {
				entity.getNavigation().stop();
				++strafingTime;
			} else {
				entity.getNavigation().moveTo(target, moveSpeed);
				strafingTime = -1;
			}

			if (strafingTime >= 20) {
				if (rand.nextFloat() < 0.3D) {
					strafingClockwise = !strafingClockwise;
				}

				if (rand.nextFloat() < 0.3D) {
					strafingBackwards = !strafingBackwards;
				}

				strafingTime = 0;
			}

			if (strafingTime > -1) {
				if (dSq > maxAttackDistanceSq * 0.75F) {
					strafingBackwards = false;
				} else if (dSq < maxAttackDistanceSq * 0.25F) {
					strafingBackwards = true;
				}

				entity.getMoveControl().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
				entity.lookAt(target, 30.0F, 30.0F);
			} else {
				entity.getLookControl().setLookAt(target, 30.0F, 30.0F);
			}

			if (entity.isUsingItem()) {
				if (!canSee && seeTime < -60) {
					entity.stopUsingItem();
				} else if (canSee) {
					int useCount = entity.getTicksUsingItem();
					if (useCount >= 20) {
						entity.stopUsingItem();
						((IRangedAttackMob) entity).performRangedAttack(target, BowItem.getPowerForTime(useCount));
						attackTime = attackCooldown;
					}
				}
			} else if (--attackTime <= 0 && seeTime >= -60) {
				entity.startUsingItem(ProjectileHelper.getWeaponHoldingHand(entity, testRangedWeapon()));
			}
		}

	}
}
