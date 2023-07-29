package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class StrippableLogStairsBlock extends LogStairsBlock {
	private final Supplier strippedStairsBlock;

	public StrippableLogStairsBlock(Block block, Supplier strippedBlock) {
		super(block);
		strippedStairsBlock = strippedBlock;
	}

	public StrippableLogStairsBlock(Supplier block, Supplier strippedBlock) {
		this((Block) block.get(), strippedBlock);
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.AXE ? (BlockState) ((Block) strippedStairsBlock.get()).defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(HALF, state.getValue(HALF)).setValue(SHAPE, state.getValue(SHAPE)).setValue(WATERLOGGED, state.getValue(WATERLOGGED)) : super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
