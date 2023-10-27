package lotr.common.block;

import java.util.function.Supplier;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class ChalkBlock extends LOTRStoneBlock {
	public ChalkBlock(MaterialColor materialColor, float hard, float res) {
		super(materialColor, hard, res);
	}

	public ChalkBlock(Supplier blockSup) {
		super(blockSup);
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
		return toolType == ToolType.SHOVEL ? ((Block) LOTRBlocks.CHALK_PATH.get()).defaultBlockState() : super.getToolModifiedState(state, world, pos, player, stack, toolType);
	}
}
