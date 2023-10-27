package lotr.common.block;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRSlabBlock extends SlabBlock implements IForgeBlockState {
	private final Block modelBlock;

	public LOTRSlabBlock(Block block) {
		super(Properties.copy(block));
		modelBlock = block;
	}

	public LOTRSlabBlock(Supplier block) {
		this((Block) block.get());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		modelBlock.animateTick(state, world, pos, rand);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}

	public Block getModelBlock() {
		return modelBlock;
	}
}
