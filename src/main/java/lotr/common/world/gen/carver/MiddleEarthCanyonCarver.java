package lotr.common.world.gen.carver;

import java.util.Set;

import com.mojang.serialization.Codec;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.gen.carver.CanyonWorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class MiddleEarthCanyonCarver extends CanyonWorldCarver {
	private Set<Block> landOnlyCarvables;

	public MiddleEarthCanyonCarver(Codec<ProbabilityConfig> codec) {
		super(codec);
		replaceableBlocks = LOTRWorldCarvers.listCarvableBlocks();
		landOnlyCarvables = LOTRWorldCarvers.listLandOnlyCarvableBlocks();
	}

	@Override
	protected boolean canReplaceBlock(BlockState state, BlockState aboveState) {
		Block block = state.getBlock();
		return this.canReplaceBlock(state) || landOnlyCarvables.contains(block) && !aboveState.getFluidState().is(FluidTags.WATER);
	}
}
