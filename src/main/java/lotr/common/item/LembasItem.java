package lotr.common.item;

import lotr.common.fac.FoodAlignmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class LembasItem extends Item {
	public LembasItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		if (!canConsumeElvishFoodSafely(entity)) {
			stack = FoodAlignmentHelper.onFoodEatenWithoutRestore(stack, world, entity);
			damageEntityHostileToElvishFood(entity);
			return stack;
		}
		return super.finishUsingItem(stack, world, entity);
	}

	public static boolean canConsumeElvishFoodSafely(LivingEntity entity) {
		return !FoodAlignmentHelper.isPledgedOrEntityAlignedToAny(entity, FoodAlignmentHelper.EVIL_CREATURE_FACTION_TYPES);
	}

	public static void damageEntityHostileToElvishFood(LivingEntity entity) {
		if (!entity.level.isClientSide) {
			entity.hurt(DamageSource.MAGIC, 2.0F);
		}

	}
}
