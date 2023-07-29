package lotr.common.item;

import java.util.Random;

import lotr.common.fac.FoodAlignmentHelper;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MaggotyBreadItem extends Item {
	public MaggotyBreadItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, world, entity);
		if (!world.isClientSide) {
			float alignProp = FoodAlignmentHelper.getHighestAlignmentProportion(entity, 250.0F, FoodAlignmentHelper.EVIL_CREATURE_FACTION_TYPES);
			float chance = (1.0F - alignProp) * 0.8F;
			Random rand = entity.getRandom();
			if (rand.nextFloat() < chance) {
				int dur = MathHelper.nextInt(rand, 20, 40);
				entity.addEffect(new EffectInstance(Effects.HUNGER, LOTRUtil.secondsToTicks(dur)));
			}
		}

		return result;
	}
}
