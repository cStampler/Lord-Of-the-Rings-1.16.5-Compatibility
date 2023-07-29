package lotr.common.item;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LOTRWaterLilyItem extends WaterPlantBlockItem {
	public LOTRWaterLilyItem(Block blockIn, Properties properties) {
		super(blockIn, properties);
	}

	@Override
	protected boolean canPlaceOnIce() {
		return true;
	}

	@Override
	protected void playPlaceSound(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		world.playSound(player, pos, SoundEvents.LILY_PAD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}
