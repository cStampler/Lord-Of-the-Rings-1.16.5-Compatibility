package lotr.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRPlanksBlock extends Block implements IForgeBlockState {
	public final MaterialColor planksColor;

	public LOTRPlanksBlock(MaterialColor color) {
		super(Properties.of(Material.WOOD, color).strength(2.0F, 3.0F).sound(SoundType.WOOD));
		planksColor = color;
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
