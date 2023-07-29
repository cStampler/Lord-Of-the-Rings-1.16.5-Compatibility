package lotr.common.block;

import java.util.function.Supplier;

public class StrippedRottenLogBlock extends RottenLogBlock {
	public StrippedRottenLogBlock(Supplier logBlock) {
		super(((RottenLogBlock) logBlock.get()).woodColor, ((RottenLogBlock) logBlock.get()).woodColor);
	}
}
