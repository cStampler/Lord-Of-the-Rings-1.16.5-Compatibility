package lotr.common.block;

import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;

public class DirectionalStoneBlock extends LOTRStoneBlock {
	public static final DirectionProperty HORIZONTAL_FACING;

	static {
		HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	}

	public DirectionalStoneBlock(MaterialColor materialColor) {
		super(materialColor);
		setDirectionalDefaultState();
	}

	public DirectionalStoneBlock(Properties properties) {
		super(properties);
		setDirectionalDefaultState();
	}

	public DirectionalStoneBlock(Supplier blockSup) {
		super(blockSup);
		setDirectionalDefaultState();
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(HORIZONTAL_FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	private void setDirectionalDefaultState() {
		registerDefaultState(stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH));
	}
}
