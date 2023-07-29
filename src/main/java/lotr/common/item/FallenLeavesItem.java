package lotr.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemUseContext;

public class FallenLeavesItem extends WaterPlantBlockItem {
	public FallenLeavesItem(Block blockIn, Properties properties) {
		super(blockIn, properties);
	}

	@Override
	protected boolean canAttemptPlaceNormally(ItemUseContext context) {
		return context.getLevel().getFluidState(context.getClickedPos().relative(context.getClickedFace())).isEmpty();
	}
}
