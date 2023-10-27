package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class ThatchBlock extends Block implements IForgeBlockState {
	public ThatchBlock() {
		this(MaterialColor.SAND);
	}

	public ThatchBlock(MaterialColor color) {
		this(Properties.of(Material.GRASS, color).strength(0.5F).sound(SoundType.GRASS).harvestTool(ToolType.HOE));
	}

	public ThatchBlock(Properties props) {
		super(props);
		CompostingHelper.prepareCompostable(this, 0.85F);
	}

	@Override
	public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
		doStandardHayFall(world, pos, entity, fallDistance);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}

	public static void doStandardHayFall(World world, BlockPos pos, Entity entity, float fallDistance) {
		entity.causeFallDamage(fallDistance, 0.2F);
	}
}
