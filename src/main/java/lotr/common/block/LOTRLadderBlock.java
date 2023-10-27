package lotr.common.block;

import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class LOTRLadderBlock extends LadderBlock {
	public LOTRLadderBlock() {
		this(Properties.of(Material.DECORATION).strength(0.4F).sound(SoundType.LADDER).noOcclusion());
	}

	public LOTRLadderBlock(Properties properties) {
		super(properties);
	}
}
