package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraftforge.common.ToolType;

public class LOTRDirtBlock extends Block {
	public LOTRDirtBlock(MaterialColor materialColor) {
		this(Properties.of(Material.DIRT, materialColor).strength(0.5F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL));
	}

	public LOTRDirtBlock(Properties properties) {
		super(properties);
	}
}
