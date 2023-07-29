package lotr.common.block;

import java.util.function.Supplier;

public class LOTRStrippedWoodBlock extends LOTRWoodBlock {
	public LOTRStrippedWoodBlock(Supplier strippedLogBlock) {
		super(((LOTRLogBlock) strippedLogBlock.get()).woodColor);
	}
}
