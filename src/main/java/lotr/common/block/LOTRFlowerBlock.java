package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.potion.Effect;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRFlowerBlock extends FlowerBlock implements IForgeBlockState {
	private static final VoxelShape WIDE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D);
	private static final VoxelShape WIDE_FLAT_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 7.0D, 13.0D);
	protected VoxelShape flowerShape;

	public LOTRFlowerBlock(Effect effect, int effectDuration) {
		this(effect, effectDuration, createDefaultFlowerProperties());
	}

	public LOTRFlowerBlock(Effect effect, int effectDuration, Properties properties) {
		super(effect, effectDuration, properties);
		CompostingHelper.prepareCompostable(this, 0.65F);
		flowerShape = FlowerBlock.SHAPE;
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
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Vector3d offset = state.getOffset(world, pos);
		return flowerShape.move(offset.x, offset.y, offset.z);
	}

	public LOTRFlowerBlock setWide() {
		flowerShape = WIDE_SHAPE;
		return this;
	}

	public LOTRFlowerBlock setWideFlat() {
		flowerShape = WIDE_FLAT_SHAPE;
		return this;
	}

	protected static Properties createDefaultFlowerProperties() {
		return Properties.of(Material.PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS);
	}
}
