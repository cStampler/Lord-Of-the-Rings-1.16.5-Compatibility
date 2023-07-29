package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraftforge.common.ToolType;

public class MordorDirtPathBlock extends LOTRPathBlock {
	public MordorDirtPathBlock(MaterialColor color) {
		super(Properties.of(Material.DIRT, color).strength(0.5F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL));
	}

	@Override
	protected BlockState getUnpathedBlockState() {
		return ((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState();
	}
}
