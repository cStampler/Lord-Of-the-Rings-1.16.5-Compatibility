package lotr.common.block;

import lotr.common.init.LOTRTileEntities;
import lotr.common.stat.LOTRStats;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public class HobbitOvenBlock extends AbstractAlloyForgeBlock {
	public HobbitOvenBlock(MaterialColor color) {
		super(color);
	}

	@Override
	protected ResourceLocation getForgeInteractionStat() {
		return LOTRStats.INTERACT_HOBBIT_OVEN;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.HOBBIT_OVEN.get()).create();
	}
}
