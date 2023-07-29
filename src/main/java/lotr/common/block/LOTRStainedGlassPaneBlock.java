package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;

public class LOTRStainedGlassPaneBlock extends StainedGlassPaneBlock {
	public LOTRStainedGlassPaneBlock(DyeColor color) {
		this(color, Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
	}

	public LOTRStainedGlassPaneBlock(DyeColor color, Properties properties) {
		super(color, properties);
	}
}
