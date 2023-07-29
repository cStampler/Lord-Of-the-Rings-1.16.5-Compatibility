package lotr.common.block;

import lotr.common.event.CompostingHelper;
import lotr.common.fac.*;
import lotr.common.init.LOTRDamageSources;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;

public class MordorThornBlock extends MordorPlantBlock {
	private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public MordorThornBlock() {
		super(Properties.of(Material.REPLACEABLE_PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
		CompostingHelper.prepareCompostable(this, 0.3F);
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!FactionPointers.MORDOR.matches(EntityFactionHelper.getFaction(entity))) {
			entity.hurt(LOTRDamageSources.PLANT, 2.0F);
		}

	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
}
