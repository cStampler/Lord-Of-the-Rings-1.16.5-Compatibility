package lotr.common.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRWoodBarsBlock extends LOTRBarsBlock implements IForgeBlockState {
	public LOTRWoodBarsBlock() {
		this(Properties.of(Material.WOOD, MaterialColor.NONE).strength(2.0F, 3.0F).sound(SoundType.WOOD).noOcclusion());
	}

	public LOTRWoodBarsBlock(Properties properties) {
		super(properties);
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
