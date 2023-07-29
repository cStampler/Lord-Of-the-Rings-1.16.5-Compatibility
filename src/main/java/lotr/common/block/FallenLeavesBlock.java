package lotr.common.block;

import java.util.*;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;

public class FallenLeavesBlock extends Block {
	public static final List ALL_FALLEN_LEAVES = new ArrayList();
	private static final Map LEAVES_TO_FALLEN_LEAVES = new HashMap();
	private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
	private final Block baseLeafBlock;

	public FallenLeavesBlock(Block leaf) {
		this(leaf, Properties.of(Material.REPLACEABLE_PLANT).strength(0.2F).sound(SoundType.GRASS).noCollission().noOcclusion().harvestTool(ToolType.HOE));
	}

	public FallenLeavesBlock(Block leaf, Properties properties) {
		super(properties);
		baseLeafBlock = leaf;
		ALL_FALLEN_LEAVES.add(this);
		LEAVES_TO_FALLEN_LEAVES.put(baseLeafBlock, this);
	}

	public FallenLeavesBlock(Supplier leaf) {
		this((Block) leaf.get());
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		return world.getBlockState(belowPos).isFaceSturdy(world, belowPos, Direction.UP) || world.getFluidState(belowPos).getType() == Fluids.WATER;
	}

	public Block getBaseLeafBlock() {
		return baseLeafBlock;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	public static FallenLeavesBlock getFallenLeavesFor(Block leafBlock) {
		return (FallenLeavesBlock) LEAVES_TO_FALLEN_LEAVES.get(leafBlock);
	}
}
