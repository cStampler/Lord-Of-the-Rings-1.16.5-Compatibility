package lotr.common.init;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.*;

public class LOTRWaterLilyBlock extends LilyPadBlock {
	public LOTRWaterLilyBlock() {
		this(Properties.of(Material.PLANT).strength(0.0F).sound(SoundType.LILY_PAD).noOcclusion());
	}

	public LOTRWaterLilyBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 0.65F);
	}

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return ((IPlantable) Blocks.LILY_PAD).getPlantType(world, pos);
	}
}
