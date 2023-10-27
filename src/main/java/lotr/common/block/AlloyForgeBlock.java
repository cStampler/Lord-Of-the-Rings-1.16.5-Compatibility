package lotr.common.block;

import lotr.common.init.LOTRTileEntities;
import lotr.common.stat.LOTRStats;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public class AlloyForgeBlock extends AbstractAlloyForgeBlock {
	public AlloyForgeBlock(MaterialColor color) {
		super(color);
	}

	@Override
	protected ResourceLocation getForgeInteractionStat() {
		return LOTRStats.INTERACT_ALLOY_FORGE;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.ALLOY_FORGE.get()).create();
	}
}
