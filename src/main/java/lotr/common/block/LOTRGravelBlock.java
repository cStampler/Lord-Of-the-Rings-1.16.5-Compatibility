package lotr.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

public class LOTRGravelBlock extends FallingBlock {
	private final int dustColor;

	public LOTRGravelBlock(MaterialColor materialColor, int dust) {
		this(Properties.of(Material.SAND, materialColor).strength(0.6F).sound(SoundType.GRAVEL).harvestTool(ToolType.SHOVEL), dust);
	}

	public LOTRGravelBlock(Properties properties, int dust) {
		super(properties);
		dustColor = dust;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
		return dustColor;
	}
}
