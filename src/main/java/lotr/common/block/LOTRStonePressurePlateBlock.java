package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.PressurePlateBlock;

public class LOTRStonePressurePlateBlock extends PressurePlateBlock {
	public LOTRStonePressurePlateBlock(Supplier stone) {
		super(Sensitivity.MOBS, Properties.copy((AbstractBlock) stone.get()).noCollission().strength(0.5F));
	}
}
