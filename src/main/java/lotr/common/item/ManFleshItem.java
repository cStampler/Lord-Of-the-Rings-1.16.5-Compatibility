package lotr.common.item;

import java.util.Random;

import lotr.common.fac.FoodAlignmentHelper;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.world.World;

public class ManFleshItem extends Item {
	public ManFleshItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		float safety = FoodAlignmentHelper.getHighestAlignmentProportion(entity, 250.0F, FoodAlignmentHelper.EVIL_CREATURE_FACTION_TYPES);
		Random rand = entity.getRandom();
		if (rand.nextFloat() < safety) {
			return super.finishUsingItem(stack, world, entity);
		}
		stack = FoodAlignmentHelper.onFoodEatenWithoutRestore(stack, world, entity);
		if (!world.isClientSide) {
			entity.addEffect(new EffectInstance(Effects.HUNGER, LOTRUtil.secondsToTicks(30)));
			entity.addEffect(new EffectInstance(Effects.POISON, LOTRUtil.secondsToTicks(5)));
		}

		return stack;
	}

	public static boolean isManFleshAligned(LivingEntity entity) {
		return FoodAlignmentHelper.hasAnyPositiveAlignment(entity, FoodAlignmentHelper.EVIL_CREATURE_FACTION_TYPES);
	}
}
