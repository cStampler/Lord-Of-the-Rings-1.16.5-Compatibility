package lotr.common.block;

import java.util.function.Supplier;

import lotr.common.event.CompostingHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThatchSlabBlock extends AxialSlabBlock {
	public ThatchSlabBlock(Supplier blockSup) {
		super(blockSup);
		CompostingHelper.prepareCompostable(this, 0.425F);
	}

	@Override
	public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
		ThatchBlock.doStandardHayFall(world, pos, entity, fallDistance);
	}
}
