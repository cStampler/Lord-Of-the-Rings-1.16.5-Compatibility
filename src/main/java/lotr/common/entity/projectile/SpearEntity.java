package lotr.common.entity.projectile;

import java.util.*;

import lotr.common.init.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.*;
import net.minecraft.util.*;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

public class SpearEntity extends LOTRAbstractArrowEntity {
	private static final DataParameter SPEAR_ITEM;

	static {
		SPEAR_ITEM = EntityDataManager.defineId(SpearEntity.class, DataSerializers.ITEM_STACK);
	}

	public SpearEntity(EntityType type, World w) {
		super(type, w);
	}

	public SpearEntity(World w, double x, double y, double z, ItemStack stack) {
		super((EntityType) LOTREntities.SPEAR.get(), x, y, z, w);
		setSpearItem(stack.copy());
	}

	public SpearEntity(World w, LivingEntity thrower, ItemStack stack) {
		super((EntityType) LOTREntities.SPEAR.get(), thrower, w);
		setSpearItem(stack.copy());
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.put("SpearItem", getSpearItem().save(new CompoundNBT()));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(SPEAR_ITEM, ItemStack.EMPTY);
	}

	private void doPickUpAnimationAndRemove(PlayerEntity player) {
		player.take(this, 1);
		this.remove();
	}

	private float getBaseImpactDamage(Entity hitEntity) {
		double speed = getDeltaMovement().length();
		ItemStack spearItem = getSpearItem();
		double damageMultiplier = getSpearItemAttackDamage(spearItem);
		damageMultiplier += EnchantmentHelper.getDamageBonus(spearItem, hitEntity instanceof LivingEntity ? ((LivingEntity) hitEntity).getMobType() : CreatureAttribute.UNDEFINED);
		damageMultiplier *= 0.7D;
		return (float) (speed * damageMultiplier);
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.TRIDENT_HIT_GROUND;
	}

	@Override
	protected int getLifespanTicksInGround() {
		return pickup == PickupStatus.DISALLOWED ? 1200 : 6000;
	}

	@Override
	protected ItemStack getPickupItem() {
		return getSpearItem().copy();
	}

	public ItemStack getSpearItem() {
		return (ItemStack) entityData.get(SPEAR_ITEM);
	}

	private double getSpearItemAttackDamage(ItemStack stack) {
		Attribute attr = Attributes.ATTACK_DAMAGE;
		ModifiableAttributeInstance attrInst = new ModifiableAttributeInstance(attr, mai -> {
		});
		attrInst.setBaseValue(1.0D);
		Collection mainhandAttackModifiers = stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(attr);
		mainhandAttackModifiers.forEach(hummel -> attrInst.addTransientModifier((AttributeModifier) hummel));
		return attrInst.getValue();
	}

	@Override
	protected float getWaterInertia() {
		return 0.99F;
	}

	@Override
	protected void onHitEntity(EntityRayTraceResult result) {
		Entity hitEntity = result.getEntity();
		int damage = calculateImpactDamageIncludingCritical(getBaseImpactDamage(hitEntity));
		Entity shooter = getOwner();
		DamageSource damageSource = LOTRDamageSources.causeThrownSpearDamage(this, Optional.ofNullable(shooter).orElse(this));
		if (hitEntity.hurt(damageSource, damage)) {
			if (isOnFire()) {
				hitEntity.setSecondsOnFire(5);
			}

			if (hitEntity instanceof LivingEntity) {
				LivingEntity hitLivingEntity = (LivingEntity) hitEntity;
				if (shooter instanceof LivingEntity) {
					EnchantmentHelper.doPostHurtEffects(hitLivingEntity, shooter);
					EnchantmentHelper.doPostDamageEffects((LivingEntity) shooter, hitLivingEntity);
				}

				doPostHurtEffects(hitLivingEntity);
			}
		}

		this.setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
		playSound(SoundEvents.TRIDENT_HIT, 1.0F, 1.0F);
	}

	private void playBreakSoundAndRemove() {
		playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + level.random.nextFloat() * 0.4F);
		this.remove();
	}

	@Override
	public void playerTouch(PlayerEntity player) {
		if (!level.isClientSide && (inGround || isNoPhysics()) && shakeTime <= 0) {
			boolean canPickUp = pickup == PickupStatus.ALLOWED || pickup == PickupStatus.CREATIVE_ONLY && player.abilities.instabuild || isNoPhysics() && getOwner().getUUID() == player.getUUID();
			if (canPickUp) {
				if (pickup == PickupStatus.CREATIVE_ONLY) {
					doPickUpAnimationAndRemove(player);
				} else {
					ItemStack pickupStack = getPickupItem().copy();
					pickupStack.hurtAndBreak(1, player, p -> {
					});
					if (pickupStack.isEmpty()) {
						playBreakSoundAndRemove();
					} else if (player.inventory.add(pickupStack)) {
						doPickUpAnimationAndRemove(player);
					}
				}
			}
		}

	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		if (nbt.contains("SpearItem", 10)) {
			setSpearItem(ItemStack.of(nbt.getCompound("SpearItem")));
		}

	}

	private void setSpearItem(ItemStack stack) {
		entityData.set(SPEAR_ITEM, stack);
	}
}
