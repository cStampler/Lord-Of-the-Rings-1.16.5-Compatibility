package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRFenceGateBlock extends FenceGateBlock implements IForgeBlockState {
	public LOTRFenceGateBlock(Supplier planks) {
		super(Properties.of(Material.WOOD, ((LOTRPlanksBlock) planks.get()).planksColor).strength(2.0F, 3.0F).sound(SoundType.WOOD));
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}
}
