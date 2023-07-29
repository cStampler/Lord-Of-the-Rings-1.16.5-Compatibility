package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.*;

public class LOTRBarsBlock extends PaneBlock {
	public LOTRBarsBlock() {
		this(Properties.of(Material.METAL, MaterialColor.NONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL).noOcclusion());
	}

	public LOTRBarsBlock(Properties properties) {
		super(properties);
	}
}
