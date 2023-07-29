package lotr.common.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;

public class VesselMilkItem extends VesselDrinkItem {
	public VesselMilkItem() {
		super(0.0F, 0, 0.0F, false, 0.0F, ImmutableList.of());
	}

	@Override
	public boolean canBeginDrinking(PlayerEntity player, ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(stack, world, entity);
		if (!world.isClientSide) {
			ItemStack proxyCure = new ItemStack(Items.MILK_BUCKET);
			entity.curePotionEffects(proxyCure);
		}

		return result;
	}
}
