package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;

public class DirectionalMineralBlock extends HorizontalBlock {
	public static final DirectionProperty FACING;
	static {
		FACING = HorizontalBlock.FACING;
	}

	private final int oreHarvestLvl;

	public DirectionalMineralBlock(Properties properties, int harvestLvl) {
		super(properties);
		oreHarvestLvl = harvestLvl;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FACING);
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return oreHarvestLvl;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return ToolType.PICKAXE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
}
