package lotr.common.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LOTRWallBlock extends WallBlock {
	private final Block modelBlock;

	public LOTRWallBlock(Block block) {
		super(Properties.copy(block));
		modelBlock = block;
	}

	public LOTRWallBlock(Supplier block) {
		this((Block) block.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		modelBlock.animateTick(state, world, pos, rand);
	}
}
