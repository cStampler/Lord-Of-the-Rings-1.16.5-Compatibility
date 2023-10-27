package lotr.common.block;

import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRLogBlock extends RotatedPillarBlock implements IForgeBlockState {
	public final MaterialColor barkColor;
	public final MaterialColor woodColor;

	public LOTRLogBlock(MaterialColor wood, MaterialColor bark) {
		this(Properties.of(Material.WOOD, logStateToMaterialColor(wood, bark)).strength(2.0F).sound(SoundType.WOOD), wood, bark);
	}

	public LOTRLogBlock(Properties properties, MaterialColor bark, MaterialColor wood) {
		super(properties);
		barkColor = bark;
		woodColor = wood;
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 5;
	}

	public static Function logStateToMaterialColor(MaterialColor wood, MaterialColor bark) {
		return state -> (((BlockState) state).getValue(RotatedPillarBlock.AXIS) == Axis.Y ? wood : bark);
	}
}
