package lotr.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.WoodButtonBlock;
import net.minecraft.block.material.Material;

public class LOTRStoneButtonBlock extends WoodButtonBlock {
	public LOTRStoneButtonBlock() {
		super(Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.STONE));
	}
}
