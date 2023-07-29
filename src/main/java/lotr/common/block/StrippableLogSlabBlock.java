package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class StrippableLogSlabBlock extends LogSlabBlock {
	private final Supplier strippedSlabBlock;

	public StrippableLogSlabBlock(Block block, Supplier strippedBlock) {
		super(block);
		strippedSlabBlock = strippedBlock;
	}

	public StrippableLogSlabBlock(Supplier block, Supplier strippedBlock) {
		this((Block) block.get(), strippedBlock);
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.AXE ? (BlockState) ((Block) strippedSlabBlock.get()).defaultBlockState().setValue(TYPE, state.getValue(TYPE)).setValue(getSlabAxisProperty(), state.getValue(getSlabAxisProperty())).setValue(WATERLOGGED, state.getValue(WATERLOGGED)) : super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
