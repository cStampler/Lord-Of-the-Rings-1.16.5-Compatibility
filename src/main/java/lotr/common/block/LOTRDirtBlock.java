package lotr.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.common.ToolType;

public class LOTRDirtBlock extends Block {
	public LOTRDirtBlock(MaterialColor materialColor) {
		this(Properties.of(Material.DIRT, materialColor).strength(0.5F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL));
	}

	public LOTRDirtBlock(Properties properties) {
		super(properties);
	}
}
