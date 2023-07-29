package lotr.common.item;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.world.World;

public abstract class WaterPlantBlockItem extends LOTRBlockItem {
	public WaterPlantBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	protected boolean canAttemptPlaceNormally(ItemUseContext context) {
		return false;
	}

	protected boolean canPlaceOnIce() {
		return false;
	}

	protected final void playNormalPlaceSound(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		SoundType sound = state.getSoundType(world, pos, player);
		world.playSound(player, pos, this.getPlaceSound(state, world, pos, player), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
	}

	protected void playPlaceSound(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		playNormalPlaceSound(world, pos, state, player);
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		BlockRayTraceResult blockTarget = getPlayerPOVHitResult(world, player, FluidMode.SOURCE_ONLY);
		BlockRayTraceResult blockTargetAbove = blockTarget.withPosition(blockTarget.getBlockPos().above());
		ActionResultType result = super.useOn(new ItemUseContext(player, hand, blockTargetAbove));
		return new ActionResult(result, player.getItemInHand(hand));
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		return canAttemptPlaceNormally(context) ? super.useOn(context) : ActionResultType.PASS;
	}
}
