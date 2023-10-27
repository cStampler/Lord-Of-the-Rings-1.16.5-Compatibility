package lotr.common.entity.npc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.config.LOTRConfig;
import lotr.common.data.DataUtil;
import lotr.common.data.LOTRLevelData;
import lotr.common.entity.CanHaveShieldDisabled;
import lotr.common.entity.LOTREntityDataSerializers;
import lotr.common.entity.npc.ai.AttackGoalsHolder;
import lotr.common.entity.npc.ai.AttackMode;
import lotr.common.entity.npc.ai.NPCCombatUpdater;
import lotr.common.entity.npc.ai.NPCTalkAnimations;
import lotr.common.entity.npc.ai.NPCTargetSelector;
import lotr.common.entity.npc.ai.goal.NPCHurtByTargetGoal;
import lotr.common.entity.npc.ai.goal.NPCNearestAttackableTargetGoal;
import lotr.common.entity.npc.ai.goal.StargazingGoal;
import lotr.common.entity.npc.ai.goal.WatchSunriseSunsetGoal;
import lotr.common.entity.npc.data.NPCEntitySettingsManager;
import lotr.common.entity.npc.data.NPCGenderProvider;
import lotr.common.entity.npc.data.NPCPersonalInfo;
import lotr.common.entity.npc.data.name.NPCNameGenerator;
import lotr.common.entity.npc.data.name.NPCNameGenerators;
import lotr.common.entity.npc.inv.NPCItemsInventory;
import lotr.common.fac.AlignmentPredicates;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionPointer;
import lotr.common.init.LOTRAttributes;
import lotr.common.init.LOTRBiomes;
import lotr.common.item.ItemOwnership;
import lotr.common.item.PouchItem;
import lotr.common.speech.NPCSpeechSender;
import lotr.common.speech.SpecialSpeechbanks;
import lotr.common.stat.LOTRStats;
import lotr.common.util.CalendarUtil;
import lotr.common.util.LOTRUtil;
import lotr.common.world.spawning.NPCSpawnSettingsManager;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

public abstract class NPCEntity extends CreatureEntity implements IRangedAttackMob, CanHaveShieldDisabled {
	private static final DataParameter FACTION_OVERRIDE;
	static {
		FACTION_OVERRIDE = EntityDataManager.defineId(NPCEntity.class, LOTREntityDataSerializers.OPTIONAL_FACTION_POINTER);
	}
	protected final NPCPersonalInfo personalInfo = new NPCPersonalInfo(this);
	protected final NPCItemsInventory npcItemsInv;
	private final NPCCombatUpdater combatUpdater;
	private final AttackGoalsHolder attackGoalsHolder;
	private int attackGoalIndex = -1;
	private boolean addedTargetingGoals = false;
	private boolean isTargetSeeker = false;
	private UUID prevAttackTargetUuid;
	private boolean loggedMissingFactionOverride = false;
	private int speechCooldown;
	private LivingEntity talkingTo;
	private int talkingToTime;
	private float talkingToInitialDistance;
	private final NPCTalkAnimations talkAnimations;
	private Optional speechbankOverride = Optional.empty();
	protected boolean spawnRequiresDarkness = false;
	protected boolean spawnRequiresSurfaceBlock = false;
	public boolean spawnRidingHorse = false;

	private List capturedLootForEnpouching = null;

	protected NPCEntity(EntityType type, World w) {
		super(type, w);
		setupNPCInfo();
		recalculateReachDistance();
		attackGoalsHolder = new AttackGoalsHolder(this);
		addNPCAI();
		npcItemsInv = new NPCItemsInventory(this);
		combatUpdater = new NPCCombatUpdater(this);
		talkAnimations = new NPCTalkAnimations(this);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		personalInfo.write(nbt);
		npcItemsInv.writeToEntityNBT(nbt);
		combatUpdater.write(nbt);
		DataUtil.writeOptionalFactionPointerToNBT(nbt, "FactionOverride", getFactionOverride());
		DataUtil.writeOptionalToNBT(nbt, "SpeechbankOverride", speechbankOverride, (hummel, hummel2, hummel3) -> DataUtil.putResourceLocation((CompoundNBT) hummel, (String) hummel2, (ResourceLocation) hummel3));
		nbt.putInt("NPCHomeX", getRestrictCenter().getX());
		nbt.putInt("NPCHomeY", getRestrictCenter().getY());
		nbt.putInt("NPCHomeZ", getRestrictCenter().getZ());
		nbt.putInt("NPCHomeRadius", (int) getRestrictRadius());
	}

