package lotr.common.block;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import lotr.common.event.CompostingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WallHeight;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateHolder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class WickerFenceBlock extends WallBlock implements IForgeBlockState {
	private final Map wickerStateToShapeMap;
	private final Map wickerStateToCollisionShapeMap;

	public WickerFenceBlock() {
		this(Properties.of(Material.DECORATION).strength(0.5F).sound(SoundType.SCAFFOLDING).harvestTool(ToolType.AXE));
	}

	public WickerFenceBlock(Properties properties) {
		super(properties);
		CompostingHelper.prepareCompostable(this, 0.85F);
		wickerStateToShapeMap = makeShapes(1.0F, 1.0F, 16.0F, 0.0F, 13.0F, 16.0F);
		wickerStateToCollisionShapeMap = makeShapes(1.0F, 1.0F, 24.0F, 0.0F, 24.0F, 24.0F);
	}

	public boolean connectsTo(BlockState state, boolean sideSolid, Direction direction) {
		return connectsToSup(state, sideSolid, direction) || state.is(BlockTags.FENCES) && state.is(BlockTags.WOODEN_FENCES) == this.is(BlockTags.WOODEN_FENCES);
	}

	private boolean connectsToSup(BlockState p_220113_1_, boolean p_220113_2_, Direction p_220113_3_) {
		Block block = p_220113_1_.getBlock();
		boolean flag = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(p_220113_1_, p_220113_3_);
		return p_220113_1_.is(BlockTags.WALLS) || !isExceptionForConnection(block) && p_220113_2_ || block instanceof PaneBlock || flag;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return (VoxelShape) wickerStateToCollisionShapeMap.get(state);
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
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return (VoxelShape) wickerStateToShapeMap.get(state);
	}

	public Map makeShapes(float f, float f1, float f2, float f3, float f4, float f5) {
		Map shapes = makeShapesSup(f, f1, f2, f3, f4, f5);
		return ImmutableMap.builder().putAll((Map) shapes.keySet().stream().collect(Collectors.toMap(state -> state, state -> ((VoxelShape) shapes.get(((StateHolder<Block, BlockState>) state).setValue(UP, true)))))).build();
	}

	private Map<BlockState, VoxelShape> makeShapesSup(float p_235624_1_, float p_235624_2_, float p_235624_3_, float p_235624_4_, float p_235624_5_, float p_235624_6_) {
		float f = 8.0F - p_235624_1_;
		float f1 = 8.0F + p_235624_1_;
		float f2 = 8.0F - p_235624_2_;
		float f3 = 8.0F + p_235624_2_;
		VoxelShape voxelshape = Block.box(f, 0.0D, f, f1, p_235624_3_, f1);
		VoxelShape voxelshape1 = Block.box(f2, p_235624_4_, 0.0D, f3, p_235624_5_, f3);
		VoxelShape voxelshape2 = Block.box(f2, p_235624_4_, f2, f3, p_235624_5_, 16.0D);
		VoxelShape voxelshape3 = Block.box(0.0D, p_235624_4_, f2, f3, p_235624_5_, f3);
		VoxelShape voxelshape4 = Block.box(f2, p_235624_4_, f2, 16.0D, p_235624_5_, f3);
		VoxelShape voxelshape5 = Block.box(f2, p_235624_4_, 0.0D, f3, p_235624_6_, f3);
		VoxelShape voxelshape6 = Block.box(f2, p_235624_4_, f2, f3, p_235624_6_, 16.0D);
		VoxelShape voxelshape7 = Block.box(0.0D, p_235624_4_, f2, f3, p_235624_6_, f3);
		VoxelShape voxelshape8 = Block.box(f2, p_235624_4_, f2, 16.0D, p_235624_6_, f3);
		Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

		for (Boolean obool : UP.getPossibleValues()) {
			for (WallHeight wallheight : EAST_WALL.getPossibleValues()) {
				for (WallHeight wallheight1 : NORTH_WALL.getPossibleValues()) {
					for (WallHeight wallheight2 : WEST_WALL.getPossibleValues()) {
						for (WallHeight wallheight3 : SOUTH_WALL.getPossibleValues()) {
							VoxelShape voxelshape9 = VoxelShapes.empty();
							voxelshape9 = applyWallShapeSup(voxelshape9, wallheight, voxelshape4, voxelshape8);
							voxelshape9 = applyWallShapeSup(voxelshape9, wallheight2, voxelshape3, voxelshape7);
							voxelshape9 = applyWallShapeSup(voxelshape9, wallheight1, voxelshape1, voxelshape5);
							voxelshape9 = applyWallShapeSup(voxelshape9, wallheight3, voxelshape2, voxelshape6);
							if (obool) {
								voxelshape9 = VoxelShapes.or(voxelshape9, voxelshape);
							}

							BlockState blockstate = defaultBlockState().setValue(UP, obool).setValue(EAST_WALL, wallheight).setValue(WEST_WALL, wallheight2).setValue(NORTH_WALL, wallheight1).setValue(SOUTH_WALL, wallheight3);
							builder.put(blockstate.setValue(WATERLOGGED, false), voxelshape9);
							builder.put(blockstate.setValue(WATERLOGGED, true), voxelshape9);
						}
					}
				}
			}
		}

		return builder.build();
	}

	private static VoxelShape applyWallShapeSup(VoxelShape p_235631_0_, WallHeight p_235631_1_, VoxelShape p_235631_2_, VoxelShape p_235631_3_) {
		if (p_235631_1_ == WallHeight.TALL) {
			return VoxelShapes.or(p_235631_0_, p_235631_3_);
		}
		return p_235631_1_ == WallHeight.LOW ? VoxelShapes.or(p_235631_0_, p_235631_2_) : p_235631_0_;
	}
}
