package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;

public class LOTRStandingSignBlock extends StandingSignBlock {
	public final WoodType signType;

	public LOTRStandingSignBlock(Supplier planks, WoodType type) {
		super(Properties.of(Material.WOOD, ((LOTRPlanksBlock) planks.get()).planksColor).noCollission().strength(1.0F).sound(SoundType.WOOD), type);
		signType = type;
		SignSetupHelper.add(this);
	}
}
