package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;

public class LOTRLanternBlock extends LanternBlock {
	public LOTRLanternBlock() {
		this(15);
	}

	public LOTRLanternBlock(int light) {
		this(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.LANTERN).lightLevel(LOTRBlocks.constantLight(light)).noOcclusion());
	}

	public LOTRLanternBlock(Properties properties) {
		super(properties);
	}
}
