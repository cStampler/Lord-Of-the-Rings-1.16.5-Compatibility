package lotr.common.item;

import java.util.Arrays;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

public class VesselMiruvorItem extends VesselDrinkItem {
	public VesselMiruvorItem(int food, float sat, EffectInstance... effs) {
		super(0.0F, food, sat, true, 0.0F, Arrays.asList(effs));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, world, entity);
		if (!LembasItem.canConsumeElvishFoodSafely(entity)) {
			LembasItem.damageEntityHostileToElvishFood(entity);
		}

		return result;
	}

	@Override
	protected float getBenefitEffectivenessFor(LivingEntity entity) {
		return !LembasItem.canConsumeElvishFoodSafely(entity) ? 0.0F : super.getBenefitEffectivenessFor(entity);
	}
}
