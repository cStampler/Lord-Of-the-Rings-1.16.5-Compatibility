package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class StrippableBranchBlock extends BranchBlock {
	private final Supplier strippedBranchBlock;

	public StrippableBranchBlock(Block block, Supplier strippedBlock) {
		super(block);
		strippedBranchBlock = strippedBlock;
	}

	public StrippableBranchBlock(Supplier block, Supplier strippedBlock) {
		this((Block) block.get(), strippedBlock);
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.AXE ? (BlockState) ((Block) strippedBranchBlock.get()).defaultBlockState().setValue(NORTH, state.getValue(NORTH)).setValue(SOUTH, state.getValue(SOUTH)).setValue(WEST, state.getValue(WEST)).setValue(EAST, state.getValue(EAST)).setValue(WATERLOGGED, state.getValue(WATERLOGGED)) : super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
