package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRTallFlowerBlock extends TallFlowerBlock implements IForgeBlockState {
	public LOTRTallFlowerBlock() {
		this(Properties.of(Material.REPLACEABLE_PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
	}

	public LOTRTallFlowerBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 0.65F);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 100;
	}
}
