package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class LOTRWoodPressurePlateBlock extends PressurePlateBlock {
	public LOTRWoodPressurePlateBlock(Supplier planks) {
		super(Sensitivity.EVERYTHING, Properties.of(Material.WOOD, ((LOTRPlanksBlock) planks.get()).planksColor).noCollission().strength(0.5F).sound(SoundType.WOOD));
	}
}
