package lotr.common.entity.npc.ai;

import java.util.UUID;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.item.SpearItem;
import lotr.common.network.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.ForgeMod;

public class NPCCombatUpdater {
	private static final UUID SHIELDING_SLOWDOWN_ID = UUID.fromString("02dcfa3a-6713-4651-93e0-adaa8a55bf9c");
	private static final AttributeModifier SHIELDING_SLOWDOWN;
	private static final UUID MOUNTED_FOLLOW_RANGE_BOOST_ID;
	private static final AttributeModifier MOUNTED_FOLLOW_RANGE_BOOST;
	static {
		SHIELDING_SLOWDOWN = new AttributeModifier(SHIELDING_SLOWDOWN_ID, "Shielding slowdown", -0.3D, Operation.MULTIPLY_TOTAL);
		MOUNTED_FOLLOW_RANGE_BOOST_ID = UUID.fromString("72a017fc-767b-49fd-bc66-c91daa7b7b12");
		MOUNTED_FOLLOW_RANGE_BOOST = new AttributeModifier(MOUNTED_FOLLOW_RANGE_BOOST_ID, "Mounted follow range boost", 1.5D, Operation.MULTIPLY_BASE);
	}
	private final NPCEntity theEntity;
	private boolean ridingMount;
	private AttackMode currentAttackMode;
	private boolean firstUpdatedAttackMode;
	private boolean refreshAttackModeNextTick;
	private AttackModeUpdater attackModeUpdater;
	private int attackModeUpdateCooldown;
	private int combatCooldown;
	private boolean combatStance;
	private int ticksSinceAttacked;
	private boolean wasShielding;
	private int shieldDisableCooldown;

	public NPCCombatUpdater(NPCEntity entity) {
		currentAttackMode = AttackMode.IDLE;
		firstUpdatedAttackMode = false;
		refreshAttackModeNextTick = false;
		attackModeUpdater = StandardAttackModeUpdaters.meleeOnly();
		attackModeUpdateCooldown = 0;
		ticksSinceAttacked = Integer.MAX_VALUE;
		wasShielding = false;
		theEntity = entity;
	}

	private void checkAttackTarget() {
		LivingEntity target = theEntity.getTarget();
		if (target != null && (!target.isAlive() || target instanceof PlayerEntity && ((PlayerEntity) target).abilities.instabuild)) {
			theEntity.setTarget((LivingEntity) null);
		}

	}

	private boolean checkForAttackModeChange() {
		if (!theEntity.isBaby()) {
			LivingEntity target = theEntity.getTarget();
			if (target != null) {
				if (attackModeUpdateCooldown <= 0) {
					double dSq = theEntity.distanceToSqr(target);
					if (dSq >= getMeleeRangeSq() && !isCarryingSpearWithBackup()) {
						if (dSq < getMaxCombatRangeSq() && currentAttackMode != AttackMode.RANGED) {
							currentAttackMode = AttackMode.RANGED;
							return true;
						}
					} else if (currentAttackMode != AttackMode.MELEE) {
						currentAttackMode = AttackMode.MELEE;
						return true;
					}
				}
			} else if (currentAttackMode != AttackMode.IDLE && combatCooldown <= 0) {
				currentAttackMode = AttackMode.IDLE;
				return true;
			}
		}

		return false;
	}

	private boolean checkForMountedChange() {
		Entity mount = theEntity.getVehicle();
		boolean isRidingMountNow = mount instanceof LivingEntity && mount.isAlive() && !(mount instanceof NPCEntity);
		if (ridingMount != isRidingMountNow) {
			setRidingHorse(isRidingMountNow);
			return true;
		}
		return false;
	}

	private SPacketNPCState createCombatStancePacket() {
		return new SPacketNPCState(theEntity, SPacketNPCState.Type.COMBAT_STANCE, combatStance);
	}

	private double getMaxCombatRange() {
		double d = theEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
		return d * 0.95D;
	}

	public double getMaxCombatRangeSq() {
		double d = getMaxCombatRange();
		return d * d;
	}

	private double getMeleeRange() {
		double d = theEntity.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + 2.0D;
		if (ridingMount) {
			d *= 1.5D;
		}

		return d;
	}

	private double getMeleeRangeSq() {
		double d = getMeleeRange();
		return d * d;
	}

	public int getTicksSinceAttacked() {
		return ticksSinceAttacked;
	}