	protected final int addAggressiveTargetingGoals() {
		return addTargetingGoals(true);
	}

	protected void addAttackGoal(int i) {
		attackGoalIndex = i;
		goalSelector.addGoal(i, attackGoalsHolder.getInitialAttackGoal());
	}

	protected final int addNonAggressiveTargetingGoals() {
		return addTargetingGoals(false);
	}

	protected void addNPCAI() {
		((GroundPathNavigator) getNavigation()).setCanOpenDoors(true);
		getNavigation().setCanFloat(true);
		setPathfindingMalus(PathNodeType.DANGER_FIRE, 16.0F);
		setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
		initialiseAttackGoals(attackGoalsHolder);
		addNPCTargetingAI();
	}

	protected void addNPCTargetingAI() {
	}

	private int addTargetingGoals(boolean seekTargets) {
		if (addedTargetingGoals) {
			throw new IllegalStateException("Mod development error - NPC addTargetingGoals can only be called once!");
		}
		addedTargetingGoals = true;
		int i = 1;
		targetSelector.addGoal(i, new NPCHurtByTargetGoal(this));
		if (seekTargets) {
			targetSelector.addGoal(i, new NPCNearestAttackableTargetGoal(this, PlayerEntity.class, true));
			i++;
			targetSelector.addGoal(i, new NPCNearestAttackableTargetGoal(this, MobEntity.class, true, new NPCTargetSelector(this)));
			i++;
		}

		isTargetSeeker = seekTargets;
		return i;
	}

	@Override
	protected void blockUsingShield(LivingEntity attacker) {
		super.blockUsingShield(attacker);
		if (attacker.getMainHandItem().canDisableShield(useItem, this, attacker)) {
			disableShield(true);
		}

	}

	protected double calculateDefaultNPCReach() {
		return 0.5F * getBbWidth() + 1.5F;
	}

	public boolean canBeFreelyTargetedBy(LivingEntity attacker) {
		return true;
	}

	@Override
	public boolean canBeLeashed(PlayerEntity player) {
		return false;
	}

	protected final boolean canSpeakToNPC() {
		if (!isAlive() || LOTRMod.PROXY.getSidedAttackTarget(this).isPresent()) {
			return false;
		}
		if (!level.isClientSide) {
			return speechCooldown >= getNPCSpeakToInterval();
		}
		return true;
	}

	public boolean canTrade(PlayerEntity player) {
		return isFriendlyAndAligned(player);
	}

	private boolean checkSpawningOnSurfaceBlock(IWorld iworld, SpawnReason reason) {
		if (reason != SpawnReason.NATURAL) {
			return true;
		}
		BlockPos pos = blockPosition();
		if (pos.getY() >= iworld.getSeaLevel()) {
			Biome biome = iworld.getBiome(pos);
			BlockState belowState = iworld.getBlockState(pos.below());
			return LOTRBiomes.getWrapperFor(biome, iworld).isSurfaceBlockForNPCSpawn(belowState);
		}
		return false;
	}

	@Override
	public boolean checkSpawnRules(IWorld iworld, SpawnReason reason) {
		if (!super.checkSpawnRules(iworld, reason)) {
			return false;
		}
		if (reason == SpawnReason.NATURAL) {
			if (spawnRequiresDarkness && !isValidLightLevelForDarkSpawn(iworld) || spawnRequiresSurfaceBlock && !checkSpawningOnSurfaceBlock(iworld, reason)) {
				return false;
			}
		}

		return true;
	}

	public final void clearHomePos() {
		restrictTo(BlockPos.ZERO, -1);
	}

	public void clearTalkingToEntity() {
		setTalkingToEntity((LivingEntity) null, 0);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(FACTION_OVERRIDE, Optional.empty());
	}

