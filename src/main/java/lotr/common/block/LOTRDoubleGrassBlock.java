package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class LOTRDoubleGrassBlock extends DoublePlantBlock implements IForgeBlockState {
	public LOTRDoubleGrassBlock() {
		this(Properties.of(Material.REPLACEABLE_PLANT).noCollission().strength(0.0F).sound(SoundType.GRASS));
	}

	public LOTRDoubleGrassBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 0.5F);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 100;
	}

	public static void accessRemoveBottomHalf(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoublePlantBlock.preventCreativeDropFromBottomPart(world, pos, state, player);
	}
}
