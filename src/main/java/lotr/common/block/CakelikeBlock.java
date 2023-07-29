package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;

public class CakelikeBlock extends CakeBlock {
	public CakelikeBlock() {
		this(Properties.copy(Blocks.CAKE));
	}

	public CakelikeBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 1.0F);
	}
}
