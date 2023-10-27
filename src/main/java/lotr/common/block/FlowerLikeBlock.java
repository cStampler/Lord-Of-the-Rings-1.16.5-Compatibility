package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class FlowerLikeBlock extends BushBlock implements IForgeBlockState {
	protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
	private VoxelShape thisShape;

	public FlowerLikeBlock() {
		this(Properties.of(Material.PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
	}

	public FlowerLikeBlock(Properties properties) {
		super(properties);
		thisShape = SHAPE;
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

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.XZ;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Vector3d offset = state.getOffset(world, pos);
		return thisShape.move(offset.x, offset.y, offset.z);
	}

	public FlowerLikeBlock setPlantShape(VoxelShape shape) {
		thisShape = shape;
		return this;
	}
}
