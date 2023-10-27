package lotr.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class LOTRSandstoneBlock extends Block {
	public LOTRSandstoneBlock(MaterialColor color) {
		super(Properties.of(Material.STONE, color).requiresCorrectToolForDrops().strength(0.8F));
	}
}
