package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBeaconBeamColorProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

public class LOTRStainedGlassBlock extends AbstractGlassBlock implements IBeaconBeamColorProvider {
	private final DyeColor glassColor;

	public LOTRStainedGlassBlock(DyeColor color) {
		this(color, Properties.of(Material.GLASS, color).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(LOTRBlocks::notAllowSpawn).isRedstoneConductor(LOTRBlocks::posPredicateFalse).isSuffocating(LOTRBlocks::posPredicateFalse).isViewBlocking(LOTRBlocks::posPredicateFalse));
	}

	public LOTRStainedGlassBlock(DyeColor color, Properties properties) {
		super(properties);
		glassColor = color;
	}

	@Override
	public DyeColor getColor() {
		return glassColor;
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidState) {
		return true;
	}
}
