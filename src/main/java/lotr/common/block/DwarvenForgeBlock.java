package lotr.common.block;

import lotr.common.init.LOTRTileEntities;
import lotr.common.stat.LOTRStats;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

public class DwarvenForgeBlock extends AbstractAlloyForgeBlock {
	public DwarvenForgeBlock(MaterialColor color) {
		super(color);
	}

	@Override
	protected ResourceLocation getForgeInteractionStat() {
		return LOTRStats.INTERACT_DWARVEN_FORGE;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.DWARVEN_FORGE.get()).create();
	}
}
