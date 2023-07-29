package lotr.common.block;

import java.util.Random;
import java.util.function.Supplier;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;

public class PipeweedCropBlock extends LOTRCropBlock {
	public PipeweedCropBlock(Properties properties, Supplier sup) {
		super(properties, sup);
	}

	public PipeweedCropBlock(Supplier sup) {
		super(sup);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (isMaxAge(state)) {
			((Block) LOTRBlocks.WILD_PIPEWEED.get()).animateTick(state, world, pos, rand);
		}

	}
}
