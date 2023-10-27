package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class StrippableRottenLogBlock extends RottenLogBlock {
	private final Supplier strippedLogBlock;

	public StrippableRottenLogBlock(MaterialColor wood, MaterialColor bark, Supplier strippedBlock) {
		super(wood, bark);
		strippedLogBlock = strippedBlock;
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.AXE ? (BlockState) ((Block) strippedLogBlock.get()).defaultBlockState().setValue(AXIS, state.getValue(AXIS)).setValue(WATERLOGGED, state.getValue(WATERLOGGED)) : super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
