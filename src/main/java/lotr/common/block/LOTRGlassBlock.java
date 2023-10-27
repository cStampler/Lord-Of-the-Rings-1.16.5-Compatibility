package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;

public class LOTRGlassBlock extends GlassBlock {
	public LOTRGlassBlock() {
		this(Properties.of(Material.GLASS).strength(0.3F).sound(SoundType.GLASS).noOcclusion().isValidSpawn(LOTRBlocks::notAllowSpawn).isRedstoneConductor(LOTRBlocks::posPredicateFalse).isSuffocating(LOTRBlocks::posPredicateFalse).isViewBlocking(LOTRBlocks::posPredicateFalse));
	}

	public LOTRGlassBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidState) {
		return true;
	}
}
