package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;

public abstract class CloverBlock extends BushBlock {
	private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 7.0D, 13.0D);

	public CloverBlock() {
		this(Properties.of(Material.REPLACEABLE_PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
	}

	public CloverBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 0.3F);
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XYZ;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Vector3d offset = state.getOffset(world, pos);
		return SHAPE.move(offset.x, offset.y, offset.z);
	}
}
