package lotr.common.block;

import java.util.function.Supplier;

import lotr.common.event.CompostingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThatchStairsBlock extends LOTRStairsBlock {
	public ThatchStairsBlock(Supplier blockSup) {
		super(blockSup);
		CompostingHelper.prepareCompostable(this, 0.56100005F);
	}

	@Override
	public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
		ThatchBlock.doStandardHayFall(world, pos, entity, fallDistance);
	}
}
