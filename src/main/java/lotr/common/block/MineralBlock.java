package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class MineralBlock extends Block {
	private final int oreHarvestLvl;

	public MineralBlock(Properties properties, int harvestLvl) {
		super(properties.requiresCorrectToolForDrops());
		oreHarvestLvl = harvestLvl;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (this == LOTRBlocks.DURNOR_BLOCK.get()) {
			LOTROreBlock.doDurnorParticles(world, pos, rand);
		}

	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return oreHarvestLvl;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return ToolType.PICKAXE;
	}

	@Override
	public boolean isFireSource(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
		return this == LOTRBlocks.DURNOR_BLOCK.get() ? true : super.isFireSource(state, world, pos, side);
	}
}
