package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class WickerFenceGateBlock extends FenceGateBlock implements IForgeBlockState {
	public WickerFenceGateBlock() {
		super(Properties.of(Material.DECORATION).strength(0.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE));
		CompostingHelper.prepareCompostable(this, 0.85F);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}
}
