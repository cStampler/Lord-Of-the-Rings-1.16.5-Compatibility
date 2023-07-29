package lotr.common.item;

import java.util.Arrays;

import lotr.common.data.LOTRLevelData;
import lotr.common.fac.FactionPointers;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.*;
import net.minecraft.world.World;

public class VesselMorgulDraughtItem extends VesselDrinkItem {
	public VesselMorgulDraughtItem(int food, float sat, float dmg, EffectInstance... effs) {
		super(0.0F, food, sat, true, dmg, Arrays.asList(effs));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, world, entity);
		if (!shouldApplyPotionEffects(stack, entity) && !world.isClientSide) {
			entity.addEffect(new EffectInstance(Effects.POISON, LOTRUtil.secondsToTicks(5)));
		}

		return result;
	}

	@Override
	protected boolean shouldApplyPotionEffects(ItemStack stack, LivingEntity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			if (LOTRLevelData.getSidedData(player).getAlignmentData().getAlignment(FactionPointers.MORDOR) <= 0.0F) {
				return false;
			}
		}

		return super.shouldApplyPotionEffects(stack, entity);
	}
}
