package lotr.common.block;

import net.minecraft.block.PaneBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class LOTRBarsBlock extends PaneBlock {
	public LOTRBarsBlock() {
		this(Properties.of(Material.METAL, MaterialColor.NONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion());
	}

	public LOTRBarsBlock(Properties properties) {
		super(properties);
	}
}
