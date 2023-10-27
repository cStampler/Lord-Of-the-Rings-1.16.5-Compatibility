package lotr.common.block;

import lotr.common.init.LOTRTileEntities;
import lotr.common.stat.LOTRStats;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public class OrcForgeBlock extends AbstractAlloyForgeBlock {
	public OrcForgeBlock(MaterialColor color) {
		super(color);
	}

	@Override
	protected ResourceLocation getForgeInteractionStat() {
		return LOTRStats.INTERACT_ORC_FORGE;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.ORC_FORGE.get()).create();
	}
}
