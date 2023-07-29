package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

public class BronzeLanternBlock extends NonWaterloggableLanternBlock {
	private static final VoxelShape bronzeGroundShape = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
	private static final VoxelShape bronzeHangingShape = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
	private static final VoxelShape bronzeGroundCollisionShape = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 2.0D, 11.0D);
	private static final VoxelShape bronzeHangingCollisionShape = Block.box(5.0D, 1.0D, 5.0D, 11.0D, 3.0D, 11.0D);

	public BronzeLanternBlock() {
		super(Properties.of(Material.DECORATION).requiresCorrectToolForDrops().harvestTool(ToolType.PICKAXE).strength(3.5F).sound(SoundType.LANTERN).lightLevel(LOTRBlocks.constantLight(14)).noOcclusion());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return state.getValue(HANGING) ? bronzeHangingCollisionShape : bronzeGroundCollisionShape;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return state.getValue(HANGING) ? bronzeHangingShape : bronzeGroundShape;
	}
}
