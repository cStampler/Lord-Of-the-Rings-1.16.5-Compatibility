package lotr.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HangingWebBlock extends Block {
	public static final EnumProperty WEB_TYPE;

	static {
		WEB_TYPE = LOTRBlockStates.HANGING_WEB_TYPE;
	}

	public HangingWebBlock() {
		this(Properties.of(Material.WEB).noCollission().strength(0.5F));
	}

	public HangingWebBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(WEB_TYPE, HangingWebBlock.Type.SINGLE));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		boolean isPresent = world.getBlockState(pos).getBlock() == this;
		HangingWebBlock.Type type = (HangingWebBlock.Type) state.getValue(WEB_TYPE);
		if (type == HangingWebBlock.Type.SINGLE) {
			return checkSolidSide(world, pos.above(), Direction.DOWN);
		}
		if (type == HangingWebBlock.Type.DOUBLE_TOP) {
			return checkSolidSide(world, pos.above(), Direction.DOWN) && matchType(world, pos.below(), HangingWebBlock.Type.DOUBLE_BOTTOM);
		}
		if (type == HangingWebBlock.Type.DOUBLE_BOTTOM) {
			return isPresent ? matchType(world, pos.above(), HangingWebBlock.Type.DOUBLE_TOP) : matchType(world, pos.above(), HangingWebBlock.Type.SINGLE);
		}
		return true;
	}

	private boolean checkSolidSide(IWorldReader world, BlockPos pos, Direction dir) {
		BlockState state = world.getBlockState(pos);
		return state.is(BlockTags.LEAVES) || Block.isFaceFull(state.getBlockSupportShape(world, pos), dir);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WEB_TYPE);
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (WebBlockHelper.shouldApplyWebSlowness(entity)) {
			entity.makeStuckInBlock(state, new Vector3d(0.75D, 0.5D, 0.75D));
		}

	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return super.getShape(state, world, pos, context);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState placeState = defaultBlockState();
		BlockState doubleBottom = placeState.setValue(WEB_TYPE, HangingWebBlock.Type.DOUBLE_BOTTOM);
		boolean doubleBottomValid = doubleBottom.canSurvive(world, pos);
		if (doubleBottomValid) {
			return doubleBottom;
		}
		BlockState single = placeState.setValue(WEB_TYPE, HangingWebBlock.Type.SINGLE);
		boolean singleValid = single.canSurvive(world, pos);
		return singleValid ? single : null;
	}

	private boolean matchType(IWorldReader world, BlockPos pos, HangingWebBlock.Type type) {
		BlockState state = world.getBlockState(pos);
		return state.getBlock() == this && state.getValue(WEB_TYPE) == type;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		boolean check = false;
		HangingWebBlock.Type type = (HangingWebBlock.Type) state.getValue(WEB_TYPE);
		if (type == HangingWebBlock.Type.SINGLE && facing == Direction.UP) {
			check = true;
		} else if ((type == HangingWebBlock.Type.DOUBLE_TOP || type == HangingWebBlock.Type.DOUBLE_BOTTOM) && (facing == Direction.DOWN || facing == Direction.UP)) {
			check = true;
		}

		if (!check || state.canSurvive(world, currentPos)) {
			return type == HangingWebBlock.Type.SINGLE && facing == Direction.DOWN && matchType(world, currentPos.below(), HangingWebBlock.Type.DOUBLE_BOTTOM) ? (BlockState) state.setValue(WEB_TYPE, HangingWebBlock.Type.DOUBLE_TOP) : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		}
		if (type == HangingWebBlock.Type.DOUBLE_TOP) {
			BlockState singleState = state.setValue(WEB_TYPE, HangingWebBlock.Type.SINGLE);
			if (singleState.canSurvive(world, currentPos)) {
				return singleState;
			}
		}

		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		ItemStack heldItem = player.getMainHandItem();
		if (heldItem.getItem() == asItem()) {
			BlockItemUseContext useContext = new BlockItemUseContext(new ItemUseContext(player, hand, trace));
			HangingWebBlock.Type type = (HangingWebBlock.Type) state.getValue(WEB_TYPE);
			BlockPos placePos = null;
			BlockState placeState = null;
			if (type == HangingWebBlock.Type.SINGLE) {
				placePos = pos.below();
				placeState = defaultBlockState().setValue(WEB_TYPE, HangingWebBlock.Type.DOUBLE_BOTTOM);
			}

			if (placePos != null && placeState != null) {
				boolean canDouble = world.getBlockState(placePos).canBeReplaced(useContext);
				if (canDouble) {
					world.setBlock(placePos, placeState, 3);
					SoundType sound = this.getSoundType(placeState, world, placePos, player);
					world.playSound(player, placePos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
					if (!player.abilities.instabuild) {
						heldItem.shrink(1);
					}

					return ActionResultType.SUCCESS;
				}
			}
		}

		return super.use(state, world, pos, player, hand, trace);
	}

	public enum Type implements IStringSerializable {
		SINGLE("single"), DOUBLE_TOP("double_top"), DOUBLE_BOTTOM("double_bottom");

		private final String typeName;

		Type(String s) {
			typeName = s;
		}

		@Override
		public String getSerializedName() {
			return typeName;
		}
	}
}
