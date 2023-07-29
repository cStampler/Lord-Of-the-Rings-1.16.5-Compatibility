package lotr.common.entity.animal;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import lotr.common.entity.ai.goal.CaracalRaidChestGoal;
import lotr.common.init.LOTREntities;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.*;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.network.datasync.*;
import net.minecraft.particles.*;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.*;

public class CaracalEntity extends CatEntity {
	private static final Ingredient BREEDING_ITEMS;
	public static final Predicate WANTS_TO_EAT;
	private static final DataParameter IS_RAIDING_CHEST;
	private static final DataParameter ARE_EARS_ALERT;
	private static final DataParameter IS_FLOPPING;
	static {
		BREEDING_ITEMS = Ingredient.of(Items.COD, Items.SALMON, Items.CHICKEN, Items.RABBIT);
		WANTS_TO_EAT = BREEDING_ITEMS.or(stack -> (stack.getItem().isEdible() && stack.getItem().getFoodProperties().isMeat()));
		IS_RAIDING_CHEST = EntityDataManager.defineId(CaracalEntity.class, DataSerializers.BOOLEAN);
		ARE_EARS_ALERT = EntityDataManager.defineId(CaracalEntity.class, DataSerializers.BOOLEAN);
		IS_FLOPPING = EntityDataManager.defineId(CaracalEntity.class, DataSerializers.BOOLEAN);
	}
	private TemptGoal temptGoal;
	private CaracalEntity.CaracalAvoidPlayerGoal avoidPlayerGoal;
	private int eatTick;
	private float yawWhenSat;
	private boolean wasSitting = false;
	private int earsAlertTimer = 0;

	private int floppingTimer = 0;

	public CaracalEntity(EntityType type, World worldIn) {
		super(type, worldIn);
		setCanPickUpLoot(true);
		lookControl = new CaracalEntity.CaracalLookController(this);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if (!level.isClientSide && isAlive() && isEffectiveAi()) {
			++eatTick;
			if (eatTick >= 560) {
				ItemStack heldItem = getItemInMouth();
				if (canEatItem(heldItem)) {
					if (canEatItemNow(heldItem)) {
						if (eatTick > 600) {
							ItemStack eatResult = heldItem.finishUsingItem(level, this);
							removeEffect(Effects.HUNGER);
							if (!eatResult.isEmpty()) {
								if (canEatItem(eatResult)) {
									setItemInMouth(eatResult);
								} else {
									this.spawnAtLocation(eatResult);
									setItemInMouth(ItemStack.EMPTY);
								}
							}

							eatTick = 0;
						} else if (eatTick > 560 && random.nextFloat() < 0.1F) {
							playSound(getEatingSound(heldItem), 1.0F, 1.0F);
							level.broadcastEntityEvent(this, (byte) 45);
						}
					}
				} else {
					this.spawnAtLocation(heldItem);
					setItemInMouth(ItemStack.EMPTY);
				}
			}
		}

	}

	public boolean areEarsAlert() {
		return (Boolean) entityData.get(ARE_EARS_ALERT);
	}

	public boolean canEatItem(ItemStack stack) {
		return WANTS_TO_EAT.test(stack);
	}

	private boolean canEatItemNow(ItemStack stack) {
		return canEatItem(stack) && getTarget() == null && onGround && !isSleeping();
	}

	@Override
	public boolean canHoldItem(ItemStack stack) {
		ItemStack currentHeldItem = getItemInMouth();
		return canEatItem(stack) && (currentHeldItem.isEmpty() || eatTick > 0 && !canEatItem(currentHeldItem));
	}

	@Override
	public boolean canTakeItem(ItemStack stack) {
		EquipmentSlotType slotType = MobEntity.getEquipmentSlotForItem(stack);
		if (!getItemBySlot(slotType).isEmpty()) {
			return false;
		}
		return slotType == EquipmentSlotType.MAINHAND && super.canTakeItem(stack);
	}

	public CaracalEntity createChild(ServerWorld world, AgeableEntity mate) {
		CaracalEntity caracal = (CaracalEntity) ((EntityType) LOTREntities.CARACAL.get()).create(world);
		CatEntity superChild = super.getBreedOffspring(world, mate);
		caracal.setOwnerUUID(superChild.getOwnerUUID());
		caracal.setTame(superChild.isTame());
		caracal.setCollarColor(superChild.getCollarColor());
		return caracal;
	}

