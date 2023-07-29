package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;

public class LOTRGlassPaneBlock extends PaneBlock {
	public LOTRGlassPaneBlock() {
		this(Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion());
	}

	public LOTRGlassPaneBlock(Properties properties) {
		super(properties);
	}
}
