package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.SoundType;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.material.Material;

public class LOTRTrapdoorBlock extends TrapDoorBlock {
	public LOTRTrapdoorBlock(Supplier planks) {
		super(Properties.of(Material.WOOD, ((LOTRPlanksBlock) planks.get()).planksColor).strength(3.0F).sound(SoundType.WOOD).noOcclusion());
	}
}
