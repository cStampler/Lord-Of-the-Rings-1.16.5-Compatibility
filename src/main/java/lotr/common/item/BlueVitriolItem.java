package lotr.common.item;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class BlueVitriolItem extends Item {
	public BlueVitriolItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		World world = entity.level;
		if (!world.isClientSide && entity.isOnFire()) {
			BlockPos pos = entity.blockPosition();
			BlockPos belowPos = pos.below();
			BlockState state = world.getBlockState(pos);
			BlockState belowState = world.getBlockState(belowPos);
			if (state.getBlock() == Blocks.FIRE && belowState.getBlock() == LOTRBlocks.HEARTH_BLOCK.get()) {
				world.setBlockAndUpdate(belowPos, ((Block) LOTRBlocks.SOUL_FIRE_HEARTH_BLOCK.get()).defaultBlockState());
				world.setBlockAndUpdate(pos, Blocks.SOUL_FIRE.defaultBlockState());
				world.playSound((PlayerEntity) null, pos, SoundEvents.BLAZE_SHOOT, SoundCategory.BLOCKS, 0.5F, MathHelper.nextFloat(world.random, 0.75F, 0.95F));
				entity.remove();
			}
		}

		return false;
	}
}
