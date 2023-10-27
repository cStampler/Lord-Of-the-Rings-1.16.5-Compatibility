package lotr.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

public class SnowBrickBlock extends Block {
	public SnowBrickBlock() {
		this(Properties.of(LOTRBlockMaterial.SNOW_BRICK).requiresCorrectToolForDrops().strength(0.4F).sound(SoundType.SNOW).harvestTool(ToolType.SHOVEL));
	}

	public SnowBrickBlock(Properties properties) {
		super(properties);
	}
}
