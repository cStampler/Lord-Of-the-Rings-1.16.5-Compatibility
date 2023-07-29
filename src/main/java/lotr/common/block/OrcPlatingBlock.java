package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.*;

public class OrcPlatingBlock extends Block {
	public OrcPlatingBlock() {
		this(Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.NETHER_BRICKS));
	}

	public OrcPlatingBlock(Properties properties) {
		super(properties);
	}
}
