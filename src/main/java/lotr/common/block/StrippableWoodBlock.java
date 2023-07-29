package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class StrippableWoodBlock extends LOTRWoodBlock {
	private final Supplier strippedWoodBlock;

	public StrippableWoodBlock(MaterialColor bark, Supplier strippedBlock) {
		super(bark);
		strippedWoodBlock = strippedBlock;
	}

	public StrippableWoodBlock(Supplier logBlock, Supplier strippedBlock) {
		this(((LOTRLogBlock) logBlock.get()).barkColor, strippedBlock);
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.AXE ? (BlockState) ((Block) strippedWoodBlock.get()).defaultBlockState().setValue(AXIS, state.getValue(AXIS)) : super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
