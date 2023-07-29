package lotr.common.item;

import com.google.common.collect.ImmutableMultimap.Builder;

import lotr.common.dispenser.DispenseSpear;
import lotr.common.entity.projectile.SpearEntity;
import lotr.common.init.LOTRMaterial;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class SpearItem extends LOTRSwordItem {
	public SpearItem(IItemTier tier) {
		super(tier, 2, -2.667F);
		DispenserBlock.registerBehavior(this, new DispenseSpear());
	}

	public SpearItem(LOTRMaterial material) {
		this(material.asTool());
	}

	private PickupStatus getPickupStatusForShooter(LivingEntity shooter) {
		if (shooter instanceof PlayerEntity) {
			return ((PlayerEntity) shooter).abilities.instabuild ? PickupStatus.CREATIVE_ONLY : PickupStatus.ALLOWED;
		}
		return PickupStatus.DISALLOWED;
	}

	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void releaseUsing(ItemStack stack, World world, LivingEntity shooter, int timeLeft) {
		int useTime = getUseDuration(stack) - timeLeft;
		float charge = BowItem.getPowerForTime(useTime);
		if (charge > 0.1D) {
			if (!world.isClientSide) {
				SpearEntity spear = new SpearEntity(world, shooter, stack);
				spear.shootFromRotation(shooter, shooter.xRot, shooter.yRot, 0.0F, charge * 3.0F, 1.0F);
				if (charge == 1.0F) {
					spear.setCritArrow(true);
				}

				applyStandardEnchantmentsFromBow(stack, spear);
				spear.pickup = getPickupStatusForShooter(shooter);
				world.addFreshEntity(spear);
			}

			world.playSound((PlayerEntity) null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + charge * 0.5F);
			if (shooter instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) shooter;
				if (!player.abilities.instabuild) {
					stack.shrink(1);
					if (stack.isEmpty()) {
						player.inventory.removeItem(stack);
					}
				}

				player.awardStat(Stats.ITEM_USED.get(this));
			}
		}

	}

	@Override
	protected void setupExtendedMeleeAttributes(Builder builder) {
		addReachModifier(builder, 1.0D);
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (canPlayerUseSpearAction(player, hand)) {
			player.startUsingItem(hand);
			return ActionResult.success(heldItem);
		}
		return ActionResult.pass(heldItem);
	}

	public static void applyStandardEnchantmentsFromBow(ItemStack stack, AbstractArrowEntity projectile) {
		int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
		if (power > 0) {
			projectile.setBaseDamage(projectile.getBaseDamage() + power * 0.5D + 0.5D);
		}

		int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
		if (punch > 0) {
			projectile.setKnockback(punch);
		}

		if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
			projectile.setSecondsOnFire(100);
		}

	}

	private static boolean canPlayerUseSpearAction(PlayerEntity player, Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			ItemStack offhandItem = player.getOffhandItem();
			if (offhandItem.getUseAnimation() == UseAction.BLOCK) {
				return player.isShiftKeyDown();
			}
		}

		return true;
	}
}
