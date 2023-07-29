package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.trees.Tree;

public class LOTRSaplingBlock extends SaplingBlock {
	public LOTRSaplingBlock(Tree tree) {
		super(tree, Properties.of(Material.PLANT).noCollission().randomTicks().strength(0.0F).sound(SoundType.GRASS));
		CompostingHelper.prepareCompostable(this, 0.3F);
	}
}
