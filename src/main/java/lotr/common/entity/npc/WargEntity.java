package lotr.common.entity.npc;

import java.util.function.Predicate;

import lotr.common.entity.DisableShieldHelper;
import lotr.common.entity.ai.goal.NonRiddenTargetGoal;
import lotr.common.entity.ai.goal.PanicIfBurningGoal;
import lotr.common.entity.ai.goal.RandomWalkingEvenWhenRiddenGoal;
import lotr.common.entity.npc.ai.AttackGoalsHolder;
import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.ai.goal.WargLeapAndDisableShieldGoal;
import lotr.common.init.LOTRSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public abstract class WargEntity extends NPCEntity {
	private static final DataParameter<Integer> WARG_TYPE;
	private static final DataParameter<Boolean> IS_LEAPING;
	private static final Predicate<LivingEntity> ANIMAL_TARGETS;
	static {
		WARG_TYPE = EntityDataManager.defineId(WargEntity.class, DataSerializers.INT);
		IS_LEAPING = EntityDataManager.defineId(WargEntity.class, DataSerializers.BOOLEAN);
		ANIMAL_TARGETS = entity -> {
			EntityType<?> type = entity.getType();
			return type == EntityType.SHEEP || type == EntityType.CHICKEN || type == EntityType.RABBIT || type == EntityType.FOX;
		};
	}
	private int leapingTick = 0;
	private int leapingProgress;

	private int prevLeapingProgress = 0;

	protected WargEntity(EntityType<? extends WargEntity> type, World w) {
		super(type, w);
		spawnRequiresDarkness = true;
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putInt("WargType", getWargType().getId());
	}

	@Override
	protected void addNPCAI() {
		super.addNPCAI();
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new PanicIfBurningGoal(this, 1.6D));
		goalSelector.addGoal(3, new WargLeapAndDisableShieldGoal(this, 0.45F));
		addAttackGoal(4);
		goalSelector.addGoal(7, new RandomWalkingEvenWhenRiddenGoal(this, 1.0D));
		goalSelector.addGoal(8, new LookAtGoal(this, PlayerEntity.class, 12.0F, 0.02F));
		goalSelector.addGoal(8, new LookAtGoal(this, NPCEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(9, new LookAtGoal(this, LivingEntity.class, 12.0F, 0.02F));
		goalSelector.addGoal(10, new LookRandomlyGoal(this));
	}

	@Override
	protected void addNPCTargetingAI() {
		int target = addAggressiveTargetingGoals();
		targetSelector.addGoal(target + 1, new NonRiddenTargetGoal(this, AnimalEntity.class, 500, true, false, ANIMAL_TARGETS));
	}

	private void addWargRider(IServerWorld sw, DifficultyInstance diff, SpawnReason reason) {
		if (!level.isClientSide && canWargBeRidden() && random.nextInt(5) == 0) {
			NPCEntity rider = createWargRider();
			if (rider != null) {
				rider.moveTo(this.getX(), this.getY(), this.getZ(), yRot, 0.0F);
				rider.finalizeSpawn(sw, diff, reason, (ILivingEntityData) null, (CompoundNBT) null);
				if (isPersistenceRequired()) {
					rider.setPersistenceRequired();
				}

				rider.startRiding(this);
			}
		}

	}

	private void applyRandomisedWargAttributes() {
		getAttribute(Attributes.MAX_HEALTH).setBaseValue(MathHelper.nextInt(random, 24, 40));
		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(MathHelper.nextInt(random, 3, 5));
	}

	protected boolean canWargBeRidden() {
		return true;
	}

	protected abstract WargType chooseWargType();

	protected Goal createWargAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.7D);
	}

	protected abstract NPCEntity createWargRider();

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(WARG_TYPE, 0);
		entityData.define(IS_LEAPING, false);
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		boolean flag = super.doHurtTarget(target);
		if (leapingTick > 0) {
			DisableShieldHelper.disableShieldIfEntityShielding(target, true);
			leapingTick = 0;
		}

		return flag;
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		applyRandomisedWargAttributes();
		setWargType(chooseWargType());
		addWargRider(sw, diff, reason);
		return spawnData;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return LOTRSoundEvents.WARG_AMBIENT;
	}

	@Override
	protected SoundEvent getAttackSound() {
		return LOTRSoundEvents.WARG_ATTACK;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return LOTRSoundEvents.WARG_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return LOTRSoundEvents.WARG_HURT;
	}

	public boolean getIsLeaping() {
		return (Boolean) entityData.get(IS_LEAPING);
	}

	public float getLeapingProgress(float f) {
		return MathHelper.lerp(f, prevLeapingProgress, leapingProgress) / 5.0F;
	}

	@Override
	public double getPassengersRidingOffset() {
		return getBbHeight() * 0.63F;
	}

	public WargType getWargType() {
		return WargType.forId((Integer) entityData.get(WARG_TYPE));
	}

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		holder.setMeleeAttackGoal(createWargAttackGoal());
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		setWargType(WargType.forId(nbt.getInt("WargType")));
	}

	private void setIsLeaping(boolean flag) {
		entityData.set(IS_LEAPING, flag);
	}

	public void setWargType(WargType type) {
		entityData.set(WARG_TYPE, type.getId());
	}

	public void startLeaping() {
		leapingTick = 40;
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide) {
			if (leapingTick > 0) {
				--leapingTick;
			}

			setIsLeaping(leapingTick > 0 && !isOnGround());
		} else {
			prevLeapingProgress = leapingProgress;
			if (getIsLeaping()) {
				leapingProgress = Math.min(leapingProgress + 1, 5);
			} else {
				leapingProgress = Math.max(leapingProgress - 1, 0);
			}
		}

	}

	public static MutableAttribute regAttrs() {
		return NPCEntity.registerBaseNPCAttributes().add(Attributes.FOLLOW_RANGE, 32.0D).add(Attributes.MOVEMENT_SPEED, 0.22D).add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
	}
}
