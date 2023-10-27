package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.DoorBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class LOTRDoorBlock extends DoorBlock {
	public LOTRDoorBlock(Supplier planks) {
		super(Properties.of(Material.WOOD, ((LOTRPlanksBlock) planks.get()).planksColor).strength(3.0F).sound(SoundType.WOOD).noOcclusion());
	}
}
