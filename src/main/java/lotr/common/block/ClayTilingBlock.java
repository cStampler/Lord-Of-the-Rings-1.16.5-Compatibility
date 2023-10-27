package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeColor;

public class ClayTilingBlock extends Block {
	public ClayTilingBlock(DyeColor dyeColor) {
		this(dyeColor.getMaterialColor());
	}

	public ClayTilingBlock(MaterialColor materialColor) {
		this(Properties.of(Material.STONE, materialColor).requiresCorrectToolForDrops().strength(1.25F, 4.2F).sound(LOTRBlocks.SOUND_CERAMIC));
	}

	public ClayTilingBlock(Properties properties) {
		super(properties);
	}
}
