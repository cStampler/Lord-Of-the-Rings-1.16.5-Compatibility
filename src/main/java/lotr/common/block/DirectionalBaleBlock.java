package lotr.common.block;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class DirectionalBaleBlock extends DirectionalBlock implements IForgeBlockState {
	public DirectionalBaleBlock(MaterialColor color) {
		this(Properties.of(Material.GRASS, color).strength(0.5F).sound(SoundType.GRASS));
	}

	public DirectionalBaleBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
		CompostingHelper.prepareCompostable(this, 0.85F);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FACING);
	}

	@Override
	public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
		ThatchBlock.doStandardHayFall(world, pos, entity, fallDistance);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 20;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}
}
