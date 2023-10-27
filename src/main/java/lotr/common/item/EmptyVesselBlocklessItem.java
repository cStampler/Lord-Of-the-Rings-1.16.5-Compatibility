package lotr.common.item;

import lotr.common.init.LOTRItemGroups;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EmptyVesselBlocklessItem extends Item implements IEmptyVesselItem {
	private final VesselType vesselType;

	public EmptyVesselBlocklessItem(VesselType ves) {
		super(new Properties().tab(LOTRItemGroups.FOOD));
		vesselType = ves;
	}

	@Override
	public VesselType getVesselType() {
		return vesselType;
	}

	@Override
	public ActionResult use(World world, PlayerEntity player, Hand hand) {
		return doEmptyVesselRightClick(world, player, hand);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		return doEmptyVesselUseOnBlock(context);
	}
}
