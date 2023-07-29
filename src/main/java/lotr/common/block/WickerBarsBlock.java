package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class WickerBarsBlock extends LOTRBarsBlock implements IForgeBlockState {
	public WickerBarsBlock() {
		this(Properties.of(Material.DECORATION).strength(0.5F).sound(SoundType.SCAFFOLDING).noOcclusion().harvestTool(ToolType.AXE));
	}

	public WickerBarsBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 0.31875002F);
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