	private boolean isCarryingSpearWithBackup() {
		return theEntity.getMainHandItem().getItem() instanceof SpearItem && !theEntity.getNPCItemsInv().getSpearBackup().isEmpty();
	}

	public boolean isCombatStance() {
		return combatStance;
	}

	public boolean isShieldDisabled() {
		return shieldDisableCooldown > 0;
	}

	public void onAttacked() {
		ticksSinceAttacked = 0;
	}

	private void onAttackModeChange(AttackMode newMode, boolean newRiding) {
		attackModeUpdater.onAttackModeChange(theEntity, newMode, newRiding);
	}

	public void read(CompoundNBT nbt) {
		ridingMount = nbt.getBoolean("RidingHorse");
	}

	public void receiveClientCombatStance(boolean state) {
		if (!theEntity.level.isClientSide) {
			throw new IllegalStateException("This method should only be called on the clientside");
		}
		combatStance = state;
	}

	public void refreshCurrentAttackMode() {
		refreshAttackModeNextTick = true;
	}

	public void sendCombatStance(ServerPlayerEntity player) {
		LOTRPacketHandler.sendTo(createCombatStancePacket(), player);
	}

	private void sendCombatStanceToAllWatchers() {
		LOTRPacketHandler.sendToAllTrackingEntity(createCombatStancePacket(), theEntity);
	}

	public void setAttackModeUpdater(AttackModeUpdater updater) {
		attackModeUpdater = updater;
	}

	public void setRidingHorse(boolean flag) {
		ridingMount = flag;
		ModifiableAttributeInstance attrib = theEntity.getAttribute(Attributes.FOLLOW_RANGE);
		attrib.removePermanentModifier(MOUNTED_FOLLOW_RANGE_BOOST_ID);
		if (ridingMount) {
			attrib.addPermanentModifier(MOUNTED_FOLLOW_RANGE_BOOST);
		}

	}

	public void temporarilyDisableShield() {
		shieldDisableCooldown = 100;
	}

	private void updateAttackMode() {
		if (attackModeUpdateCooldown > 0) {
			--attackModeUpdateCooldown;
		}

		boolean changedMounted = checkForMountedChange();
		boolean changedAttackMode = checkForAttackModeChange();
		if (!firstUpdatedAttackMode) {
			firstUpdatedAttackMode = true;
			changedAttackMode = true;
		}

		if (refreshAttackModeNextTick) {
			refreshAttackModeNextTick = false;
			changedAttackMode = true;
		}

		if (changedAttackMode || changedMounted) {
			onAttackModeChange(currentAttackMode, ridingMount);
			if (changedAttackMode) {
				attackModeUpdateCooldown = 10;
			}
		}

	}

	public void updateCombat() {
		theEntity.level.getProfiler().push("NPCCombatUpdater#updateCombat");
		if (!theEntity.level.isClientSide) {
			checkAttackTarget();
			updateCombatCooldown();
			if (ticksSinceAttacked < Integer.MAX_VALUE) {
				++ticksSinceAttacked;
			}

			if (theEntity.isAlive()) {
				updateAttackMode();
			}

			updateCombatStance();
			updateShielding();
		}

		theEntity.level.getProfiler().pop();
	}

	private void updateCombatCooldown() {
		if (theEntity.getTarget() != null) {
			combatCooldown = 40;
		} else if (combatCooldown > 0) {
			--combatCooldown;
		}

	}

	private void updateCombatStance() {
		boolean prevCombatStance = combatStance;
		combatStance = combatCooldown > 0;
		if (combatStance != prevCombatStance) {
			sendCombatStanceToAllWatchers();
		}

	}

	private void updateShielding() {
		boolean isShielding = theEntity.isBlocking();
		if (isShielding != wasShielding) {
			wasShielding = isShielding;
			ModifiableAttributeInstance attrib = theEntity.getAttribute(Attributes.MOVEMENT_SPEED);
			attrib.removeModifier(SHIELDING_SLOWDOWN_ID);
			if (isShielding) {
				attrib.addTransientModifier(SHIELDING_SLOWDOWN);
			}
		}

		if (shieldDisableCooldown > 0) {
			--shieldDisableCooldown;
		}

	}

	public void write(CompoundNBT nbt) {
		nbt.putBoolean("RidingHorse", ridingMount);
	}
}