	@Override
	public void disableShield(boolean flag) {
		float f = 0.25F + EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
		if (flag) {
			f += 0.75F;
		}

		if (random.nextFloat() < f) {
			combatUpdater.temporarilyDisableShield();
			stopUsingItem();
			playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + level.random.nextFloat() * 0.4F);
		}

	}

	@Override
	public boolean doHurtTarget(Entity target) {
		if (!super.doHurtTarget(target)) {
			return false;
		}
		ItemStack weapon = getMainHandItem();
		if (!weapon.isEmpty() && target instanceof LivingEntity) {
			int weaponItemDamage = weapon.getDamageValue();
			weapon.getItem().hurtEnemy(weapon, (LivingEntity) target, this);
			weapon.setDamageValue(weaponItemDamage);
		}

		return true;
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean playerHit) {
		int equipmentCount = (int) Stream.of(EquipmentSlotType.values()).filter(slotx -> !getItemBySlot(slotx).isEmpty()).count();
		if (equipmentCount > 0) {
			EquipmentSlotType[] var5 = EquipmentSlotType.values();
			int var6 = var5.length;

			for (int var7 = 0; var7 < var6; ++var7) {
				EquipmentSlotType slot = var5[var7];
				ItemStack equipmentDrop = getItemBySlot(slot);
				if (!equipmentDrop.isEmpty()) {
					float dropChance = getEquipmentDropChance(slot);
					boolean dropUndamaged = dropChance > 1.0F;
					boolean dropGuaranteedVanilla = dropChance >= 1.0F;
					if ((dropGuaranteedVanilla || playerHit) && !EnchantmentHelper.hasVanishingCurse(equipmentDrop)) {
						boolean doDrop = true;
						if (!dropGuaranteedVanilla) {
							int chance = 20 * equipmentCount - looting * 4 * equipmentCount;
							chance = Math.max(chance, 1);
							if (random.nextInt(chance) != 0) {
								doDrop = false;
							}
						}

						if (doDrop) {
							if (!dropUndamaged && equipmentDrop.isDamageableItem()) {
								float dropDamageF = MathHelper.nextFloat(random, 0.5F, 0.75F);
								if (random.nextInt(12) == 0) {
									dropDamageF = MathHelper.nextFloat(random, 0.0F, 0.5F);
								}

								int dropDamage = MathHelper.floor(equipmentDrop.getMaxDamage() * dropDamageF);
								equipmentDrop.setDamageValue(dropDamage);
							}

							this.spawnAtLocation(equipmentDrop);
							setItemSlot(slot, ItemStack.EMPTY);
						}
					}
				}
			}
		}

	}

	@Override
	protected void dropFromLootTable(DamageSource source, boolean attackedRecently) {
		capturedLootForEnpouching = new ArrayList();
		super.dropFromLootTable(source, attackedRecently);
		List enpouchedLoot = sortCapturedLootIntoPouches(capturedLootForEnpouching);
		capturedLootForEnpouching = null;
		enpouchedLoot.forEach(item -> {
			npcDropItem((ItemStack) item, 0.0F, false, false);
		});
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		if (reason == SpawnReason.SPAWN_EGG) {
			setPersistenceRequired();
		}

		return spawnData;
	}

	protected final ITextComponent formatGenericNPCName(ITextComponent npcName, ITextComponent typeName) {
		return npcName != null && !npcName.getContents().equals(typeName.getContents()) ? new TranslationTextComponent("entityname.lotr.generic", npcName, typeName) : typeName;
	}

	protected ITextComponent formatNPCName(ITextComponent npcName, ITextComponent typeName) {
		return formatGenericNPCName(npcName, typeName);
	}

	public boolean generatesLocalAreaOfInfluence() {
		return true;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 200;
	}

	public final int getAttackCooldownTicks() {
		return Math.max(MathHelper.ceil(getAttackCooldownTicksF()), 1);
	}

	protected float getAttackCooldownTicksF() {
		return (float) (1.0D / getAttributeValue(Attributes.ATTACK_SPEED) * 20.0D);
	}

	public final int getAttackGoalIndex() {
		return attackGoalIndex;
	}

	public final AttackGoalsHolder getAttackGoalsHolder() {
		return attackGoalsHolder;
	}

	protected SoundEvent getAttackSound() {
		return null;
	}

	public float getDrunkenSpeechFactor() {
		return random.nextInt(3) == 0 ? MathHelper.nextFloat(random, 0.0F, 0.2F) : 0.0F;
	}

	protected final ITextComponent getEntityTypeName() {
		return super.getTypeName();
	}

	public final Faction getFaction() {
		Optional<FactionPointer> sus = getFactionOverride();
		if (sus != null && sus.isPresent()) {
			FactionPointer overridePointer = getFactionOverride().get();
			Optional<Faction> override = overridePointer.resolveFaction(level);
			if (override.isPresent()) {
				return override.get();
			}
			if (!loggedMissingFactionOverride) {
				LOTRLog.debug("NPC '%s' with a factionOverride '%s' could not resolve faction reference", getName().getString(), overridePointer.getName());
				loggedMissingFactionOverride = true;
			}
		}
		return NPCEntitySettingsManager.getEntityTypeFaction(this);
	}

	private Optional<FactionPointer> getFactionOverride() {
		return (Optional<FactionPointer>) entityData.get(FACTION_OVERRIDE);
	}

	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.MALE_OR_FEMALE;
	}

	protected SoundEvent getKillSound() {
		return null;
	}

	@Override
	public MovementController getMoveControl() {
		return isRidingOtherNPCMount() ? moveControl : super.getMoveControl();
	}

	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.NAMELESS_THING;
	}

	@Override
	public PathNavigator getNavigation() {
		return isRidingOtherNPCMount() ? navigation : super.getNavigation();
	}

	public NPCCombatUpdater getNPCCombatUpdater() {
		return combatUpdater;
	}

	public SoundEvent getNPCDrinkSound(ItemStack itemstack) {
		return getDrinkingSound(itemstack);
	}

	public NPCItemsInventory getNPCItemsInv() {
		return npcItemsInv;
	}

	protected final ITextComponent getNPCName() {
		return Optional.ofNullable(personalInfo.getName()).map(StringTextComponent::new).orElse((StringTextComponent) null);
	}

	protected int getNPCSpeakToInterval() {
		return 40;
	}

	public final NPCPersonalInfo getPersonalInfo() {
		return personalInfo;
	}

	public final double getSpawnCountWeight() {
		return isPersistenceRequired() ? 0.0D : 1.0D;
	}

	private Optional getSpeechbank() {
		Optional specialSpeech = SpecialSpeechbanks.getSpecialSpeechbank(random);
		if (specialSpeech.isPresent()) {
			return specialSpeech;
		}
		return speechbankOverride.isPresent() ? speechbankOverride : NPCEntitySettingsManager.getEntityTypeSettings(this).getSpeechbank();
	}

	public NPCTalkAnimations getTalkAnimations() {
		return talkAnimations;
	}

	public LivingEntity getTalkingToEntity() {
		return talkingTo;
	}

	public float getTalkingToInitialDistance() {
		return talkingToInitialDistance;
	}

	@Override
	protected final ITextComponent getTypeName() {
		ITextComponent typeName = getEntityTypeName();
		ITextComponent npcName = getNPCName();
		if (CalendarUtil.isAprilFools()) {
			npcName = new StringTextComponent("Gandalf");
		}

		return formatNPCName(npcName, typeName);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, IWorldReader reader) {
		if (spawnRequiresDarkness) {
			Biome biome = level.getBiome(pos);
			return NPCSpawnSettingsManager.getSpawnsForBiome(biome, getCommandSenderWorld()).allowsDarknessSpawnsInDaytime() ? 1.0F : 0.5F - reader.getBrightness(pos);
		}
		return 0.0F;
	}

	public final boolean hasHomePos() {
		return hasRestriction();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		boolean willBeBlockedByShield = amount > 0.0F && isDamageSourceBlocked(source);
		Vector3d preMotion = getDeltaMovement();
		boolean flag = super.hurt(source, amount);
		if (flag) {
			combatUpdater.onAttacked();
		}

		if (willBeBlockedByShield) {
			this.setDeltaMovement(preMotion);
		}

		return flag;
	}

	@Override
	protected void hurtCurrentlyUsedShield(float damage) {
		super.hurtCurrentlyUsedShield(damage);
		playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + random.nextFloat() * 0.4F);
	}

	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
	}

	@Override
	public boolean isBaby() {
		return personalInfo.isChild();
	}

	public boolean isCivilianNPC() {
		return !isTargetSeeker;
	}

	private boolean isDamageSourceBlocked(DamageSource p_184583_1_) {
		Entity entity = p_184583_1_.getDirectEntity();
		boolean flag = false;
		if (entity instanceof AbstractArrowEntity) {
			AbstractArrowEntity abstractarrowentity = (AbstractArrowEntity) entity;
			if (abstractarrowentity.getPierceLevel() > 0) {
				flag = true;
			}
		}

		if (!p_184583_1_.isBypassArmor() && isBlocking() && !flag) {
			Vector3d vector3d2 = p_184583_1_.getSourcePosition();
			if (vector3d2 != null) {
				Vector3d vector3d = getViewVector(1.0F);
				Vector3d vector3d1 = vector3d2.vectorTo(position()).normalize();
				vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
				if (vector3d1.dot(vector3d) < 0.0D) {
					return true;
				}
			}
		}

		return false;
	}

	public final boolean isDrunk() {
		return personalInfo.isDrunk();
	}

	public boolean isFleeing() {
		return goalSelector.getRunningGoals().anyMatch(prioritizedGoal -> (prioritizedGoal.getGoal() instanceof AvoidEntityGoal));
	}

	public boolean isFriendlyAndAligned(PlayerEntity player) {
		return isNotFighting(player) && LOTRLevelData.getSidedData(player).getAlignmentData().hasAlignment(getFaction(), AlignmentPredicates.POSITIVE_OR_ZERO);
	}

	public final boolean isNotFighting(PlayerEntity player) {
		return getTarget() != player && lastHurtByPlayer != player;
	}

	private boolean isRidingOtherNPCMount() {
		return isPassenger() && getVehicle() instanceof NPCEntity;
	}

	public boolean isStargazing() {
		return goalSelector.getRunningGoals().anyMatch(prioritizedGoal -> (prioritizedGoal.getGoal() instanceof StargazingGoal));
	}

	public boolean isTalking() {
		return talkingTo != null;
	}

	private boolean isValidLightLevelForDarkSpawn(IWorld iworld) {
		BlockPos pos = blockPosition();
		if (spawnRequiresDarkness) {
			Biome biome = iworld.getBiome(pos);
			if (NPCSpawnSettingsManager.getSpawnsForBiome(biome, iworld).allowsDarknessSpawnsInDaytime()) {
				return true;
			}
		}

		World thisWorld = getCommandSenderWorld();
		if (thisWorld instanceof ServerWorld) {
			return MonsterEntity.isDarkEnoughToSpawn((ServerWorld) thisWorld, pos, random);
		}
		LOTRLog.warn("Something is trying to check NPC spawning light levels on the client side! This should never happen!");
		return false;
	}

	public boolean isWatchingSunriseOrSunset() {
		return goalSelector.getRunningGoals().anyMatch(prioritizedGoal -> (prioritizedGoal.getGoal() instanceof WatchSunriseSunsetGoal));
	}

	@Override
	public void killed(ServerWorld sWorld, LivingEntity killedEntity) {
		super.killed(sWorld, killedEntity);
		if (getKillSound() != null) {
			playSound(getKillSound(), getSoundVolume(), getVoicePitch());
		}

	}

	protected final void markNPCSpoken() {
		speechCooldown = 0;
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		if (canSpeakToNPC()) {
			if (level.isClientSide) {
				return ActionResultType.SUCCESS;
			}

			if (sendNormalSpeechTo((ServerPlayerEntity) player)) {
				int talkingToTime = LOTRUtil.secondsToTicks(LOTRConfig.COMMON.getRandomNPCTalkToPlayerDuration(random));
				setTalkingToEntity(player, talkingToTime);
				player.awardStat(LOTRStats.TALK_TO_NPC);
				return ActionResultType.SUCCESS;
			}
		}

		return super.mobInteract(player, hand);
	}

	protected final void npcArrowAttack(LivingEntity target, float charge) {
		ItemStack heldItem = getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, item -> (item instanceof BowItem)));
		ItemStack ammoItem = getProjectile(heldItem);
		AbstractArrowEntity arrow = ProjectileHelper.getMobArrow(this, ammoItem, charge);
		if (heldItem.getItem() instanceof BowItem) {
			arrow = ((BowItem) heldItem.getItem()).customArrow(arrow);
		}

		double dx = target.getX() - this.getX();
		double dy = target.getY(0.3333333333333333D) - arrow.getY();
		double dz = target.getZ() - this.getZ();
		double dxzSq = MathHelper.sqrt(dx * dx + dz * dz);
		arrow.shoot(dx, dy + dxzSq * 0.20000000298023224D, dz, 1.6F, (float) getAttributeValue((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get()));
		playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 0.8F));
		level.addFreshEntity(arrow);
	}

	protected final ItemEntity npcDropItem(ItemStack item, float offsetY, boolean captureIfEnpouching, boolean applyOwnership) {
		if (applyOwnership && item != null && !item.isEmpty() && item.getMaxStackSize() == 1) {
			ItemOwnership.addPreviousOwner(item, getName());
		}

		if (captureIfEnpouching && capturedLootForEnpouching != null && item != null) {
			capturedLootForEnpouching.add(item);
			return null;
		}
		return super.spawnAtLocation(item, offsetY);
	}

	protected void npcSetAttackTarget(LivingEntity target, boolean speak) {
		getTarget();
		super.setTarget(target);
		if (target != null && !target.getUUID().equals(prevAttackTargetUuid)) {
			prevAttackTargetUuid = target.getUUID();
			if (!level.isClientSide) {
				if (getAttackSound() != null) {
					playSound(getAttackSound(), getSoundVolume(), getVoicePitch());
				}

				if (target instanceof PlayerEntity && speak) {
					PlayerEntity player = (PlayerEntity) target;
					List nearbyNPCsAttackingSamePlayer = level.getEntities(this, getBoundingBox().inflate(16.0D), e -> {
						if (!(e instanceof NPCEntity)) {
							return false;
						}
						NPCEntity otherNPC = (NPCEntity) e;
						return otherNPC.isAlive() && otherNPC.getTarget() == player;
					});
					if (nearbyNPCsAttackingSamePlayer.size() <= 5) {
						sendNormalSpeechTo((ServerPlayerEntity) player);
					}
				}
			}
		}

	}

	public void onAttackModeChange(AttackMode newMode, boolean newRiding) {
	}

	public void onPlayerStartTrackingNPC(ServerPlayerEntity player) {
		personalInfo.sendData(player);
		npcItemsInv.sendIsEating(player);
		combatUpdater.sendCombatStance(player);
		talkAnimations.sendData(player);
	}

	private void pathBackToHome() {
		if (!level.isClientSide && hasHomePos() && !this.isWithinRestriction()) {
			BlockPos homePos = getRestrictCenter();
			int homeRange = (int) getRestrictRadius();
			double maxDist = homeRange + 128.0D;
			double distToHomeSq = this.distanceToSqr(Vector3d.atBottomCenterOf(homePos));
			if (distToHomeSq > maxDist * maxDist) {
				clearHomePos();
			} else if (getTarget() == null && getNavigation().isDone()) {
				clearHomePos();
				boolean goDirectlyHome = false;
				if (level.hasChunkAt(homePos)) {
				}

				double homeSpeed = 1.3D;
				if (goDirectlyHome) {
					getNavigation().moveTo(homePos.getX() + 0.5D, homePos.getY() + 0.5D, homePos.getZ() + 0.5D, homeSpeed);
				} else {
					Vector3d path = null;

					for (int l = 0; l < 16 && path == null; ++l) {
						path = RandomPositionGenerator.getPosTowards(this, 8, 7, Vector3d.atBottomCenterOf(homePos));
					}

					if (path != null) {
						getNavigation().moveTo(path.x, path.y, path.z, homeSpeed);
					}
				}

				restrictTo(homePos, homeRange);
			}
		}

	}

	@Override
	public void performRangedAttack(LivingEntity target, float charge) {
		npcArrowAttack(target, charge);
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		personalInfo.read(nbt);
		npcItemsInv.readFromEntityNBT(nbt);
		combatUpdater.read(nbt);
		setFactionOverride(DataUtil.readOptionalFactionPointerFromNBT(nbt, "FactionOverride"));
		speechbankOverride = DataUtil.readOptionalFromNBT(nbt, "SpeechbankOverride", (hummel, hummel2) -> DataUtil.getResourceLocation((CompoundNBT) hummel, (String) hummel2));
		if (nbt.contains("NPCHomeRadius")) {
			int x = nbt.getInt("NPCHomeX");
			int y = nbt.getInt("NPCHomeY");
			int z = nbt.getInt("NPCHomeZ");
			int r = nbt.getInt("NPCHomeRadius");
			restrictTo(new BlockPos(x, y, z), r);
		}

	}

	private void recalculateReachDistance() {
		getAttribute(ForgeMod.REACH_DISTANCE.get()).setBaseValue(calculateDefaultNPCReach());
	}

	public void refreshCurrentAttackMode() {
		combatUpdater.refreshCurrentAttackMode();
	}

	@Override
	public void refreshDimensions() {
		super.refreshDimensions();
		recalculateReachDistance();
	}

	public boolean sendNormalSpeechTo(ServerPlayerEntity player) {
		Optional optSpeechbank = getSpeechbank();
		if (optSpeechbank.isPresent()) {
			sendSpeechTo(player, (ResourceLocation) optSpeechbank.get());
			return true;
		}
		return false;
	}

	public void sendSpeechTo(ServerPlayerEntity player, ResourceLocation speechbank) {
		NPCSpeechSender.sendMessageInContext(player, this, speechbank);
		markNPCSpoken();
	}

	private void setFactionOverride(Optional factionOverride) {
		entityData.set(FACTION_OVERRIDE, factionOverride);
	}

	public void setTalkingToEntity(LivingEntity e, int time) {
		talkingTo = e;
		talkingToTime = time;
		talkingToInitialDistance = talkingTo == null ? 0.0F : distanceTo(talkingTo);
	}

	@Override
	public final void setTarget(LivingEntity target) {
		boolean speak = target != null && getSensing().canSee(target) && random.nextInt(3) == 0;
		npcSetAttackTarget(target, speak);
	}

	private void setupNPCInfo() {
		if (!level.isClientSide) {
			personalInfo.setMale(getGenderProvider().isMale(random));
			personalInfo.setName(getNameGenerator().generateName(random, personalInfo.isMale()));
			personalInfo.assumeRandomPersonalityTraits(random);
		}

	}

	public boolean shouldRenderNPCChest() {
		return personalInfo.isFemale() && !isBaby() && getItemBySlot(EquipmentSlotType.CHEST).isEmpty();
	}

	public boolean shouldRenderNPCHair() {
		return true;
	}

	private List sortCapturedLootIntoPouches(List capturedLoot) {
		List pouches = new ArrayList();
		List otherLoot = new ArrayList();
		Iterator var4 = capturedLoot.iterator();

		while (var4.hasNext()) {
			ItemStack stack = (ItemStack) var4.next();
			if (stack.getItem() instanceof PouchItem) {
				pouches.add(stack);
			} else {
				otherLoot.add(stack);
			}
		}

		List notPouchedLoot = new ArrayList();
		Iterator var11 = otherLoot.iterator();

		while (var11.hasNext()) {
			ItemStack stack = (ItemStack) var11.next();
			Iterator var7 = pouches.iterator();

			while (var7.hasNext()) {
				ItemStack pouch = (ItemStack) var7.next();
				PouchItem.AddItemResult result = PouchItem.tryAddItemToPouch(pouch, stack, false);
				if (result == PouchItem.AddItemResult.FULLY_ADDED) {
					stack = ItemStack.EMPTY;
					break;
				}
			}

			if (!stack.isEmpty()) {
				notPouchedLoot.add(stack);
			}
		}

		pouches.addAll(notPouchedLoot);
		return pouches;
	}

	@Override
	public final ItemEntity spawnAtLocation(ItemStack item, float offsetY) {
		return npcDropItem(item, offsetY, true, true);
	}

	@Override
	public void tick() {
		super.tick();
		combatUpdater.updateCombat();
		personalInfo.tick();
		updateSwingTime();
		++speechCooldown;
		if (talkingToTime > 0) {
			--talkingToTime;
			if (talkingToTime <= 0) {
				clearTalkingToEntity();
			}
		}

		talkAnimations.updateAnimation();
		pathBackToHome();
	}

	public boolean useSmallArmsModel() {
		return false;
	}

	protected static MutableAttribute registerBaseNPCAttributes() {
		return MobEntity.createMobAttributes().add(Attributes.ATTACK_DAMAGE, 1.0D).add(Attributes.ATTACK_SPEED).add(ForgeMod.REACH_DISTANCE.get(), 0.0D).add((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get()).add((Attribute) LOTRAttributes.NPC_MOUNT_ATTACK_SPEED.get()).add((Attribute) LOTRAttributes.NPC_CONVERSATION_RANGE.get());
	}
}
