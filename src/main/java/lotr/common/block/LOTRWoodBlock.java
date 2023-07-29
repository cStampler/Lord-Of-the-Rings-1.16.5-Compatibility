package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRWoodBlock extends RotatedPillarBlock implements IForgeBlockState {
	public final MaterialColor barkColor;

	public LOTRWoodBlock(MaterialColor bark) {
		super(Properties.of(Material.WOOD, bark).strength(2.0F).sound(SoundType.WOOD));
		barkColor = bark;
	}

	public LOTRWoodBlock(Supplier logBlock) {
		this(((LOTRLogBlock) logBlock.get()).barkColor);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}
}
