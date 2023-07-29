package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;

public class MordorGrassBlock extends MordorPlantBlock {
	private static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public MordorGrassBlock() {
		super(Properties.of(Material.REPLACEABLE_PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
		CompostingHelper.prepareCompostable(this, 0.3F);
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XYZ;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
}
