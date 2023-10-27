package lotr.common.init;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

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
