package lotr.common.block;

import java.util.function.Supplier;

public class LOTRStrippedLogBlock extends LOTRLogBlock {
	public LOTRStrippedLogBlock(Supplier logBlock) {
		super(((LOTRLogBlock) logBlock.get()).woodColor, ((LOTRLogBlock) logBlock.get()).woodColor);
	}
}
