package lotr.common.item;

import lotr.common.block.VesselDrinkBlock;
import lotr.common.init.LOTRItemGroups;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EmptyVesselBlockItem extends LOTRBlockItem implements IEmptyVesselItem {
	private final VesselType vesselType;

	public EmptyVesselBlockItem(Block block, VesselType ves) {
		super(block, new Properties().tab(LOTRItemGroups.FOOD));
		vesselType = ves;
	}

	@Override
	public VesselType getVesselType() {
		return vesselType;
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
		return doEmptyVesselUseOnBlock(context);
	}

	@Override
	protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		if (world.getFluidState(pos).is(FluidTags.WATER) && !context.getPlayer().isSecondaryUseActive()) {
			return false;
		}
		if (super.placeBlock(context, state)) {
			VesselDrinkBlock.setVesselDrinkItem(world, pos, context.getItemInHand(), getVesselType());
			return true;
		}
		return false;
	}

	@Override
	public ActionResultType tryToPlaceVesselBlock(ItemUseContext context) {
		return super.useOn(context);
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		return doEmptyVesselRightClick(world, player, hand);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		return tryToPlaceVesselBlock(context);
	}
}
