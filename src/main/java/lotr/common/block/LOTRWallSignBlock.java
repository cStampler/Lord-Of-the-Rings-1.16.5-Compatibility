package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.WallSignBlock;

public class LOTRWallSignBlock extends WallSignBlock {
	public LOTRWallSignBlock(Supplier sign) {
		super(Properties.copy((AbstractBlock) sign.get()).dropsLike((Block) sign.get()), ((LOTRStandingSignBlock) sign.get()).signType);
		SignSetupHelper.add(this);
	}
}