	@Override
	public void customServerAiStep() {
		super.customServerAiStep();
		if (getMoveControl().hasWanted()) {
			double speed = getMoveControl().getSpeedModifier();
			if (speed <= 0.6D) {
				setPose(Pose.CROUCHING);
				setSprinting(false);
			} else if (speed >= 1.33D) {
				setPose(Pose.STANDING);
				setSprinting(true);
			} else {
				setPose(Pose.STANDING);
				setSprinting(false);
			}
		} else {
			setPose(Pose.STANDING);
			setSprinting(false);
		}

		if (isRaidingChest()) {
			setPose(Pose.CROUCHING);
			setSprinting(true);
		}

	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(IS_RAIDING_CHEST, false);
		entityData.define(ARE_EARS_ALERT, false);
		entityData.define(IS_FLOPPING, false);
	}

	@Override
	protected void dropAllDeathLoot(DamageSource source) {
		ItemStack heldItem = getItemInMouth();
		if (!heldItem.isEmpty()) {
			this.spawnAtLocation(heldItem);
			setItemInMouth(ItemStack.EMPTY);
		}

		super.dropAllDeathLoot(source);
	}

	private void dropItemBack(ItemStack stack) {
		ItemEntity itementity = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), stack);
		level.addFreshEntity(itementity);
	}

	@Override
	public ItemStack eat(World world, ItemStack stack) {
		int healAmount = stack.isEdible() ? stack.getItem().getFoodProperties().getNutrition() : 0;
		ItemStack result = super.eat(world, stack);
		if (!world.isClientSide && healAmount > 0) {
			heal(healAmount);
		}

		return result;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 200;
	}

	@Override
	public SoundEvent getEatingSound(ItemStack stack) {
		return SoundEvents.CAT_EAT;
	}

	public ItemStack getItemInMouth() {
		return getItemBySlot(EquipmentSlotType.MAINHAND);
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntitySize size) {
		return size.height * 0.85F;
	}

	@Override
	protected float getVoicePitch() {
		return super.getVoicePitch() * 0.65F;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte id) {
		if (id == 45) {
			ItemStack heldItem = getItemInMouth();
			if (!heldItem.isEmpty()) {
				for (int i = 0; i < 8; ++i) {
					Vector3d crumbMotion = new Vector3d((random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D).xRot((float) Math.toRadians(-xRot)).yRot((float) Math.toRadians(-yRot));
					level.addParticle(new ItemParticleData(ParticleTypes.ITEM, heldItem), this.getX() + getLookAngle().x / 2.0D, this.getY(), this.getZ() + getLookAngle().z / 2.0D, crumbMotion.x, crumbMotion.y + 0.05D, crumbMotion.z);
				}
			}
		} else {
			super.handleEntityEvent(id);
		}

	}

	public boolean hasItemInMouth() {
		return !getItemBySlot(EquipmentSlotType.MAINHAND).isEmpty();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		boolean flag = super.hurt(source, amount);
		if (flag && isInSittingPose()) {
			setInSittingPose(false);
		}

		return flag;
	}

	public boolean isFloppa() {
		return isTame() && hasCustomName() && nameContainsAny(getCustomName().getString(), "floppa", "gregory", "gosha");
	}

	public boolean isFlopping() {
		return (Boolean) entityData.get(IS_FLOPPING);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return BREEDING_ITEMS.test(stack);
	}

	public boolean isRaidingChest() {
		return (Boolean) entityData.get(IS_RAIDING_CHEST);
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		if (!isTame() || !isOrderedToSit() || !player.isShiftKeyDown() || !player.getItemInHand(hand).isEmpty()) {
			return super.mobInteract(player, hand);
		}
		if (!level.isClientSide) {
			playSound(SoundEvents.CAT_PURR, 0.6F + 0.4F * (random.nextFloat() - random.nextFloat()), 1.0F);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	protected void pickUpItem(ItemEntity itemEntity) {
		ItemStack stack = itemEntity.getItem();
		if (canHoldItem(stack)) {
			int count = stack.getCount();
			if (count > 1) {
				dropItemBack(stack.split(count - 1));
			}

			spitOutCurrentItemInMouth();
			onItemPickup(itemEntity);
			setItemInMouth(stack.split(1));
			handDropChances[EquipmentSlotType.MAINHAND.getIndex()] = 2.0F;
			take(itemEntity, stack.getCount());
			itemEntity.remove();
			eatTick = 0;
		}

	}

	@Override
	protected void reassessTameGoals() {
		if (avoidPlayerGoal == null) {
			avoidPlayerGoal = new CaracalEntity.CaracalAvoidPlayerGoal(this, PlayerEntity.class, 16.0F, 0.8D, 1.33D);
		}

		goalSelector.removeGoal(avoidPlayerGoal);
		if (!isTame()) {
			goalSelector.addGoal(5, avoidPlayerGoal);
		}

	}

	@Override
	protected void registerGoals() {
		temptGoal = new CaracalEntity.CaracalTemptGoal(this, 0.6D, BREEDING_ITEMS, true);
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new SitGoal(this));
		goalSelector.addGoal(3, new CaracalEntity.CaracalMorningGiftGoal(this));
		goalSelector.addGoal(4, temptGoal);
		goalSelector.addGoal(6, new CatLieOnBedGoal(this, 1.1D, 8));
		goalSelector.addGoal(7, new CaracalRaidChestGoal(this, 1.2D));
		goalSelector.addGoal(8, new FollowOwnerGoal(this, 1.0D, 10.0F, 5.0F, false));
		goalSelector.addGoal(9, new CatSitOnBlockGoal(this, 0.8D));
		goalSelector.addGoal(10, new LeapAtTargetGoal(this, 0.3F));
		goalSelector.addGoal(11, new OcelotAttackGoal(this));
		goalSelector.addGoal(12, new BreedGoal(this, 0.8D));
		goalSelector.addGoal(13, new WaterAvoidingRandomWalkingGoal(this, 0.8D, 1.0E-5F));
		goalSelector.addGoal(13, new CaracalEntity.CaracalFindItemsGoal(this, 1.2D));
		goalSelector.addGoal(14, new LookAtGoal(this, PlayerEntity.class, 10.0F));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, RabbitEntity.class, 10, false, false, (Predicate) null));
		targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, ChickenEntity.class, 10, false, false, (Predicate) null));
	}

	private void setAreEarsAlert(boolean flag) {
		entityData.set(ARE_EARS_ALERT, flag);
	}

	private void setIsFlopping(boolean flag) {
		entityData.set(IS_FLOPPING, flag);
	}

	public void setIsRaidingChest(boolean flag) {
		entityData.set(IS_RAIDING_CHEST, flag);
	}

	public void setItemInMouth(ItemStack stack) {
		setItemSlot(EquipmentSlotType.MAINHAND, stack);
	}

	private void spitOutCurrentItemInMouth() {
		ItemStack stack = getItemInMouth();
		if (!stack.isEmpty() && !level.isClientSide) {
			ItemEntity drop = new ItemEntity(level, this.getX() + getLookAngle().x, this.getY() + 1.0D, this.getZ() + getLookAngle().z, stack);
			drop.setPickUpDelay(40);
			drop.setThrower(getUUID());
			playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
			level.addFreshEntity(drop);
		}

	}

	@Override
	public void tick() {
		super.tick();
		if (isInSittingPose()) {
			if (!wasSitting) {
				yawWhenSat = yRot;
			}

			yBodyRot = yRot = yawWhenSat;
		}

		wasSitting = isInSittingPose();
		if (temptGoal != null && temptGoal.isRunning() && !isTame() && tickCount % 100 == 0) {
			playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0F, 1.0F);
		}

		if (!level.isClientSide) {
			if (earsAlertTimer < 0) {
				++earsAlertTimer;
			} else if (areEarsAlert()) {
				--earsAlertTimer;
				if (earsAlertTimer <= 0) {
					setAreEarsAlert(false);
					earsAlertTimer = -LOTRUtil.secondsToTicks(10 + random.nextInt(15));
				}
			}

			if (floppingTimer < 0) {
				++floppingTimer;
			} else if (!isFlopping()) {
				if (random.nextInt(600) == 0) {
					setIsFlopping(true);
					int maxFlopSeconds = isFloppa() ? 10 : 6;
					floppingTimer = LOTRUtil.secondsToTicks(MathHelper.nextInt(random, 1, maxFlopSeconds));
				}
			} else {
				--floppingTimer;
				if (floppingTimer <= 0) {
					setIsFlopping(false);
					floppingTimer = -LOTRUtil.secondsToTicks(30 + random.nextInt(200));
					if (isFloppa()) {
						floppingTimer /= 2;
					}
				}
			}
		}

	}

	private static boolean nameContainsAny(String name, String... matches) {
		Stream var10000 = Stream.of(matches).map(String::toLowerCase);
		String var10001 = name.toLowerCase();
		var10001.getClass();
		return var10000.anyMatch(hummel -> var10001.contains((CharSequence) hummel));
	}

	public static MutableAttribute regAttrs() {
		return CatEntity.createAttributes().add(Attributes.MAX_HEALTH, 15.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
	}

	static class CaracalAvoidPlayerGoal extends AvoidEntityGoal {
		static Predicate var10006 = EntityPredicates.NO_CREATIVE_OR_SPECTATOR;
		private final CaracalEntity caracal;

		public CaracalAvoidPlayerGoal(CaracalEntity caracal, Class entityClassToAvoid, float avoidDistance, double farSpeed, double nearSpeed) {
			super(caracal, entityClassToAvoid, avoidDistance, farSpeed, nearSpeed, var10006::test);
			this.caracal = caracal;
		}

		@Override
		public boolean canContinueToUse() {
			return !caracal.isTame() && super.canContinueToUse();
		}

		@Override
		public boolean canUse() {
			return !caracal.isTame() && super.canUse();
		}
	}

	static class CaracalFindItemsGoal extends Goal {
		private static final Predicate ITEM_SELECTOR = itemEntity -> (!((ItemEntity) itemEntity).hasPickUpDelay() && ((ItemEntity) itemEntity).isAlive() && CaracalEntity.WANTS_TO_EAT.test(((ItemEntity) itemEntity).getItem()));
		private final CaracalEntity caracal;
		private final double speed;

		public CaracalFindItemsGoal(CaracalEntity caracal, double speed) {
			this.caracal = caracal;
			this.speed = speed;
			setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canContinueToUse() {
			if (caracal.hasItemInMouth()) {
				return false;
			}
			if (!caracal.isOrderedToSit() && caracal.getTarget() == null && caracal.getLastHurtByMob() == null) {
				return findDroppedItem() != null;
			}
			return false;
		}

		@Override
		public boolean canUse() {
			return caracal.getRandom().nextInt(10) == 0 && canContinueToUse();
		}

		private ItemEntity findDroppedItem() {
			List items = caracal.level.getEntitiesOfClass(ItemEntity.class, caracal.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), ITEM_SELECTOR);
			return !items.isEmpty() ? (ItemEntity) items.get(0) : null;
		}

		@Override
		public void start() {
			ItemEntity droppedItem = findDroppedItem();
			if (droppedItem != null) {
				caracal.getNavigation().moveTo(droppedItem, speed);
			}

		}

		@Override
		public void tick() {
			if (!caracal.hasItemInMouth()) {
				ItemEntity droppedItem = findDroppedItem();
				if (droppedItem != null) {
					caracal.getNavigation().moveTo(droppedItem, speed);
				}
			}

		}
	}

	public static class CaracalLookController extends LookController {
		private final CaracalEntity theCaracal;

		public CaracalLookController(CaracalEntity caracal) {
			super(caracal);
			theCaracal = caracal;
		}

		@Override
		public void tick() {
			if (hasWanted && !theCaracal.areEarsAlert() && theCaracal.earsAlertTimer >= 0 && theCaracal.random.nextInt(60) == 0) {
				theCaracal.setAreEarsAlert(true);
				theCaracal.earsAlertTimer = LOTRUtil.secondsToTicks(1 + theCaracal.random.nextInt(2));
			}

			super.tick();
		}
	}

	static class CaracalMorningGiftGoal extends Goal {
		private final CaracalEntity caracal;
		private PlayerEntity theOwner;
		private BlockPos bedPos;
		private int tickCounter;

		public CaracalMorningGiftGoal(CaracalEntity caracal) {
			this.caracal = caracal;
		}

		private boolean anyOtherCaracalsGivingGifts() {
			return caracal.level.getEntitiesOfClass(CaracalEntity.class, new AxisAlignedBB(bedPos).inflate(2.0D)).stream().filter(otherCaracal -> (otherCaracal != caracal)).anyMatch(otherCaracal -> (otherCaracal.isLying() || otherCaracal.isRelaxStateOne()));
		}

		@Override
		public boolean canContinueToUse() {
			return caracal.isTame() && !caracal.isOrderedToSit() && theOwner != null && theOwner.isSleeping() && bedPos != null && !anyOtherCaracalsGivingGifts();
		}

		@Override
		public boolean canUse() {
			if (!caracal.isTame()) {
				return false;
			}
			if (caracal.isOrderedToSit()) {
			} else {
				LivingEntity owner = caracal.getOwner();
				if (owner instanceof PlayerEntity) {
					theOwner = (PlayerEntity) owner;
					if (!theOwner.isSleeping() || caracal.distanceToSqr(theOwner) > 100.0D) {
						return false;
					}

					BlockPos ownerPos = theOwner.blockPosition();
					BlockState state = caracal.level.getBlockState(ownerPos);
					if (state.getBlock().is(BlockTags.BEDS)) {
						bedPos = state.getOptionalValue(HorizontalBlock.FACING).map(dir -> ownerPos.relative(dir.getOpposite())).orElseGet(() -> new BlockPos(ownerPos));
						return !anyOtherCaracalsGivingGifts();
					}
				}
			}
			return false;
		}

		private void spawnGiftItem() {
			Random rand = caracal.getRandom();
			Mutable movingPos = new Mutable();
			movingPos.set(caracal.blockPosition());
			caracal.randomTeleport(movingPos.getX() + MathHelper.nextInt(rand, -5, 5), movingPos.getY() + MathHelper.nextInt(rand, -2, 2), movingPos.getZ() + MathHelper.nextInt(rand, -5, 5), false);
			movingPos.set(caracal.blockPosition());
			LootTable lootTable = caracal.level.getServer().getLootTables().get(LootTables.CAT_MORNING_GIFT);
			Builder builder = new Builder((ServerWorld) caracal.level).withParameter(LootParameters.ORIGIN, caracal.position()).withParameter(LootParameters.THIS_ENTITY, caracal).withRandom(rand);
			float yawRadians = (float) Math.toRadians(caracal.yBodyRot);
			lootTable.getRandomItems(builder.create(LootParameterSets.GIFT)).forEach(lootStack -> {
				caracal.level.addFreshEntity(new ItemEntity(caracal.level, movingPos.getX() - MathHelper.sin(yawRadians), movingPos.getY(), movingPos.getZ() + MathHelper.cos(yawRadians), lootStack));
			});
		}

		@Override
		public void start() {
			if (bedPos != null) {
				caracal.setInSittingPose(false);
				caracal.getNavigation().moveTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 1.100000023841858D);
			}

		}

		@Override
		public void stop() {
			caracal.setLying(false);
			float skyAngle = caracal.level.getTimeOfDay(1.0F);
			if (theOwner.getSleepTimer() >= 100 && skyAngle > 0.77F && skyAngle < 0.8F && caracal.level.getRandom().nextFloat() < 0.7F) {
				spawnGiftItem();
			}

			tickCounter = 0;
			caracal.setRelaxStateOne(false);
			caracal.getNavigation().stop();
		}

		@Override
		public void tick() {
			if (theOwner != null && bedPos != null) {
				caracal.setInSittingPose(false);
				caracal.getNavigation().moveTo(bedPos.getX(), bedPos.getY(), bedPos.getZ(), 1.100000023841858D);
				if (caracal.distanceToSqr(theOwner) < 2.5D) {
					++tickCounter;
					if (tickCounter > 16) {
						caracal.setLying(true);
						caracal.setRelaxStateOne(false);
					} else {
						caracal.lookAt(theOwner, 45.0F, 45.0F);
						caracal.setRelaxStateOne(true);
					}
				} else {
					caracal.setLying(false);
				}
			}

		}
	}

	static class CaracalTemptGoal extends TemptGoal {
		@Nullable
		private PlayerEntity temptingPlayer;
		private final CaracalEntity caracal;

		public CaracalTemptGoal(CaracalEntity caracal, double speed, Ingredient temptItems, boolean scaredByPlayerMovement) {
			super(caracal, speed, temptItems, scaredByPlayerMovement);
			this.caracal = caracal;
		}

		@Override
		protected boolean canScare() {
			return temptingPlayer != null && temptingPlayer.equals(player) ? false : super.canScare();
		}

		@Override
		public boolean canUse() {
			return super.canUse() && !caracal.isTame();
		}

		@Override
		public void tick() {
			super.tick();
			if (temptingPlayer == null && caracal.getRandom().nextInt(600) == 0) {
				temptingPlayer = player;
			} else if (caracal.getRandom().nextInt(500) == 0) {
				temptingPlayer = null;
			}

		}
	}
}
