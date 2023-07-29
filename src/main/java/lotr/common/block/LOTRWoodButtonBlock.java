package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;

public class LOTRWoodButtonBlock extends WoodButtonBlock {
	public LOTRWoodButtonBlock() {
		super(Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD));
	}
}
