package lotr.common.block;

import net.minecraft.block.*;
import net.minecraftforge.common.ToolType;

public class IceBrickBlock extends Block {
	public IceBrickBlock() {
		this(Properties.of(LOTRBlockMaterial.ICE_BRICK).requiresCorrectToolForDrops().friction(0.98F).strength(0.5F).sound(SoundType.GLASS).harvestTool(ToolType.PICKAXE));
	}

	public IceBrickBlock(Properties properties) {
		super(properties);
	}
}
