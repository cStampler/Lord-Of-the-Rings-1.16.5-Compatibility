package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;

public class LOTRStoneButtonBlock extends WoodButtonBlock {
	public LOTRStoneButtonBlock() {
		super(Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.STONE));
	}
}
