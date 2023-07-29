package lotr.common.block;

import java.util.function.Supplier;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.*;

public class LOTRStoneBlock extends Block {
	public LOTRStoneBlock(MaterialColor materialColor) {
		this(materialColor, 1.5F, 6.0F);
	}

	public LOTRStoneBlock(MaterialColor materialColor, float hard, float res) {
		this(Properties.of(Material.STONE, materialColor).requiresCorrectToolForDrops().strength(hard, res));
	}

	public LOTRStoneBlock(MaterialColor materialColor, int light) {
		this(Properties.of(Material.STONE, materialColor).requiresCorrectToolForDrops().strength(1.5F, 6.0F).lightLevel(LOTRBlocks.constantLight(light)));
	}

	public LOTRStoneBlock(Properties properties) {
		super(properties);
	}

	public LOTRStoneBlock(Supplier blockSup) {
		this(Properties.copy((AbstractBlock) blockSup.get()));
	}
}
