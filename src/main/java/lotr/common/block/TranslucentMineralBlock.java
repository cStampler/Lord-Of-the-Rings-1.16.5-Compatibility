package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

public class TranslucentMineralBlock extends MineralBlock implements IBeaconBeamColorProvider {
	private final DyeColor beaconBeamColor;

	public TranslucentMineralBlock(Properties properties, int harvestLvl, DyeColor color) {
		super(properties, harvestLvl);
		beaconBeamColor = color;
	}

	@Override
	public DyeColor getColor() {
		return beaconBeamColor;
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidState) {
		return true;
	}
}
